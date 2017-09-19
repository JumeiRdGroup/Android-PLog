package org.mym.plog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * An abstract implementation for printer，Only default initialization and get/set methods are
 * implemented.
 *
 * @since 2.0.0-beta2
 */
public abstract class AbsPrinter implements Printer {

    private Formatter mFormatter;

    private SoftWrapper mSoftWrapper;

    private Style mStyle;

    private Interceptor mInterceptor;

    private int mMaxLengthPerLine;

    public AbsPrinter() {
        this(0);
    }

    public AbsPrinter(int maxLengthPerLine) {
        mMaxLengthPerLine = maxLengthPerLine;
        doDefaultInit();
    }

    private void doDefaultInit() {
        try {
            //noinspection unchecked
            Class<? extends Formatter> clz = ((Class<? extends Formatter>)
                    Class.forName("org.mym.plog.formatter.DefaultFormatter"));
            //Only create a instance for provided dependency
            mFormatter = clz.newInstance();
        } catch (Exception e) {
            //If formatter module is not included, use null formatter.
            mFormatter = null;
        }
    }

    @Nullable
    @Override
    public Formatter getFormatter() {
        return mFormatter;
    }

    public void setFormatter(Formatter formatter) {
        mFormatter = formatter;
    }

    @Override
    public SoftWrapper getSoftWrapper() {
        return mSoftWrapper;
    }

    public void setSoftWrapper(SoftWrapper softWrapper) {
        mSoftWrapper = softWrapper;
    }

    @Nullable
    @Override
    public Style getStyle() {
        return mStyle;
    }

    public void setStyle(Style style) {
        mStyle = style;
    }

    @Override
    public int getMaxLengthPerLine() {
        return mMaxLengthPerLine;
    }

    @Override
    public boolean onIntercept(@PrintLevel int level, @NonNull String tag,
                               @Nullable Category category, @NonNull String msg) {
        return mInterceptor != null && mInterceptor.onIntercept(level, tag, category, msg);
    }

    public void setInterceptor(Interceptor interceptor) {
        mInterceptor = interceptor;
    }

}
