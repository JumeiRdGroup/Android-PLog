package org.mym.plog;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;
import org.mym.plog.config.PLogConfig;


/**
 *
 * Entry of all library public API; you can set configurations and launch log requests using this
 * class with only a static method call.
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public final class PLog {

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
     * Get current config, useful for temporarily change config and backup then.
     * Another scenario is to debug this library, or using in extend libraries.
     *
     * @return Current config; or default config if {@link #init(PLogConfig)} is not called yet.
     * @see #init(PLogConfig)
     * @since 1.3.0
     */
    @NonNull
    public static PLogConfig getCurrentConfig() {
        checkInitOrUseDefaultConfig();
        return mConfig;
    }

    public static LogRequest category(String category) {
        return category(SimpleCategory.obtain(category));
    }

    public static LogRequest category(@NonNull Category category) {
        return new LogRequest().category(category);
    }

    public static LogRequest level(@PrintLevel int level){
        return new LogRequest().level(level);
    }

    public static LogRequest tag(@NonNull String tag){
        return new LogRequest().tag(tag);
    }

    public static void v(String msg, Object... params) {
        //TODO implement a pool of logRequest
        new LogRequest().level(Log.VERBOSE).msg(msg).params(params).stackOffset(1).print();
    }

    public static void d(String msg, Object... params) {
        new LogRequest().level(Log.DEBUG).msg(msg).params(params).stackOffset(1).print();
    }

    public static void i(String msg, Object... params) {
        new LogRequest().level(Log.INFO).msg(msg).params(params).stackOffset(1).print();
    }

    public static void w(String msg, Object... params) {
        new LogRequest().level(Log.WARN).msg(msg).params(params).stackOffset(1).print();
    }

    public static void e(String msg, Object... params) {
        new LogRequest().level(Log.ERROR).msg(msg).params(params).stackOffset(1).print();
    }

    /**
     * Print a predefined message using a predefined level.
     *
     * @see org.mym.plog.config.PLogConfig.Builder#emptyMsg(String)
     * @see org.mym.plog.config.PLogConfig.Builder#emptyMsgLevel(int)
     */
    public static void empty() {
        new LogRequest().level(getCurrentConfig().getEmptyMsgLevel())
                .msg(getCurrentConfig().getEmptyMsg())
                .stackOffset(1).print();
    }

    /**
     * A helper method to print a PLOG call stack trace here.
     * @since 2.0.0-beta4
     */
    public static void printStackTraceHere() {
        new LogRequest().level(Log.INFO).printTraceOnly().print();
    }

    /**
     * A recommended helper method useful when you just want to print objects using default format.
     * The log level for this method is defined as <pre><code>Log.DEBUG</code></pre>.
     *
     * @param params objects to print.
     */
    public static void objects(Object... params) {
        new LogRequest().level(Log.DEBUG).params(params).stackOffset(1).print();
    }

    /**
     * Print out json string representing by a JSONObject.
     * @deprecated use {@link #objects(Object...)} instead. This method will be removed in future
     * release.
     * @since 2.0.0
     */
    public static void json(JSONObject jsonObject) {
        new LogRequest().level(Log.DEBUG).params(jsonObject).stackOffset(1).print();
    }

    /**
     * Print exceptions in WARN level.
     */
    public static void throwable(Throwable throwable) {
        new LogRequest().level(Log.WARN).params(throwable).stackOffset(1).print();
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     */
    public static void wtf(Throwable throwable) {
        new LogRequest().level(Log.ERROR).params(throwable).stackOffset(1).print();
    }

    private static void checkInitOrUseDefaultConfig() {
        if (mConfig == null) {
            init(new PLogConfig.Builder().build());
        }
    }

    /**
     * Prepare printers; this method should always be called on application start because you should
     * set your own onIntercept logic using {@link Printer} interface.
     * <pre>
     *     <code>
     * PLog.prepare(new DebugPrinter(BuildConfig.DEBUG));
     *     </code>
     * </pre>
     *
     * <strong>Note that calling this method will clear all printers set before.</strong>
     *
     * @param printers printers to print logs; they are parallel from each other.
     */
    public static void prepare(Printer... printers) {
        LogEngine.setPrinters(printers);
    }

    /**
     * Append an extra printer to currently printer collection.
     *
     * @param printer cannot be null
     */
    public static void appendPrinter(@NonNull Printer printer) {
        LogEngine.appendPrinter(printer);
    }
}
