package org.mym.plog;

import android.text.TextUtils;
import android.util.Log;

import org.mym.plog.config.PLogConfig;
import org.mym.plog.formatter.Formatter;
import org.mym.plog.formatter.JSONFormatter;
import org.mym.plog.formatter.ObjectFormatter;
import org.mym.plog.formatter.StringFormatter;
import org.mym.plog.formatter.ThrowableFormatter;
import org.mym.plog.logger.Logger;
import org.mym.plog.util.StackTraceUtil;
import org.mym.plog.util.TimingLogger;

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

    /**
     * 0 dalvik.system.VMStack.getThreadStackTrace(Native Method)       <br/>
     * 1 java.lang.Thread.getStackTrace(Thread.java:579)                <br/>
     * 2 {@link StackTraceUtil#getCurrentStack()}                       <br/>
     * 3 {@link StackTraceUtil#generateAutoTag(int)}/
     * {@link StackTraceUtil#generateStackInfo(boolean, int)}   <br/>
     * 4 {@link #log(int, int, String, Formatter, Logger, String, Object...)}<br/>
     * 5 v, d, i, w, e                                                  <br/>
     * 6 invoker
     */
    private static final int STACK_TRACE_INDEX = 6;
    private static Formatter FMT_JSON = new JSONFormatter();
    private static Formatter FMT_OBJECT = new ObjectFormatter();
    private static Formatter FMT_THROWABLE = new ThrowableFormatter();
    private static Formatter FMT_STRING = new StringFormatter();
    private static PLogConfig mConfig;
    private static TimingLogger mTimingLogger;

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

    /**
     * Add timing split, the meaning of this operation is same as standard TimingLogger.
     */
    public static void addTimingSplit(String splitLabel) {
        mTimingLogger.addSplit(splitLabel);
    }

    /**
     * Dump timing log using current logger.
     */
    public static void dumpTimingToLog() {
        mTimingLogger.dumpToLog();
    }

    /**
     * Reset timing logger. A creation maybe needed if never called timing function.
     *
     * @see android.util.TimingLogger
     */
    public static void resetTimingLogger(String tag, String label) {
        if (mTimingLogger == null) {
            mTimingLogger = createDefaultTimingLogger();
        }
        mTimingLogger.reset(tag, label);
    }

    /**
     * Reset timing logger.
     */
    public static void resetTimingLogger() {
        if (mTimingLogger == null) {
            mTimingLogger = createDefaultTimingLogger();
        }
        mTimingLogger.reset();
    }

    private static TimingLogger createDefaultTimingLogger() {
        return new TimingLogger("PLogTiming", "TimingLabel");
    }

    public static void v(String msg, Object... params) {
        basicLog(Log.VERBOSE, null, msg, params);
    }

    public static void v(String tag, String msg, Object... params) {
        basicLog(Log.VERBOSE, tag, msg, params);
    }

    public static void d(String msg, Object... params) {
        basicLog(Log.DEBUG, null, msg, params);
    }

    public static void d(String tag, String msg, Object... params) {
        basicLog(Log.DEBUG, tag, msg, params);
    }

    public static void i(String msg, Object... params) {
        basicLog(Log.INFO, null, msg, params);
    }

    public static void i(String tag, String msg, Object... params) {
        basicLog(Log.INFO, tag, msg, params);
    }

    public static void w(String msg, Object... params) {
        basicLog(Log.WARN, null, msg, params);
    }

    public static void w(String tag, String msg, Object... params) {
        basicLog(Log.WARN, tag, msg, params);
    }

    public static void e(String msg, Object... params) {
        basicLog(Log.ERROR, null, msg, params);
    }

    public static void e(String tag, String msg, Object... params) {
        basicLog(Log.ERROR, tag, msg, params);
    }

    private static void basicLog(int level, String tag, String msg, Object... params) {
        log(level, 1, tag, FMT_STRING, null, msg, params);
    }

    /**
     * Print {@link PLogConfig#getEmptyMsg()}, using DEBUG Level,
     * or {@link PLogConfig#getEmptyMsgLevel()} if specified.
     */
    public static void empty() {
        log(mConfig.getEmptyMsgLevel(), 0, null, new StringFormatter(), null,
                mConfig.getEmptyMsg());
    }

    /**
     * Use this method for skipping some middle methods, if necessary.
     * <p><strong>Note: This method is often not recommend; v,d,i,w,e methods is enough for common
     * use.</strong></p>
     *
     * @param level       log level, MUST be one of
     *                    {@link Log#VERBOSE}, {@link Log#DEBUG}, {@link Log#INFO},
     *                    {@link Log#WARN}, {@link Log#ERROR}.
     * @param stackOffset stack offset, often pass an non-negative integer, the default log is 0.
     * @param tag         log tag.
     * @param msg         log message.
     * @param params      log params, optional.
     */
    public static void logWithStackOffset(int level, int stackOffset, String tag, String msg,
                                          Object... params) {
        //Just transfer to log() method, for keep same layer level with v,d,i,w,e methods.
        log(level, stackOffset, tag, FMT_STRING, mConfig.getLogger(), msg, params);
    }

    /**
     * A helper method useful when you just want to print objects using default format.
     * The log level for this method is defined as {@link Log#INFO}.
     *
     * @param params objects to print.
     */
    public static void objects(Object... params) {
        log(Log.DEBUG, 0, null, FMT_OBJECT, null, null, params);
    }

    /**
     * A helper method useful when you just want to print objects using default format.
     *
     * @param level  log level.
     * @param params objects to print.
     */
    public static void objects(int level, Object... params) {
        log(level, 0, null, FMT_OBJECT, null, null, params);
    }

    /**
     * Print json string. <br>
     * NOTE: Only one json string is allowed on each call.
     *
     * @since 1.5.0
     */
    public static void json(String msg) {
        log(Log.DEBUG, 0, null, FMT_JSON, null, msg);
    }

    /**
     * Print json string. <br>
     * NOTE: It is strongly recommended to call this method instead of objects, because the internal
     * implementation may be changed in later versions, and cannot promise compatibility of JSON
     * usage.
     *
     * @since 1.5.0
     */
    public static void json(int level, String msg) {
        log(level, 0, null, FMT_JSON, null, null, msg);
    }

    /**
     * Print exceptions in WARN level.
     */
    public static void throwable(Throwable throwable) {
        log(Log.WARN, 0, null, new ThrowableFormatter(), null, null, throwable);
    }

    /**
     * Print exceptions in specified level.
     */
    public static void throwable(int level, String msg, Throwable throwable) {
        log(level, 0, null, new ThrowableFormatter(), null, msg, throwable);
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     */
    public static void wtf(Throwable throwable) {
        log(Log.ERROR, 0, null, new ThrowableFormatter(), null, null, throwable);
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     */
    public static void wtf(int level, String msg, Throwable throwable) {
        log(Log.ERROR, 0, null, new ThrowableFormatter(), null, msg, throwable);
    }

    /**
     * Core Implementation.
     * If this log is considered as loggable by controller, then wrap log content by config and
     * print log using specified formatter and logger.
     *
     * @param level       log level
     * @param stackOffset how many level PLog class is wrapped
     * @param tag         log tag
     * @param formatter   formatter to decide how to wrap line and helper strings, not null
     * @param logger      logger to decide where to write.If null, mConfig.getLogger is used.
     * @param msg         original log message, may be null
     * @param params      original params for format; may be empty
     */
    private static void log(int level, int stackOffset, String tag,
                            Formatter formatter, Logger logger,
                            String msg, Object... params) {
        //Keep safe
        checkInitOrUseDefaultConfig();

        if (logger == null) {
            logger = mConfig.getLogger();
        }

        //Checking for auto tag
        if (TextUtils.isEmpty(tag) && mConfig.isUseAutoTag()) {
            int offset = STACK_TRACE_INDEX + mConfig.getGlobalStackOffset() + stackOffset;
            tag = StackTraceUtil.generateAutoTag(offset);
        }
        //Only concat when tag is not empty and config is specified to true
        if ((!TextUtils.isEmpty(tag)) && mConfig.isForceConcatGlobalTag()) {
            tag = mConfig.getGlobalTag() + "-" + tag;
        }
        //If still empty, using global
        else if (TextUtils.isEmpty(tag)) {
            tag = mConfig.getGlobalTag();
        }

        //Call controller
        if (!mConfig.getController().isLogEnabled(level, tag, msg)) {
            return;
        }

        //Format log content
        String logContent;
        boolean isFormattedCorrectly = false;
        try {
            logContent = formatter.format(msg, params);
            isFormattedCorrectly = true;
        } catch (Exception ignored) {
            logContent = msg;
        }

        //wrap line
        if ((!isFormattedCorrectly) || (!formatter.isPreWrappedFormat())) {
            logContent = wrapLine(logContent, mConfig.getMaxLengthPerLine());
        }

        //insert line number if allowed
        String lineInfo = null;
        if (mConfig.isKeepLineNumber()) {
            int offsetFromZero = STACK_TRACE_INDEX + mConfig.getGlobalStackOffset() + stackOffset;
            lineInfo = StackTraceUtil.generateStackInfo(mConfig.isKeepInnerClass(),
                    offsetFromZero);
        }
        //This condition test is for exception case(e.g. wrong call);
        // Just leave them here, do not delete
        if (logContent != null && lineInfo != null) {
            if (logContent.indexOf('\n') != -1) {
                //Assume multi line
                logContent = lineInfo + "\n" + logContent;
            } else {
                logContent = lineInfo + logContent;
            }
        }
        //But lineInfo maybe null if user disabled keepLineNumber feature.
        //So call to logger cannot be put in the if block above.
        if (logContent != null) {
            callLoggerPrint(level, tag, logContent, logger);
        }
    }

    /**
     * Soft wrap line rule implementation.
     *
     * @param logContent       log to be printed
     * @param maxLengthPerLine max length
     * @return wrapped log
     */
    private static String wrapLine(String logContent, int maxLengthPerLine) {
        int currentIndex = 0;
        //Use a StringBuilder to build multi line text but print only once, solve #6
        StringBuilder sb = new StringBuilder(logContent.length()
                + logContent.length() / maxLengthPerLine); //plus \n symbol
        while (currentIndex < logContent.length()) {
            //compute max length of this line
            int currentLineLength = Math.min(mConfig.getMaxLengthPerLine(),
                    logContent.length() - currentIndex);

            //Force new line if \n appears, otherwise use our soft wrap.
            String subLine;

            int newlineIndex = logContent.indexOf("\n", currentIndex);
            int thisLineEnd = currentIndex + currentLineLength;

            //has \n in this line;
            if (newlineIndex != -1 && newlineIndex < thisLineEnd) {
                subLine = logContent.substring(currentIndex, newlineIndex);
                currentIndex = newlineIndex + 1;
            } else {
                subLine = logContent.substring(currentIndex, thisLineEnd);
                currentIndex = thisLineEnd;
            }

            //Not print yet, only append.
            sb.append(subLine);
            //Has more chars
            if (currentIndex < logContent.length()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static void callLoggerPrint(int level, String tag, String logContent, Logger logger) {
        switch (level) {
            case Log.VERBOSE:
                logger.v(tag, logContent);
                break;
            case Log.DEBUG:
                logger.d(tag, logContent);
                break;
            case Log.INFO:
                logger.i(tag, logContent);
                break;
            case Log.WARN:
                logger.w(tag, logContent);
                break;
            case Log.ERROR:
                logger.e(tag, logContent);
                break;
        }
    }

    private static void checkInitOrUseDefaultConfig() {
        if (mConfig == null) {
            init(new PLogConfig.Builder().build());
        }
    }
}
