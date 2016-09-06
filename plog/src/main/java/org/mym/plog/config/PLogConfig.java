package org.mym.plog.config;

import android.text.TextUtils;
import android.util.Log;

import org.mym.plog.PLog;

/**
 * Class for config fields.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class PLogConfig {
    // -------------- DEFAULT FIELDS BEGIN --------------
    private static final int DEFAULT_EMPTY_MSG_LEVEL = Log.DEBUG;
    private static final String DEFAULT_EMPTY_MSG = "Here executed.";
    private static final String DEFAULT_GLOBAL_TAG = "GlobalTag";
    //In my test case, default logcat can display only 4048 characters per line, so we force
    // print a new line when log is too long.
    private static final int DEFAULT_MAX_LENGTH_PER_LINE = 4000;
    // -------------- DEFAULT FIELDS  END  --------------

    private String globalTag;
    /**
     * If this config is set to true, then all tags would be appended after global tag.
     * @since 1.0.0
     */
    private boolean forceConcatGlobalTag;
    /**
     * If set to true, then use class name as tag. The concat global tag config is still valid.
     * @since 1.2.0
     */
    private boolean useAutoTag;
    private String emptyMsg;
    private int emptyMsgLevel;
    private boolean keepLineNumber;
    /**
     * If this set to false, then inner class name would not be printed.
     * @since 1.0.0
     */
    private boolean keepInnerClass;
    private Logger logger;
    private LogController controller;
    /**
     * Max character count per line.
     * @since 1.2.0
     */
    private int maxLengthPerLine;

    private PLogConfig(Builder builder) {
        globalTag = builder.globalTag;
        forceConcatGlobalTag = builder.forceConcatGlobalTag;
        useAutoTag = builder.useAutoTag;
        emptyMsg = builder.emptyMsg;
        emptyMsgLevel = builder.emptyMsgLevel;
        keepLineNumber = builder.keepLineNumber;
        keepInnerClass = builder.keepInnerClass;
        logger = builder.logger;
        controller = builder.controller;
        maxLengthPerLine = builder.maxLengthPerLine;
    }

    /**
     * This method check whether a config is valid. This is very useful when importing new
     * config items, and can prevent user's wrong usage.
     * <p>If all condition check pass, then this method do nothing; otherwise it would throw a
     * Runtime Exception.</p>
     *
     * @param config config object to detect
     * @throws RuntimeException maybe any subclass of RuntimeException if any condition assert
     *                          failed, though it usually be a {@link NullPointerException}.
     */
    public static void checkConfigSafe(PLogConfig config) throws RuntimeException {
        if (config == null) {
            throw new NullPointerException("Customized config cannot be null!");
        }
        if (config.getController() == null) {
            throw new NullPointerException("Log controller cannot be null!");
        }
        if (config.getLogger() == null) {
            throw new NullPointerException("Logger cannot be null!");
        }
        if (config.getEmptyMsg() == null) {
            throw new NullPointerException("Empty msg cannot be null!");
        }
        if (config.getGlobalTag() == null) {
            throw new NullPointerException("Global tag cannot be null!");
        }
        if (config.getMaxLengthPerLine() <= 0){
            throw new IllegalArgumentException("Max length per line must be positive!");
        }
    }

    /**
     * @see Builder#Builder()
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * @see Builder#Builder(PLogConfig)
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder(PLogConfig copy) {
        return new Builder(copy);
    }

    public String getGlobalTag() {
        return globalTag;
    }

    public String getEmptyMsg() {
        return emptyMsg;
    }

    public int getEmptyMsgLevel() {
        return emptyMsgLevel;
    }

    public boolean isForceConcatGlobalTag() {
        return forceConcatGlobalTag;
    }

    public boolean isKeepLineNumber() {
        return keepLineNumber;
    }

    public boolean isKeepInnerClass() {
        return keepInnerClass;
    }

    public Logger getLogger() {
        return logger;
    }

    public LogController getController() {
        return controller;
    }

    public int getMaxLengthPerLine() {
        return maxLengthPerLine;
    }

    public boolean isUseAutoTag() {
        return useAutoTag;
    }

    @SuppressWarnings("unused")
    public static final class Builder {
        private String globalTag;
        private boolean forceConcatGlobalTag;
        private boolean useAutoTag;
        private int emptyMsgLevel;
        private String emptyMsg;
        private boolean keepLineNumber;
        private boolean keepInnerClass;
        private Logger logger;
        private int maxLengthPerLine;
        private LogController controller;

        /**
         * Create a builder, you can also use static method {@link #newBuilder()}.
         */
        public Builder() {
        }

        public Builder(PLogConfig copy) {
            this.controller = copy.controller;
            this.globalTag = copy.globalTag;
            this.forceConcatGlobalTag = copy.forceConcatGlobalTag;
            this.useAutoTag = copy.useAutoTag;
            this.emptyMsg = copy.emptyMsg;
            this.emptyMsgLevel = copy.emptyMsgLevel;
            this.keepLineNumber = copy.keepLineNumber;
            this.keepInnerClass = copy.keepInnerClass;
            this.logger = copy.logger;
            this.maxLengthPerLine = copy.maxLengthPerLine;
        }

        /**
         * Set global tag for all log, param must be not null.
         */
        public Builder globalTag(String val) {
            globalTag = val;
            return this;
        }

        /**
         * If this sets to true, then all tags would be appended after global tag.
         */
        public Builder forceConcatGlobalTag(boolean val) {
            forceConcatGlobalTag = val;
            return this;
        }

        /**
         * If set to true, then use class name as tag. The concat global tag config is still valid.
         * @since 1.2.0
         */
        public Builder useAutoTag(boolean val) {
            useAutoTag = val;
            return this;
        }

        /**
         * Set level for empty log. Default is {@value #DEFAULT_EMPTY_MSG_LEVEL}.
         *
         * @param val Must be one of
         *            {@link Log#VERBOSE}, {@link Log#DEBUG}, {@link Log#INFO},
         *            {@link Log#WARN}, {@link Log#ERROR}.
         *            Otherwise this param is ignored.
         */
        public Builder emptyMsgLevel(int val) {
            emptyMsgLevel = val;
            return this;
        }

        /**
         * Set default message for log printed by calling {@link PLog#empty()}.
         * The default value is "{@value #DEFAULT_EMPTY_MSG}".
         */
        public Builder emptyMsg(String val) {
            emptyMsg = val;
            return this;
        }

        /**
         * If this sets to true, the line number info would printed in log message.
         */
        public Builder keepLineNumber(boolean val) {
            keepLineNumber = val;
            return this;
        }

        /**
         * If this sets to true and the log printed in an inner class, then this inner class name
         * would also printed before method name.
         */
        public Builder keepInnerClass(boolean val) {
            keepInnerClass = val;
            return this;
        }

        /**
         * Customize logger.
         */
        public Builder logger(Logger val) {
            logger = val;
            return this;
        }

        /**
         * Define a threshold value to tell logger to split log into multi lines when log length
         * reaches this limitation.
         *
         * @param val any positive integer is now allowed. Default is {@value
         *            #DEFAULT_MAX_LENGTH_PER_LINE} (i.e. logcat max length) by test result.
         * @since 1.2.0
         */
        public Builder maxLengthPerLine(int val) {
            maxLengthPerLine = val;
            return this;
        }

        /**
         * Customize log controller.
         */
        public Builder controller(LogController val) {
            controller = val;
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

            if (logger == null) {
                logger = new DefaultLogger();
            }
            if (controller == null) {
                controller = new EasyLogController(true, true);
            }

            //Assume all positive integers are acceptable
            if (maxLengthPerLine <= 0) {
                maxLengthPerLine = DEFAULT_MAX_LENGTH_PER_LINE;
            }

            return new PLogConfig(this);
        }
    }
}
