package org.mym.plog.config;

import android.text.TextUtils;
import android.util.Log;

/**
 * Class for config fields.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class PLogConfig {
    private String globalTag;
    /**
     * If this config is set to true, then all tag would be append after global tag.
     */
    private boolean forceConcatGlobalTag;
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

    private PLogConfig(Builder builder) {
        globalTag = builder.globalTag;
        forceConcatGlobalTag = builder.forceConcatGlobalTag;
        emptyMsg = builder.emptyMsg;
        emptyMsgLevel = builder.emptyMsgLevel;
        keepLineNumber = builder.keepLineNumber;
        keepInnerClass = builder.keepInnerClass;
        logger = builder.logger;
        controller = builder.controller;
    }

    @SuppressWarnings("unused")
    public static final class Builder {
        private String globalTag;
        private boolean forceConcatGlobalTag;
        private int emptyMsgLevel;
        private String emptyMsg;
        private boolean keepLineNumber;
        private boolean keepInnerClass;
        private Logger logger;
        private LogController controller;

        public Builder() {
        }

        public Builder globalTag(String val) {
            globalTag = val;
            return this;
        }

        public Builder forceConcatGlobalTag(boolean val) {
            forceConcatGlobalTag = val;
            return this;
        }

        public Builder emptyMsgLevel(int val) {
            emptyMsgLevel = val;
            return this;
        }

        public Builder emptyMsg(String val) {
            emptyMsg = val;
            return this;
        }

        public Builder keepLineNumber(boolean val) {
            keepLineNumber = val;
            return this;
        }

        public Builder keepInnerClass(boolean val) {
            keepInnerClass = val;
            return this;
        }

        public Builder logger(Logger val) {
            logger = val;
            return this;
        }

        public Builder controller(LogController val) {
            controller = val;
            return this;
        }

        public PLogConfig build() {
            //check fields which can be unsafe
            if (emptyMsgLevel < Log.VERBOSE || emptyMsgLevel > Log.ASSERT) {
                emptyMsgLevel = Log.DEBUG;
            }

            if (TextUtils.isEmpty(emptyMsg)) {
                emptyMsg = "Here executed.";
            }

            if (globalTag == null) {
                globalTag = "GlobalTag";
            }

            if (logger == null) {
                logger = new DefaultLogger();
            }
            if (controller == null) {
                controller = new EasyLogController(true);
            }
            return new PLogConfig(this);
        }
    }
}
