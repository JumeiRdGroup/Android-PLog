package org.mym.plog.internal;

import android.support.annotation.NonNull;

import org.mym.plog.SoftWrapper;

/**
 * A simple length truncate wrapper.
 *
 * @since 2.0.0
 */
public class WordLengthWrapper implements SoftWrapper {


    /**
     * Soft wrap line rule implementation.
     *
     * @param logContent       log to be printed, NOT NULL
     * @param maxLengthPerLine max length
     * @return wrapped log
     */
    @Override
    public String wrapLine(@NonNull String logContent, int maxLengthPerLine) {

        if (logContent.isEmpty()) { // Not need to to StringBuilder and while loop
            return logContent;
        }

        int currentIndex = 0;
        //Use a StringBuilder to build multi line text but print only once, solve #6
        StringBuilder sb = new StringBuilder(logContent.length()
                + logContent.length() / maxLengthPerLine); //plus \n symbol

        while (currentIndex < logContent.length()) {
            //compute max length of this line
            int currentLineLength = Math.min(maxLengthPerLine,
                    logContent.length() - currentIndex);

            //Force new line if \n appears, otherwise use our soft wrap.
            String subLine;

            int newlineIndex = logContent.indexOf("\n", currentIndex);
            int thisLineEnd = currentIndex + currentLineLength;

            //has \n in this line;
            if (newlineIndex != -1 && newlineIndex < thisLineEnd) {
                subLine = logContent.substring(currentIndex, newlineIndex);
                currentIndex = newlineIndex + 1;
            } else {
                subLine = logContent.substring(currentIndex, thisLineEnd);
                currentIndex = thisLineEnd;
            }

            //Not print yet, only append.
            sb.append(subLine);
            //Has more chars
            if (currentIndex < logContent.length()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
