package org.mym.plog;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This interface and its subclasses define the onIntercept and output rules.
 * Please consider it as compose of logger and controller in PLog 1.x version;
 * or just like the "Tree" concept of Timber library.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public interface Printer extends Interceptor {

    /**
     * Specify the formatter of this printer.
     * @return can be null. If so, msg would never be formatted.
     */
    @Nullable
    Formatter getFormatter();

    @Nullable
    Style getStyle();

//    /**
//     * Specify soft wrap setting.
//     * @return If returns true, soft wrap feature for this printer will be disabled.
//     */
//    @CheckResult
//    boolean isSoftWrapDisallowed();

    /**
     * Specify word wrapper, if needed.
     * @return the soft wrapper implementation; return null means disable soft wrap for this
     * printer. The constant implementation defined in {@link SoftWrapper} is strongly recommended.
     * @see SoftWrapper#WORD_BREAK_WRAPPER
     * @see SoftWrapper#WORD_LENGTH_WRAPPER
     */
    SoftWrapper getSoftWrapper();

    /**
     * Indicate how many characters should contains in one line; but only affect when soft wrap
     * is enabled. That is, {@link #getSoftWrapper()} returns not null.
     *
     * @return Should be a positive integer; otherwise you will get an argument exception.
     */
    int getMaxLengthPerLine();

    /**
     * Do the real output operation, e.g. call Log.x(), or write to file.
     * @param level    print level of this log.
     * @param msg      content of this log(already formatted and soft wrapped, if needed).
     */
    void print(@PrintLevel int level, @NonNull String tag, @NonNull String msg);
}
