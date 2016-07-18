package org.mym.plog.config;

/**
 * Class for config fields.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class PLogConfig {
    private String globalTag;
    private String emptyMsg;
    private boolean keepLineNumber;
    private Logger logger;
    private LogController controller;

    public String getGlobalTag() {
        return globalTag;
    }

    public String getEmptyMsg() {
        return emptyMsg;
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
        emptyMsg = builder.emptyMsg;
        keepLineNumber = builder.keepLineNumber;
        logger = builder.logger;
        controller = builder.controller;
    }

    @SuppressWarnings("unused")
    public static final class Builder {
        private String globalTag;
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
