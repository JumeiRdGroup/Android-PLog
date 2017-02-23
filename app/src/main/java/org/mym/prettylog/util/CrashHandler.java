package org.mym.prettylog.util;

import android.util.Log;

import org.mym.plog.PLog;
import org.mym.prettylog.CrashPrinter;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by muyangmin on 9/8/16.
 *
 * @author muyangmin
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static volatile CrashHandler sInstance = null;
    Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        //create a temp variable to improve performance for reading volatile field.
        CrashHandler instance = sInstance;
        if (instance == null) {
            synchronized (CrashHandler.class) {
                instance = sInstance;
                //double check here
                if (instance == null) {
                    instance = new CrashHandler();
                    sInstance = instance;
                }
            }
        }
        return instance;
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        //TODO consider remove this on release version
        throwable.printStackTrace();
        CrashPrinter.setExtraInfo(generateExtraInfo(thread));
        PLog.level(Log.ERROR).category(CrashPrinter.CRASH).params(throwable).execute();
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, throwable);
        }

    }

    private String generateExtraInfo(Thread thread) {
        //Print time and thread
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss, z",
                Locale.getDefault());
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("CrashTime: ").append(format.format(System.currentTimeMillis()));
        sb.append("CrashThread: ").append(thread.toString());
        return sb.toString();
    }
}
