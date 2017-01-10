package org.mym.plog;

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
public class DebugPrinter implements Printer {

    private boolean isDebug;

    public DebugPrinter(boolean isDebug) {
        this.isDebug = isDebug;
    }

    @Override
    public boolean intercept(@PrintLevel int level, @NonNull String tag,
                             @Nullable Category category, @NonNull String msg) {
        //Intercept none
        return isDebug;
    }

    @Nullable
    @Override
    public Formatter getFormatter() {
        try {
            //noinspection unchecked
            Class<? extends Formatter> clz = ((Class<? extends Formatter>)
                    Class.forName("org.mym.plog.formatter.DefaultFormatter"));
            //Only create a instance for provided dependency
            return clz.newInstance();
        } catch (Exception e) {
            //If formatter module is not included, use null formatter.
            return null;
        }
    }

    @Nullable
    @Override
    public Style getStyle() {
        return null;
    }

    @Override
    public boolean disallowSoftWrap() {
        return false;
    }

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
