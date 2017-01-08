package org.mym.plog.printer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.mym.plog.Category;
import org.mym.plog.PrintLevel;
import org.mym.plog.Printer;
import org.mym.plog.formatter.DefaultFormatter;
import org.mym.plog.formatter.Formatter;

/**
 * Print all message to logcat.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class DebugPrinter implements Printer {
    @Override
    public boolean intercept(@PrintLevel int level, @Nullable Category category, @NonNull String
            msg) {
        //Intercept none
        return false;
    }

    @Nullable
    @Override
    public Formatter getFormatter() {
        try {
            Class.forName("org.mym.plog.formatter.DefaultFormatter");
            //Only create a instance for provided dependency
            return new DefaultFormatter();
        }catch (ClassNotFoundException e){
            //If formatter module is not included, use null formatter.
            return null;
        }
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
