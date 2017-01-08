package org.mym.plog;

import android.support.annotation.Nullable;

/**
 * This interface and its subclasses defines how logs are decorated.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
public interface Style {

    /**
     * Prefix of each log.
     * @return can be null
     */
    @Nullable
    String msgPrefix();

    /**
     * Suffix of each log.
     * @return can be null
     */
    @Nullable
    String msgSuffix();

    /**
     * the line header property is only effected when soft wrap is enabled.
     * @return the string of each line header, e.g. |.
     */
    @Nullable
    String lineHeader();
}
