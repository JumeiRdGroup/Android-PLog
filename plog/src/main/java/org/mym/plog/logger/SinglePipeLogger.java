package org.mym.plog.logger;

import android.util.Log;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static android.util.Log.WARN;

/**
 * <p>
 * This class is an implementation for those who want to use only one output stream for any level
 * log. Considering clients may want to use level to generate wrapper string, the
 * {@link #log(int, String, String)} method provides a parameter to indicate log level.
 * </p>
 * Created by muyangmin on 9/6/16.
 *
 * @author muyangmin
 * @since V3.94
 */
public abstract class SinglePipeLogger implements Logger {
    /**
     * This method is for those who wants to keep double output pipes for debugging.
     */
    @SuppressWarnings("unused")
    protected static void callDefaultLogger(int level, String tag, String msg) {
        switch (level) {
            case VERBOSE:
                Log.v(tag, msg);
                break;
            case DEBUG:
                Log.d(tag, msg);
                break;
            case INFO:
                Log.i(tag, msg);
                break;
            case WARN:
                Log.w(tag, msg);
                break;
            case ERROR:
                Log.e(tag, msg);
                break;
        }
    }

    @Override
    public void d(String tag, String msg) {
        log(DEBUG, tag, msg);
    }

    @Override
    public void v(String tag, String msg) {
        log(VERBOSE, tag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        log(INFO, tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        log(WARN, tag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        log(ERROR, tag, msg);
    }

    protected abstract void log(int level, String tag, String msg);
}
