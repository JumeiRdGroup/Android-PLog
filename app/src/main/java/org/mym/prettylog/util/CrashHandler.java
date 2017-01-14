package org.mym.prettylog.util;

import org.mym.plog.PLog;

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
//        PLog.wtf(throwable);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, throwable);
        }

    }
}
