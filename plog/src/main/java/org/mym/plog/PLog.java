package org.mym.plog;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;
import org.mym.plog.config.PLogConfig;
//import org.mym.plog.formatter.StringFormatter;
//import org.mym.plog.logger.Logger;


/**
 * Entry class of log module, settings, and init configs are all here.
 * <p>You don't need to create this class since it is only an utility class, for configs,
 * please use {@link #init(PLogConfig)}. <br>
 * Also, it is strongly recommended to create config instance using
 * {@link org.mym.plog.config.PLogConfig.Builder}, instead of directly call constructor.
 * </p>
 *
 * @author Muyangmin
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class PLog {
//
//    /**
//     * 0 dalvik.system.VMStack.getThreadStackTrace(Native Method)       <br/>
//     * 1 java.lang.Thread.getStackTrace(Thread.java:579)                <br/>
//     * 2 {@link StackTraceUtil#getCurrentStack()}                       <br/>
//     * 3 {@link StackTraceUtil#generateAutoTag(int)}/
//     * {@link StackTraceUtil#generateStackInfo(boolean, int)}   <br/>
//     * 4 {@link #log(int, int, String, Formatter, Logger, String, Object...)}<br/>
//     * 5 v, d, i, w, e                                                  <br/>
//     * 6 invoker
//     */
//    private static final int STACK_TRACE_INDEX = 6;
//    private static Formatter FMT_JSON = new JSONFormatter();
//    private static Formatter FMT_OBJECT = new ObjectFormatter();
//    private static Formatter FMT_THROWABLE = new ThrowableFormatter();
//    private static Formatter FMT_STRING = new StringFormatter();
    private static PLogConfig mConfig;

    //The constructor of this class is meaningless, so make it private
    private PLog() {
        //Empty
    }

    /**
     * Check and set {@link #mConfig} field. this operation guarantee the reading of config for
     * later operation is safe.
     */
    private static synchronized void safelySetConfig(PLogConfig config) throws RuntimeException {
        PLogConfig.checkConfigSafe(config);
        mConfig = config;
    }

    /**
     * Init PLog using customized config.
     */
    public static void init(PLogConfig config) {
        safelySetConfig(config);
    }

    /**
     * Get current config; maybe this is useful for temporarily change config and backup then.
     * Another scenario is to debug this library.
     *
     * @return Current config; or default config if {@link #init(PLogConfig)} is not called yet.
     * @see #init(PLogConfig)
     * @since 1.3.0
     */
    public static PLogConfig getCurrentConfig() {
        checkInitOrUseDefaultConfig();
        return mConfig;
    }

    public static LogRequest level(@PrintLevel int level){
        return new LogRequest().level(level);
    }

    public static LogRequest tag(@NonNull String tag){
        return new LogRequest().tag(tag);
    }

    public static void v(String msg, Object... params) {
        //TODO implement a pool of logRequest
        new LogRequest().level(Log.VERBOSE).msg(msg).params(params).execute();
    }

    public static void d(String msg, Object... params) {
        new LogRequest().level(Log.DEBUG).msg(msg).params(params).execute();
    }

    public static void i(String msg, Object... params) {
        new LogRequest().level(Log.INFO).msg(msg).params(params).execute();
    }

    public static void w(String msg, Object... params) {
        new LogRequest().level(Log.WARN).msg(msg).params(params).execute();
    }

    public static void e(String msg, Object... params) {
        new LogRequest().level(Log.ERROR).msg(msg).params(params).execute();
    }

    /**
     * Print {@link PLogConfig#getEmptyMsg()}, using DEBUG Level,
     * or {@link PLogConfig#getEmptyMsgLevel()} if specified.
     */
    public static void empty() {
        new LogRequest().level(mConfig.getEmptyMsgLevel()).execute();
    }

    /**
     * A helper method useful when you just want to print objects using default format.
     * The log level for this method is defined as {@link Log#INFO}.
     *
     * @param params objects to print.
     */
    public static void objects(Object... params) {
        new LogRequest().level(Log.DEBUG).params(params).execute();
    }

    /**
     * Print json string.
     * @since 2.0.0
     */
    public static void json(JSONObject jsonObject) {
        new LogRequest().level(Log.DEBUG).params(jsonObject).execute();
    }

    /**
     * Print exceptions in WARN level.
     */
    public static void throwable(Throwable throwable) {
        new LogRequest().level(Log.WARN).params(throwable).execute();
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     */
    public static void wtf(Throwable throwable) {
        new LogRequest().level(Log.ERROR).params(throwable).execute();
    }

    private static void checkInitOrUseDefaultConfig() {
        if (mConfig == null) {
            init(new PLogConfig.Builder().build());
        }
    }
//
//    /**
//     * Core Implementation.
//     * If this log is considered as loggable by controller, then wrap log content by config and
//     * print log using specified formatter and logger.
//     *
//     * @param level       log level
//     * @param stackOffset how many level PLog class is wrapped
//     * @param tag         log tag
//     * @param formatter   formatter to decide how to wrap line and helper strings, not null
//     * @param logger      logger to decide where to write.If null, mConfig.getLogger is used.
//     * @param msg         original log message, may be null
//     * @param params      original params for format; may be empty
//     */
//    private static void log(int level, int stackOffset, String tag,
//                            Formatter formatter, Logger logger,
//                            String msg, Object... params) {
//        //Keep safe
//        checkInitOrUseDefaultConfig();
//
//        if (logger == null) {
//            logger = mConfig.getLogger();
//        }
//        //Checking null calls
//        if (TextUtils.isEmpty(msg) && params.length == 0) {
//            PLog.w("You're logging with empty msg and arguments, output would be force change to " +
//                    "empty msg in empty msg level !!");
//            level = mConfig.getEmptyMsgLevel();
//            msg = mConfig.getEmptyMsg();
//        }
//
//        //Checking for auto tag
//        if (TextUtils.isEmpty(tag) && mConfig.isUseAutoTag()) {
//            int offset = STACK_TRACE_INDEX + mConfig.getGlobalStackOffset() + stackOffset;
//            tag = StackTraceUtil.generateAutoTag(offset);
//        }
//        //Only concat when tag is not empty and config is specified to true
//        if ((!TextUtils.isEmpty(tag)) && mConfig.isForceConcatGlobalTag()) {
//            tag = mConfig.getGlobalTag() + "-" + tag;
//        }
//        //If still empty, using global
//        else if (TextUtils.isEmpty(tag)) {
//            tag = mConfig.getGlobalTag();
//        }
//
//        //Call controller
//        if (!mConfig.getController().isLogEnabled(level, tag, msg)) {
//            return;
//        }
//
//        //Format log content
//        /**
//         * IMPORTANT: Formatter can accept empty msg argument, see doc in {@link Formatter}.
//         */
//        String logContent;
//        boolean isFormattedCorrectly = false;
//        try {
//            logContent = formatter.format(msg, params);
//            isFormattedCorrectly = true;
//        } catch (Exception ignored) {
//            logContent = msg;
//        }
//
//        //wrap line
//        /**
//         * IMPORTANT: If formatted string is empty, it shouldn't do soft wrap line operation;
//         * doing that would lead to a NullPointerException.
//         * so do a check before call of wrapLine().
//         */
//        if ((logContent != null)
//                && ((!isFormattedCorrectly) || (!formatter.isPreWrappedFormat()))) {
//            logContent = wrapLine(logContent, mConfig.getMaxLengthPerLine());
//        }
//
//        //insert line number if allowed
//        String lineInfo = null;
//        if (mConfig.isKeepLineNumber()) {
//            int offsetFromZero = STACK_TRACE_INDEX + mConfig.getGlobalStackOffset() + stackOffset;
//            lineInfo = StackTraceUtil.generateStackInfo(mConfig.isKeepInnerClass(),
//                    offsetFromZero);
//        }
//        //This condition test is for exception case(e.g. wrong call);
//        // Just leave them here, do not delete
//        if (logContent != null && lineInfo != null) {
//            if (logContent.indexOf('\n') != -1) {
//                //Assume multi line
//                logContent = lineInfo + "\n" + logContent;
//            } else {
//                logContent = lineInfo + logContent;
//            }
//        }
//        //But lineInfo maybe null if user disabled keepLineNumber feature.
//        //So call to logger cannot be put in the if block above.
//        if (logContent != null) {
//            callLoggerPrint(level, tag, logContent, logger);
//        }
//    }
//
//    /**
//     * Soft wrap line rule implementation.
//     *
//     * @param logContent       log to be printed, NOT NULL
//     * @param maxLengthPerLine max length
//     * @return wrapped log
//     */
//    private static String wrapLine(String logContent, int maxLengthPerLine) {
//        //Safety Check
//        assert logContent != null;
//
//        if (logContent.isEmpty()) { // Not need to to StringBuilder and while loop
//            return logContent;
//        }
//
//        int currentIndex = 0;
//        //Use a StringBuilder to build multi line text but print only once, solve #6
//        StringBuilder sb = new StringBuilder(logContent.length()
//                + logContent.length() / maxLengthPerLine); //plus \n symbol
//        while (currentIndex < logContent.length()) {
//            //compute max length of this line
//            int currentLineLength = Math.min(mConfig.getMaxLengthPerLine(),
//                    logContent.length() - currentIndex);
//
//            //Force new line if \n appears, otherwise use our soft wrap.
//            String subLine;
//
//            int newlineIndex = logContent.indexOf("\n", currentIndex);
//            int thisLineEnd = currentIndex + currentLineLength;
//
//            //has \n in this line;
//            if (newlineIndex != -1 && newlineIndex < thisLineEnd) {
//                subLine = logContent.substring(currentIndex, newlineIndex);
//                currentIndex = newlineIndex + 1;
//            } else {
//                subLine = logContent.substring(currentIndex, thisLineEnd);
//                currentIndex = thisLineEnd;
//            }
//
//            //Not print yet, only append.
//            sb.append(subLine);
//            //Has more chars
//            if (currentIndex < logContent.length()) {
//                sb.append("\n");
//            }
//        }
//        return sb.toString();
//    }
//
//    private static void callLoggerPrint(int level, String tag, String logContent, Logger logger) {
//        switch (level) {
//            case Log.VERBOSE:
//                logger.v(tag, logContent);
//                break;
//            case Log.DEBUG:
//                logger.d(tag, logContent);
//                break;
//            case Log.INFO:
//                logger.i(tag, logContent);
//                break;
//            case Log.WARN:
//                logger.w(tag, logContent);
//                break;
//            case Log.ERROR:
//                logger.e(tag, logContent);
//                break;
//        }
//    }

    /**
     * Prepare printers; this method should always be called on application start because you should
     * set your own onIntercept logic using {@link Printer} interface.
     * <p>
     * Sample usage: <br>
     * <code>
     * PLog.prepare(new DebugPrinter(BuildConfig.DEBUG));
     * </code>
     * </p>
     *
     * @param printers printers to print logs; they are parallel from each other.
     */
    public static void prepare(Printer... printers) {
        LogEngine.setPrinters(printers);
    }
}
