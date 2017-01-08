package org.mym.plog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mym.plog.formatter.Formatter;

/**
 * This interface and its subclasses define the intercept and output rules.
 * Please consider it as compose of logger and controller in PLog 1.x version;
 * or just like the "Tree" concept of Timber library.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public interface Printer {
    /**
     * Indicate if this item of log can be printed.
     *
     * @param level    print level of this log.
     * @param category category of this log, if specified.
     * @param msg      content of this log(before formatting!).
     * @return if returns true, this log won't be printed and just be ignored. Otherwise it would
     * be formatted and printed as usual.
     */
    boolean intercept(@PrintLevel int level, @Nullable Category category, @NonNull String msg);

    /**
     * Specify the formatter of this printer.
     * @return can be null. If so, msg would never be formatted.
     */
    @Nullable
    Formatter getFormatter();

    /**
     * Specify soft wrap setting.
     * @return If returns true, soft wrap feature for this printer will be disabled.
     */
    boolean disallowSoftWrap();

    /**
     * Do the real output operation, e.g. call Log.x(), or write to file.
     * @param level    print level of this log.
     * @param msg      content of this log(already formatted and soft wrapped, if needed).
     */
    void print(@PrintLevel int level, @NonNull String tag, @NonNull String msg);
}
