package org.mym.plog;

import android.support.annotation.NonNull;

import org.mym.plog.internal.WordLengthWrapper;
import org.mym.plog.internal.WordBreakWrapper;

/**
 * Provide soft wrap feature.
 *
 * @since 2.0.0
 */
public interface SoftWrapper {

    SoftWrapper WORD_BREAK_WRAPPER = new WordBreakWrapper();

    SoftWrapper WORD_LENGTH_WRAPPER = new WordLengthWrapper();

//    int WRAP_LENGTH_SHORT = 100;
//    int WRAP_LENGTH_MIDDLE = 200;
//    int WRAP_LENGTH_LONG = 400;

    String wrapLine(@NonNull String input, int wrapLength);

}
