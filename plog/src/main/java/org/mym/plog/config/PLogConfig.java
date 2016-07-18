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
    private Logger logger;
    private LogController controller;

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

    public Logger getLogger() {
        return logger;
    }

    public LogController getController() {
        return controller;
    }

    private PLogConfig(Builder builder) {
        globalTag = builder.globalTag;
        emptyMsgLevel = builder.emptyMsgLevel;
        forceConcatGlobalTag = builder.forceConcatGlobalTag;
        emptyMsg = builder.emptyMsg;
        keepLineNumber = builder.keepLineNumber;
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
