package org.mym.plog.formatter;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class is a default implementation for {@link Formatter}, provide all supported types.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
@SuppressWarnings("WeakerAccess")
public class DefaultFormatter implements Formatter {

    public static final int FLAG_FMT_JSON = 1 << 1;
    public static final int FLAG_FMT_POJO = 1 << 2;
    public static final int FLAG_FMT_THROWABLE = 1 << 3;
    public static final int FLAG_FMT_COLLECTION = 1 << 4;

    @IntDef(value = {FLAG_FMT_JSON, FLAG_FMT_POJO, FLAG_FMT_THROWABLE, FLAG_FMT_COLLECTION}, flag
            = true)
    public @interface FormatFlag {

    }

    private int mFormatFlag;

    /**
     * Use a collection to save implementations to avoid redundant implementation.
     */
    private List<FormatterImpl> mFormatterImpls = new ArrayList<>();

    /**
     * Create a formatter with all supported type format enabled.
     */
    public DefaultFormatter() {
        this(true);
    }

    /**
     * Create a formatter which only support specified types.
     *
     * @param enabledTypes types to format.
     */
    public DefaultFormatter(@FormatFlag int... enabledTypes) {
        this(false, enabledTypes);
    }

    private DefaultFormatter(boolean enableAll, @FormatFlag int... enabledTypes) {
        if (enableAll) {
            mFormatFlag = ~0;
        } else {
            for (int enabledType : enabledTypes) {
                mFormatFlag |= enabledType;
            }
        }
        if (BuildConfig.DEBUG) {
            Log.i("DefaultFormatter", "The enabledFlag is " + Integer.toBinaryString(mFormatFlag));
        }
        initFormatter();
    }

    /**
     * Initialize formatter map using type flag passed in constructor.
     */
    private void initFormatter() {
        if (isEnabled(FLAG_FMT_COLLECTION)) {
            mFormatterImpls.add(new FormatterImpl(FormatterImpl.PRIORITY_NORMAL,
                    new CollectionFormatter(), FLAG_FMT_COLLECTION, new Class[]{
                    SparseArray.class, ArrayList.class, HashMap.class
            }));
        }
        if (isEnabled(FLAG_FMT_JSON)) {
            mFormatterImpls.add(new FormatterImpl(FormatterImpl.PRIORITY_NORMAL,
                    new JSONFormatter(), FLAG_FMT_JSON, new Class[]{
                    JSONObject.class, JSONArray.class
            }));
        }
        if (isEnabled(FLAG_FMT_POJO)) {
            //NOTICE: ensure pojo formatter is the lowest priority!
            mFormatterImpls.add(new FormatterImpl(FormatterImpl.PRIORITY_LOW,
                    new ObjectFormatter(), FLAG_FMT_POJO, new Class[]{
                    Object.class
            }));
        }
        if (isEnabled(FLAG_FMT_THROWABLE)) {
            mFormatterImpls.add(new FormatterImpl(FormatterImpl.PRIORITY_NORMAL,
                    new ThrowableFormatter(), FLAG_FMT_THROWABLE, new Class[]{
                    Throwable.class
            }));
        }
        //Ensure all types are sorted by priority
        Collections.sort(mFormatterImpls);
    }

    private boolean isEnabled(@FormatFlag int flag) {
        return (mFormatFlag & flag) != 0;
    }

    @Override
    public String format(String msg, Object... params) throws Exception {
        if (params == null || params.length == 0) {
            return msg;
        }
        String[] formattedParam = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            boolean formatted = false;
            for (FormatterImpl impl : mFormatterImpls) {
                if (isFormatterAvailable(impl.typeFlag, param, impl.supportedClass)){
                    formattedParam[i] = impl.formatter.format("", param);
                    formatted = true;
                    break;
                }
            }
            if (!formatted){
                formattedParam[i] = param.toString();
            }
        }
        return String.format(msg, (Object[]) formattedParam);
    }

    private boolean isFormatterAvailable(@FormatFlag int flag, @NonNull Object param,
                                         @NonNull Class... types) throws Exception {
        //If this formatter is disabled at all, it is always unavailable
        if (!isEnabled(flag)) {
            return false;
        }
        Class<?> paramClz = param.getClass();
        for (Class<?> clz : types) {
            //if clz is equal or super class, the format operation is safe.
            if (clz.isAssignableFrom(paramClz)) {
                return true;
            }
        }
        //all formatter in sparse array iterated, but still not found.
        return false;
    }

    private class FormatterImpl implements Comparable<FormatterImpl> {

        private static final int PRIORITY_LOW = 1;
        private static final int PRIORITY_NORMAL = 2;
        private static final int PRIORITY_HIGH = 3;

        /**
         * Priority to decide use which formatter is preferred.
         */
        private int priority;

        /**
         * the real implementation.
         */
        private Formatter formatter;

        /**
         * associated format flag.
         */
        @FormatFlag
        private int typeFlag;

        /**
         * Supported classes.
         */
        private Class<?>[] supportedClass;

        public FormatterImpl(int priority, @NonNull Formatter formatter, int typeFlag,
                             @NonNull Class<?>[] supportedClass) {
            this.priority = priority;
            this.formatter = formatter;
            this.typeFlag = typeFlag;
            this.supportedClass = supportedClass;
        }

        @Override
        public int compareTo(@NonNull FormatterImpl o) {
            return Integer.valueOf(priority).compareTo(o.priority);
        }
    }

}
