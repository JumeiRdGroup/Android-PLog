package org.mym.plog;

import android.text.TextUtils;
import android.util.Log;

import org.mym.plog.config.Logger;
import org.mym.plog.config.PLogConfig;
import org.mym.plog.util.ObjectUtil;
import org.mym.plog.util.TimingLogger;

/**
 * Entry class of log module, settings, and init configs are all here.
 *
 * <p>You don't need to create this class since it is only an utility class, for configs,
 * please use {@link #init(PLogConfig)}. <br>
 * Also, it is strongly recommended to create config instance using
 * {@link org.mym.plog.config.PLogConfig.Builder}, instead of directly call constructor.
 * </p>
 * @author Muyangmin
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class PLog {

    /**
     * 0 dalvik.system.VMStack.getThreadStackTrace(Native Method)       <br/>
     * 1 java.lang.Thread.getStackTrace(Thread.java:579)                <br/>
     * 2 {@link #getLineNumAndMethodName(int)}                             <br/>
     * 3 {@link #wrapLogStr(int, String, Object...)}                         <br/>
     * 4 {@link #log(int, int, String, String, Object...)}                   <br/>
     * 5 v, d, i, w, e                                                  <br/>
     * 6 invoker
     */
    private static final int STACK_TRACE_INDEX = 6;

    private static PLogConfig mConfig;
    private static TimingLogger mTimingLogger;

    //The constructor of this class is meaningless, so make it private
    private PLog(){
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
     * @return Current config; or default config if {@link #init(PLogConfig)} is not called yet.
     * @see #init(PLogConfig)
     * @since 1.3.0
     */
    public static PLogConfig getCurrentConfig(){
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
     * Reset timing logger. A creation maybe needed if never called timing fucntion.
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
        log(Log.VERBOSE, 0, null, msg, params);
    }

    public static void v(String tag, String msg, Object... params) {
        log(Log.VERBOSE, 0, tag, msg, params);
    }

    public static void d(String msg, Object... params) {
        log(Log.DEBUG, 0, null, msg, params);
    }

    public static void d(String tag, String msg, Object... params) {
        log(Log.DEBUG, 0, tag, msg, params);
    }

    public static void i(String msg, Object... params) {
        log(Log.INFO, 0, null, msg, params);
    }

    public static void i(String tag, String msg, Object... params) {
        log(Log.INFO, 0, tag, msg, params);
    }

    public static void w(String msg, Object... params) {
        log(Log.WARN, 0, null, msg, params);
    }

    public static void w(String tag, String msg, Object... params) {
        log(Log.WARN, 0, tag, msg, params);
    }

    public static void e(String msg, Object... params) {
        log(Log.ERROR, 0, null, msg, params);
    }

    public static void e(String tag, String msg, Object... params) {
        log(Log.ERROR, 0, tag, msg, params);
    }

    /**
     * Print {@link PLogConfig#getEmptyMsg()}, using DEBUG Level,
     * or {@link PLogConfig#getEmptyMsgLevel()} if specified.
     */
    public static void empty() {
        int level = mConfig == null ? Log.DEBUG : mConfig.getEmptyMsgLevel();
        log(level, 0, null, null);
    }

    /**
     * Use this method for skipping some middle methods, if necessary.
     * <p><strong>Note: This method is often not recommend; v,d,i,w,e methods is enough for common
     * use.</strong></p>
     * @param level log level, MUST be one of
     *              {@link Log#VERBOSE}, {@link Log#DEBUG}, {@link Log#INFO},
     *              {@link Log#WARN}, {@link Log#ERROR}.
     * @param stackOffset stack offset, often pass an non-negative integer, the default log is 0.
     * @param tag log tag.
     * @param msg log message.
     * @param params log params, optional.
     */
    public static void logWithStackOffset(int level, int stackOffset, String tag, String msg,
                                          Object... params){
        //Just transfer to log() method, for keep same layer level with v,d,i,w,e methods.
        log(level, stackOffset, tag, msg, params);
    }

    /**
     * A helper method useful when you just want to print objects using default format.
     * The log level for this method is defined as {@link Log#INFO}.
     * @param params objects to print.
     */
    public static void objects(Object... params){
        log(Log.INFO, 0, null, null, params);
    }

    /**
     * A helper method useful when you just want to print objects using default format.
     * @param level log level.
     * @param params objects to print.
     */
    public static void objects(int level, Object... params){
        log(level, 0, null, null, params);
    }

    /**
     * Core method : internal implementation.
     */
    private static void log(int level, int stackOffset, String tag, String msg, Object... params) {
        checkInitOrUseDefaultConfig();

        //Checking for auto tag
        if (TextUtils.isEmpty(tag) && mConfig.isUseAutoTag()) {
            tag = getAutoTag(stackOffset);
        }
        //Only concat when tag is not empty and config is specified to true
        if ((!TextUtils.isEmpty(tag)) && mConfig.isForceConcatGlobalTag()) {
            tag = mConfig.getGlobalTag() + "-" + tag;
        }
        //If still empty, using global
        else if (TextUtils.isEmpty(tag)) {
            tag = mConfig.getGlobalTag();
        }

        //If loggable, print it
        if (mConfig.getController().isLogEnabled(level, tag, msg)) {
            String logContent = wrapLogStr(stackOffset, msg, params);
            Logger logger = mConfig.getLogger();

            int maxLengthPerLine = mConfig.getMaxLengthPerLine();
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
                if (newlineIndex != -1 && newlineIndex < thisLineEnd){
                    subLine = logContent.substring(currentIndex, newlineIndex);
                    currentIndex = newlineIndex + 1;
                }
                else{
                    subLine = logContent.substring(currentIndex, thisLineEnd);
                    currentIndex = thisLineEnd;
                }

                //Not print yet, only append.
                sb.append(subLine).append("\n");
            }
            //IMPORTANT: if build throwable stack trace in string, it may be split into multi
            // line and becomes very ugly. Another side, if throwable is only a part of params(
            // e.g. total 3 params but only one throwable), it may flush other params into very
            // later print.
            for (Object p : params){
                if (p instanceof Throwable){
                    sb.append(Log.getStackTraceString((Throwable)p));
                }
            }
            callLoggerPrint(level, tag, sb.toString(), logger);
        }
    }

    // Get class name for tag.
    //
    // Note: This implementation is quite similar to #getLineNumAndMethodName, but different:
    // * stack level less 1 than #getLineNumAndMethodName
    // * inner class name using only last inner class (if present)
    // * if inner class unavailable, use full class name
    private static String getAutoTag(int stackOffset) {
        final int TARGET_STACK = STACK_TRACE_INDEX + mConfig.getGlobalStackOffset()
                + stackOffset - 1;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace == null || stackTrace.length < TARGET_STACK) {
            return null;
        }
        StackTraceElement element = stackTrace[TARGET_STACK];
        String className = element.getClassName();
        //parse to simple name
        String pkgPath[] = className.split("\\.");
        if (pkgPath.length > 0) {
            className = pkgPath[pkgPath.length - 1];
        }

        //IMPORTANT:
        // 因为Java语法允许在匿名类中继续包含具名的子类,因此必须逆序遍历,但是lastIndex方法没有endIndex参数。
        // 所以只能反向遍历, 每次截取最后一段执行subString, 如果全是数字,则继续往前遍历。
        // Since nested inner class in anonymous is allowed, here we must do reversal traverse
        // for the string.
        StringBuilder sbInnerClass = new StringBuilder();
        int index;
        String strLoop = className;
        while ((index = strLoop.lastIndexOf("$")) != -1) {
            String piece = strLoop.substring(index + 1); //skip dollar
            sbInnerClass.insert(0, piece);
            //Careful: if only judge 0-9, then A$1$2$3 case would get unexpected answer 2$3.
            if (!piece.matches("[0-9$]+")) {
                break;
            }
            //still anonymous class, continue loop
            sbInnerClass.insert(0, "$");
            //truncate last piece
            strLoop = strLoop.substring(0, index);
        }
        //delete first leading dollar
        if (sbInnerClass.length() > 0 && sbInnerClass.charAt(0) == '$') {
            sbInnerClass.deleteCharAt(0);
        }
        String innerClassName = sbInnerClass.toString();
        //This happens on class like MainActivity$1.
        if (TextUtils.isDigitsOnly(innerClassName)) {
            //Reset; use full name instead.
            innerClassName = null;
        }

        return TextUtils.isEmpty(innerClassName) ? className : innerClassName;
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

    private static String wrapLogStr(int stackOffset, String msg, Object... params) {
        String lineInfo = null;
        if (mConfig.isKeepLineNumber()) {
            lineInfo = getLineNumAndMethodName(stackOffset);
        }
        String content;
        //Both msg and params is empty, using empty msg
        if (TextUtils.isEmpty(msg) && (params == null || params.length == 0)) {
            content = mConfig.getEmptyMsg();
        } else {
            //if msg is specified, use default format
            if (!TextUtils.isEmpty(msg)) {
                content = String.format(msg, params);
            } else {
                //No msg but objects, using concat mode
                StringBuilder sb = new StringBuilder();
                if (params.length > 1) {
                    sb.append("\n");
                }
                for (int i = 0; i < params.length; i++) {
                    sb.append("param[")
                            .append(i)
                            .append("]=")
                            .append(ObjectUtil.objectToString(params[i]))
                            .append("\n")
                    ;
                }
                content = sb.toString();
            }
        }
        if (!TextUtils.isEmpty(lineInfo)) {
            content = lineInfo + content;
        }
        return content;
    }

    private static String getLineNumAndMethodName(int stackOffset) {
        final int TARGET_STACK = STACK_TRACE_INDEX + mConfig.getGlobalStackOffset() + stackOffset;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace == null || stackTrace.length < TARGET_STACK) {
            return null;
        }
        StackTraceElement element = stackTrace[TARGET_STACK];
        String className = element.getClassName();
        //parse to simple name
        String pkgPath[] = className.split("\\.");
        if (pkgPath.length > 0) {
            className = pkgPath[pkgPath.length - 1];
        }

        //If log in inner class, then class name contains '$', which cause IDE navigate file
        // function not working.
        int innerclassSymbolIndex = className.indexOf("$");
        //is inner class
        String innerClassName = null;
        if (innerclassSymbolIndex!=-1){
            //skip the first symbol
            innerClassName = className.substring(innerclassSymbolIndex+1);
            className = className.substring(0, innerclassSymbolIndex);
        }

        String methodName = element.getMethodName();
        int lineNum = element.getLineNumber();

        //concat inner classname in method string.
        if (mConfig.isKeepInnerClass() && (!TextUtils.isEmpty(innerClassName))){
            methodName = String.format("$%s#%s()", innerClassName, methodName);
        }
        else{
            methodName = String.format("#%s()", methodName);
        }

        return String.format("[(%s.java:%s)%s]", className, lineNum, methodName);
    }

}
