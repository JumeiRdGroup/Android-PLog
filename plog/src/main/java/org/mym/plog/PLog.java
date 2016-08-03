package org.mym.plog;

import android.text.TextUtils;
import android.util.Log;

import org.mym.plog.config.Logger;
import org.mym.plog.config.PLogConfig;

/**
 * Entry class of log module, settings, and init configs are all here.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class PLog {

    /**
     * 0 dalvik.system.VMStack.getThreadStackTrace(Native Method)       <br/>
     * 1 java.lang.Thread.getStackTrace(Thread.java:579)                <br/>
     * 2 {@link #getLineNumAndMethodName()}                             <br/>
     * 3 {@link #wrapLogStr(String, Object...)}                         <br/>
     * 4 {@link #log(int, String, String, Object...)}                   <br/>
     * 5 v, d, i, w, e                                                  <br/>
     * 6 invoker
     */
    private static final int STACK_TRACE_INDEX = 6;

    private static PLogConfig mConfig;

    public static void init(PLogConfig config) {
        mConfig = config;
    }

    public static void v(String msg, Object... params) {
        log(Log.VERBOSE, null, msg, params);
    }

    public static void v(String tag, String msg, Object... params) {
        log(Log.VERBOSE, tag, msg, params);
    }

    public static void d(String msg, Object... params) {
        log(Log.DEBUG, null, msg, params);
    }

    public static void d(String tag, String msg, Object... params) {
        log(Log.DEBUG, tag, msg, params);
    }

    public static void i(String msg, Object... params) {
        log(Log.INFO, null, msg, params);
    }

    public static void i(String tag, String msg, Object... params) {
        log(Log.INFO, tag, msg, params);
    }

    public static void w(String msg, Object... params) {
        log(Log.WARN, null, msg, params);
    }

    public static void w(String tag, String msg, Object... params) {
        log(Log.WARN, tag, msg, params);
    }

    public static void e(String msg, Object... params) {
        log(Log.ERROR, null, msg, params);
    }

    public static void e(String tag, String msg, Object... params) {
        log(Log.ERROR, tag, msg, params);
    }

    /**
     * Print {@link PLogConfig#getEmptyMsg()}, using DEBUG Level,
     * or {@link PLogConfig#getEmptyMsgLevel()} if specified.
     */
    public static void empty() {
        int level = mConfig == null ? Log.DEBUG : mConfig.getEmptyMsgLevel();
        log(level, null, null);
    }

    private static void log(int level, String tag, String msg, Object... params) {
        checkInitOrUseDefaultConfig();

        //When tag is empty, using global
        if (TextUtils.isEmpty(tag)) {
            tag = mConfig.getGlobalTag();
        }
        //Only concat when tag is not empty and config is specified to true
        else if (mConfig.isForceConcatGlobalTag()){
            tag = mConfig.getGlobalTag() + "-" + tag;
        }

        //If loggable, print it
        if (mConfig.getController().isLogEnabled(level, tag, msg)) {
            String logContent = wrapLogStr(msg, params);
            Logger logger = mConfig.getLogger();
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
    }

    private static void checkInitOrUseDefaultConfig() {
        if (mConfig == null) {
            mConfig = new PLogConfig.Builder().build();
        }
    }

    private static String wrapLogStr(String msg, Object... params) {
        String lineInfo = null;
        if (mConfig.isKeepLineNumber()) {
            lineInfo = getLineNumAndMethodName();
        }
        String content;
        if (TextUtils.isEmpty(msg)) {
            content = mConfig.getEmptyMsg();
        } else {
            content = String.format(msg, params);
        }
        if (!TextUtils.isEmpty(lineInfo)) {
            content = lineInfo + content;
        }
        return content;
    }

    private static String getLineNumAndMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace == null || stackTrace.length < STACK_TRACE_INDEX) {
            return null;
        }
        StackTraceElement element = stackTrace[STACK_TRACE_INDEX];
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
