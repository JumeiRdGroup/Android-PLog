package org.mym.plog.config;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.mym.plog.Interceptor;
import org.mym.plog.PLog;
import org.mym.plog.PrintLevel;

/**
 * Configuration class for the whole library.
 *
 * <p>
 *     You can't call constructor directly; instead we recommend you to use Builder. All options
 *     are documented in that class.
 * </p>
 *
 * @see PLogConfig.Builder
 * @since 1.0.0
 */
public class PLogConfig {
    // -------------- DEFAULT FIELDS BEGIN --------------
    private static final int DEFAULT_EMPTY_MSG_LEVEL = Log.DEBUG;
    private static final String DEFAULT_EMPTY_MSG = "Here executed.";
    private static final String DEFAULT_GLOBAL_TAG = "GlobalTag";
    // -------------- DEFAULT FIELDS  END  --------------

    /*
     * A very useful setting when user does NOT directly call `PLog.xxx` but called by
     * `PLogWrapper.xxx`, etc.
     * Be careful for this setting: if you pass a wrong value, all your line number maybe incorrect.
     * @since 1.4.0
     */
    private int globalStackOffset;
    private String globalTag;
    /*
     * If this config is set to true, then all tags would be appended after global tag.
     * @since 1.0.0
     */
    private boolean forceConcatGlobalTag;
    /*
     * If set to true, then use class name as tag. The concat global tag config is still valid.
     * @since 1.2.0
     */
    private boolean useAutoTag;
    private String emptyMsg;
    private int emptyMsgLevel;
    private boolean needLineNumber;

    /*
     * If set to true, all log message will contains current thread information.
     *
     * @since 2.0.0
     */
    private boolean needThreadInfo;

    /*
     * Global interceptor which would affect all printers, may be null.
     *
     * @since 2.0.0
     */
    private Interceptor globalInterceptor;

    /*
     * Define a max limit for recursive formatting. Currently only affect when using default
     * formatter, please see `formatter` module or documentation for more details.
     *
     * @since 2.0.0
     */
    private int maxRecursiveDepth;

    private PLogConfig(Builder builder) {
        globalStackOffset = builder.globalStackOffset;
        globalTag = builder.globalTag;
        forceConcatGlobalTag = builder.forceConcatGlobalTag;
        useAutoTag = builder.useAutoTag;
        emptyMsg = builder.emptyMsg;
        emptyMsgLevel = builder.emptyMsgLevel;
        needLineNumber = builder.needLineNumber;
        globalInterceptor = builder.globalInterceptor;
        needThreadInfo = builder.needThreadInfo;
        maxRecursiveDepth = builder.maxRecursiveDepth;
    }

    /**
     * Check whether a config is valid or not. This is useful when our library importing new
     * config items, and can prevent user's wrong usage.
     * <p>
     * If all condition check pass, then this method do nothing; otherwise it would throw a
     * Runtime Exception.
     * </p>
     *
     * @param config config object to detect
     * @throws RuntimeException maybe any subclass of RuntimeException if any assertion failed.
     */
    public static void checkConfigSafe(PLogConfig config) throws RuntimeException {
        if (config == null) {
            throw new NullPointerException("Customized config cannot be null!");
        }
        if (config.getEmptyMsg() == null) {
            throw new NullPointerException("Empty msg cannot be null!");
        }
        if (config.getGlobalTag() == null) {
            throw new NullPointerException("Global tag cannot be null!");
        }
    }

    /**
     * Create a new Builder.
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Create a new builder and clone all options in the argument.
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder(PLogConfig copy) {
        return new Builder(copy);
    }

    public int getGlobalStackOffset() {
        return globalStackOffset;
    }

    public String getEmptyMsg() {
        return emptyMsg;
    }

    @PrintLevel
    public int getEmptyMsgLevel() {
        return emptyMsgLevel;
    }

    public String getGlobalTag() {
        return globalTag;
    }

    public boolean isForceConcatGlobalTag() {
        return forceConcatGlobalTag;
    }

    public boolean isUseAutoTag() {
        return useAutoTag;
    }

    @Nullable
    public Interceptor getGlobalInterceptor() {
        return globalInterceptor;
    }

    public boolean isNeedLineNumber() {
        return needLineNumber;
    }

    public boolean isNeedThreadInfo() {
        return needThreadInfo;
    }

    public int getMaxRecursiveDepth() {
        return maxRecursiveDepth;
    }

    @SuppressWarnings("unused")
    public static final class Builder {
        private String globalTag;
        private boolean forceConcatGlobalTag;
        private boolean useAutoTag;
        @PrintLevel
        private int emptyMsgLevel;
        private String emptyMsg;
        private boolean needLineNumber;
        private int globalStackOffset;
        @Nullable
        private Interceptor globalInterceptor;
        private boolean needThreadInfo;
        private int maxRecursiveDepth;

        /**
         * Create a builder, you can also use static method {@link #newBuilder()}.
         */
        public Builder() {
            //DEFAULT FIELDS IS INIT HERE
            useAutoTag = true;
            maxRecursiveDepth = 2;
        }

        /**
         * Create a builder.
         */
        public Builder(PLogConfig copy) {
            this.globalStackOffset = copy.globalStackOffset;
            this.globalTag = copy.globalTag;
            this.forceConcatGlobalTag = copy.forceConcatGlobalTag;
            this.useAutoTag = copy.useAutoTag;
            this.emptyMsg = copy.emptyMsg;
            this.emptyMsgLevel = copy.emptyMsgLevel;
            this.globalInterceptor = copy.globalInterceptor;
            this.needLineNumber = copy.needLineNumber;
            this.needThreadInfo = copy.needThreadInfo;
            this.maxRecursiveDepth = copy.maxRecursiveDepth;
        }

        /**
         * Set global tag for all logs, param should not be null.
         */
        public Builder globalTag(@NonNull String val) {
            globalTag = val;
            return this;
        }

        /**
         * If set to true, all tags will be appended after global tag, regardless of it is an
         * auto tag or explicit tag.
         *
         * We recommend you set to true if you've set a `global tag`.
         */
        public Builder forceConcatGlobalTag(boolean val) {
            forceConcatGlobalTag = val;
            return this;
        }

        /**
         * If set to true(as default option), use class name as tag.
         *
         * @since 1.2.0
         */
        public Builder useAutoTag(boolean val) {
            useAutoTag = val;
            return this;
        }

        /**
         * Set level for empty log. Default is <pre><code>Log.DEBUG</code></pre>.
         *
         * @param val Must be one of
         *            {@link Log#VERBOSE}, {@link Log#DEBUG}, {@link Log#INFO},
         *            {@link Log#WARN}, {@link Log#ERROR}.
         *            Otherwise this param is ignored.
         */
        public Builder emptyMsgLevel(@PrintLevel int val) {
            emptyMsgLevel = val;
            return this;
        }

        /**
         * Set default message for log printed by calling {@link PLog#empty()}.
         */
        public Builder emptyMsg(@NonNull String val) {
            emptyMsg = val;
            return this;
        }

        /**
         * If set to true, the line number info will be printed in log message.
         * @deprecated This method name is a little ambiguous, and will be removed in future
         * release. Use {@link #needLineNumber(boolean)} instead.
         */
        public Builder keepLineNumber(boolean val) {
            return needLineNumber(val);
        }

        /**
         * If set to true, the source line number info will be printed in log message.
         */
        public Builder needLineNumber(boolean val) {
            needLineNumber = val;
            return this;
        }

        /**
         * Set a global stack offset.
         * <p>
         * The recommended value is your wrapper level to `PLog` calls.
         * If you use PLog.xxx() directly, you DO NOT need this method.
         * </p>
         */
        public Builder globalStackOffset(int val) {
            globalStackOffset = val;
            return this;
        }

        /**
         * Register a global interceptor, useful for handle different app environments etc.
         */
        public Builder globalInterceptor(@Nullable Interceptor val) {
            globalInterceptor = val;
            return this;
        }

        /**
         * @deprecated This method name is a little ambiguous, and will be removed in future
         * release. Use {@link #needThreadInfo(boolean)} instead.
         */
        public Builder keepThreadInfo(boolean val) {
            return needThreadInfo(val);
        }

        /**
         * If set to true, thread info will be printed into log message.
         */
        public Builder needThreadInfo(boolean val) {
            needThreadInfo = val;
            return this;
        }

        /**
         * Set max depth limit when recursively format objects. If you do not use `formatter`
         * dependency, you don't need this option as well.
         *
         * @param depth should be positive. pass non-positive value will totally disable
         *              recursive formatting.
         * @since 2.0.0
         */
        public Builder maxRecursiveDepth(int depth) {
            maxRecursiveDepth = depth;
            return this;
        }

        public PLogConfig build() {
            //check fields which can be unsafe
            if (emptyMsgLevel < Log.VERBOSE || emptyMsgLevel > Log.ASSERT) {
                emptyMsgLevel = DEFAULT_EMPTY_MSG_LEVEL;
            }

            if (TextUtils.isEmpty(emptyMsg)) {
                emptyMsg = DEFAULT_EMPTY_MSG;
            }

            if (globalTag == null) {
                globalTag = DEFAULT_GLOBAL_TAG;
            }

            return new PLogConfig(this);
        }
    }
}
