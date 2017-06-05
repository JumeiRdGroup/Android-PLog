package org.mym.plog;

import android.annotation.SuppressLint;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Print all message to logcat.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DebugPrinter extends AbsPrinter {

    private boolean isDebug;

    public DebugPrinter(boolean isDebug) {
        this(isDebug, 100);
    }

    public DebugPrinter(boolean isDebug, int maxLengthPerLine) {
        super(maxLengthPerLine);
        this.isDebug = isDebug;
    }

    @CheckResult
    @Override
    public boolean onIntercept(@PrintLevel int level, @NonNull String tag,
                               @Nullable Category category, @NonNull String msg) {
        //Intercept all logs if not in debug
        return !isDebug || super.onIntercept(level, tag, category, msg);
    }

    @SuppressLint("LogNotPLog")
    @Override
    public void print(@PrintLevel int level, @NonNull String tag, @NonNull String msg) {
        switch (level) {
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.ASSERT:
                Log.wtf(tag, msg);
                break;
        }
    }
}
