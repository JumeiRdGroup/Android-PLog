package org.mym.plog;

import android.util.Log;

/**
 * Default implementation of {@link Logger}.
 * This class can also be an adapter class : you can just override methods which you want.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class DefaultLogger implements Logger {
    @Override
    public void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    @Override
    public void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    @Override
    public void wtf(String tag, String msg) {
        Log.wtf(tag, msg);
    }
}
