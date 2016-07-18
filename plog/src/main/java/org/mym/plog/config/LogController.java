package org.mym.plog.config;

/**
 * This interface provides a mechanism to fully control log output
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public interface LogController {
    /**
     * Decide whether this log should be printed.
     * @param level log level, same as standard V, D, I, W, E.
     * @param tag log tag.
     * @param msg log content
     * @return if this log should be printed, return true.
     * @see android.util.Log#VERBOSE
     * @see android.util.Log#DEBUG
     * @see android.util.Log#INFO
     * @see android.util.Log#WARN
     * @see android.util.Log#ERROR
     */
    boolean isLogEnabled(int level, String tag, String msg);
}
