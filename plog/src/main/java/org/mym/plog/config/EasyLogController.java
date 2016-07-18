package org.mym.plog.config;

/**
 * Default implementation of {@link LogController}.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class EasyLogController implements LogController {
    private boolean isLogEnabled;

    public EasyLogController(boolean isLogEnabled) {
        this.isLogEnabled = isLogEnabled;
    }

    @Override
    public boolean isLogEnabled(int level, String tag, String msg) {
        return isLogEnabled;
    }
}
