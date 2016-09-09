package org.mym.plog.formatter;

import android.text.TextUtils;
import android.util.Log;

/**
 * This class can format Throwable into standard trace strings.
 * Created by muyangmin on Sep 09, 2016.
 *
 * @author muyangmin
 * @since 1.5.0
 */
public class ThrowableFormatter implements Formatter {

    /**
     * @param msg    msg to be formatted at the 1st line. This is likely
     *               with{@link android.util.Log#e(String, String, Throwable)}, etc.
     * @param params params to format msg. the 1st argument must be a Throwable instance, and others
     *               are ignored.
     * @throws IllegalArgumentException when argument do not contain a Throwable at 1st position.
     */
    @Override
    public String format(String msg, Object... params) throws Exception {
        if (params.length < 1 || (!(params[0] instanceof Throwable))) {
            throw new IllegalArgumentException("Throwable argument not found!");
        }
        Throwable tr = (Throwable) params[0];
        String stackTrace = Log.getStackTraceString(tr);
        if (TextUtils.isEmpty(msg)) {
            return stackTrace;
        } else {
            return msg + "\n" + stackTrace;
        }
    }

    @Override
    public boolean isPreWrappedFormat() {
        return true;
    }
}
