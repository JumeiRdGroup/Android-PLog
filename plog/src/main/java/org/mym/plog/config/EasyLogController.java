package org.mym.plog.config;

/**
 * Default implementation of {@link LogController}.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class EasyLogController implements LogController {

    private boolean isLogEnabled;
    private boolean isTimingLogEnabled;

    public EasyLogController(boolean isTimingLogEnabled, boolean isLogEnabled) {
        this.isTimingLogEnabled = isTimingLogEnabled;
        this.isLogEnabled = isLogEnabled;
    }

    @Override
    public boolean isLogEnabled(int level, String tag, String msg) {
        return isLogEnabled;
    }

    @Override
    public boolean isTimingLogEnabled() {
        return isTimingLogEnabled;
    }
}
