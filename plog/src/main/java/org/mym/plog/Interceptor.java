package org.mym.plog;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Decide intercept a log, or let it print out.
 *
 * @since 2.0.0-beta2
 */
public interface Interceptor {
    /**
     * Decide whether this item of log should be intercepted.
     *
     * @param level    print level of this log.
     * @param category category of this log, if specified.
     * @param msg      content of this log(before formatting!). <strong> Note if msg in the
     *                 original request is null, you'll get an empty string here.</strong>
     * @return if returns true, this log won't be printed and just be ignored. Otherwise it would
     * be formatted and printed as usual.
     */
    @CheckResult
    boolean onIntercept(@PrintLevel int level, @NonNull String tag,
                        @Nullable Category category, @NonNull String msg);

}
