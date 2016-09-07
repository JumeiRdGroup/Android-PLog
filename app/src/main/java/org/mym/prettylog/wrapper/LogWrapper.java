package org.mym.prettylog.wrapper;

import android.content.Context;

import org.mym.plog.PLog;
import org.mym.plog.config.EasyLogController;
import org.mym.plog.config.PLogConfig;

/**
 * <p>
 * This class is a sample for how to wrap the log library for extensibility or replace library.
 * But I still recommend you to continuously use PLog library :)
 * This file is published on GitHub Gist; you can find it in my public gist list:
 * https://gist.github.com/Muyangmin
 * </p>
 * Created by muyangmin on 9/7/16.
 *
 * @author muyangmin
 * @since V1.3.0
 */
@SuppressWarnings("unused")
public class LogWrapper {

    private static IAppLogger sLogger;

    /**
     * This context param is not necessary for PLog; however I cannot promise other library
     * does not need it.
     * @param context should be application context
     */
    public static void init(Context context){
        sLogger = LogFactory.createLogger();
        sLogger.init(context);
    }

    public static void addTimingSplit(String label) {
        sLogger.addTimingSplit(label);
    }

    public static void objects(Object... objects) {
        sLogger.objects(objects);
    }

    public static void i(String msg, Object... obj) {
        sLogger.i(msg, obj);
    }

    public static void resetTiming(String tag, String label) {
        sLogger.resetTiming(tag, label);
    }

    public static void d(String msg, Object... obj) {
        sLogger.d(msg, obj);
    }

    public static void w(String msg, Object... obj) {
        sLogger.w(msg, obj);
    }

    public static void resetTiming() {
        sLogger.resetTiming();
    }

    public static void i(String tag, String msg, Object... obj) {
        sLogger.i(tag, msg, obj);
    }

    public static void empty() {
        sLogger.empty();
    }

    public static void v(String msg, Object... obj) {
        sLogger.v(msg, obj);
    }

    public static void e(String msg, Object... obj) {
        sLogger.e(msg, obj);
    }

    public static void dumpTiming() {
        sLogger.dumpTiming();
    }

    private interface IAppLogger {

        /**
         * This context param is not necessary for PLog; however I cannot promise other library
         * does not need it.
         * @param context should be application context
         */
        void init(Context context);

        void v(String msg, Object... obj);
        void d(String msg, Object... obj);
        void i(String msg, Object... obj);
        void w(String msg, Object... obj);
        void e(String msg, Object... obj);

        void i(String tag, String msg, Object... obj); //other method contains tag param is omitted

        void objects(Object... objects);

        void empty();

        //For timing
        void resetTiming();
        void resetTiming(String tag, String label);
        void addTimingSplit(String splitLabel);
        void dumpTiming();
    }

    private static class LogFactory {
        public static IAppLogger createLogger(){
            return new PLogImpl();
        }
    }

    private static class PLogImpl implements IAppLogger {
        @Override
        public void addTimingSplit(String splitLabel) {
            PLog.addTimingSplit(splitLabel);
        }

        @Override
        public void init(Context context) {
            PLog.init(PLogConfig.newBuilder()
                    .useAutoTag(true)
                    .keepLineNumber(true)
                    .keepInnerClass(true)
                    .forceConcatGlobalTag(true)
                    .maxLengthPerLine(160)
                    //TODO replace your BuildConfig.DEBUG here
                    .controller(new EasyLogController(true, true))
                    .build());
        }

        @Override
        public void v(String msg, Object... obj) {
            PLog.v(msg, obj);
        }

        @Override
        public void d(String msg, Object... obj) {
            PLog.d(msg, obj);
        }

        @Override
        public void i(String msg, Object... obj) {
            PLog.i(msg, obj);
        }

        @Override
        public void w(String msg, Object... obj) {
            PLog.w(msg, obj);
        }

        @Override
        public void e(String msg, Object... obj) {
            PLog.e(msg, obj);
        }

        @Override
        public void i(String tag, String msg, Object... obj) {
            PLog.i(tag, msg, obj);
        }

        @Override
        public void objects(Object... objects) {
            PLog.objects(objects);
        }

        @Override
        public void empty() {
            PLog.empty();
        }

        @Override
        public void resetTiming() {
            PLog.resetTimingLogger();
        }

        @Override
        public void resetTiming(String tag, String label) {
            PLog.resetTimingLogger(tag, label);
        }

        @Override
        public void dumpTiming() {
            PLog.dumpTimingToLog();
        }
    }
}
