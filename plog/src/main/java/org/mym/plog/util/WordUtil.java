package org.mym.plog.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a copy of WordUtils in apache commons-lang, version 3.5.
 * Disclaimer:
 * 1. PLog use this only for providing more stable soft-wrap feature without include dependency.
 * 2. PLog also respect APACHE licence.
 * <p>
 * Created by muyangmin on Jan 13, 2017.
 *
 * @since 4.1.0
 */

public class WordUtil {

    private static final String LINE_SEPARATOR = getSystemProperty("line.separator");

    public static String wrap(final String str, int wrapLength) {
        return wrap(str, wrapLength, null, false, "[\\s\\n]+");
    }

    /**
     * <p>Wraps a single line of text, identifying words by <code>wrapOn</code>.</p>
     * <p>
     * <p>Leading spaces on a new line are stripped.
     * Trailing spaces are not stripped.</p>
     * <p>
     * <table border="1" summary="Wrap Results">
     * <tr>
     * <th>input</th>
     * <th>wrapLength</th>
     * <th>newLineString</th>
     * <th>wrapLongWords</th>
     * <th>wrapOn</th>
     * <th>result</th>
     * </tr>
     * <tr>
     * <td>null</td>
     * <td>*</td>
     * <td>*</td>
     * <td>true/false</td>
     * <td>*</td>
     * <td>null</td>
     * </tr>
     * <tr>
     * <td>""</td>
     * <td>*</td>
     * <td>*</td>
     * <td>true/false</td>
     * <td>*</td>
     * <td>""</td>
     * </tr>
     * <tr>
     * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
     * <td>20</td>
     * <td>"\n"</td>
     * <td>true/false</td>
     * <td>" "</td>
     * <td>"Here is one line of\ntext that is going\nto be wrapped after\n20 columns."</td>
     * </tr>
     * <tr>
     * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
     * <td>20</td>
     * <td>"&lt;br /&gt;"</td>
     * <td>true/false</td>
     * <td>" "</td>
     * <td>"Here is one line of&lt;br /&gt;text that is going&lt;br /&gt;to be wrapped after&lt;
     * br /&gt;20 columns."</td>
     * </tr>
     * <tr>
     * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
     * <td>20</td>
     * <td>null</td>
     * <td>true/false</td>
     * <td>" "</td>
     * <td>"Here is one line of" + systemNewLine + "text that is going" + systemNewLine + "to be
     * wrapped after" + systemNewLine + "20 columns."</td>
     * </tr>
     * <tr>
     * <td>"Click here to jump to the commons website - http://commons.apache.org"</td>
     * <td>20</td>
     * <td>"\n"</td>
     * <td>false</td>
     * <td>" "</td>
     * <td>"Click here to jump\nto the commons\nwebsite -\nhttp://commons.apache.org"</td>
     * </tr>
     * <tr>
     * <td>"Click here to jump to the commons website - http://commons.apache.org"</td>
     * <td>20</td>
     * <td>"\n"</td>
     * <td>true</td>
     * <td>" "</td>
     * <td>"Click here to jump\nto the commons\nwebsite -\nhttp://commons.apach\ne.org"</td>
     * </tr>
     * <tr>
     * <td>"flammable/inflammable"</td>
     * <td>20</td>
     * <td>"\n"</td>
     * <td>true</td>
     * <td>"/"</td>
     * <td>"flammable\ninflammable"</td>
     * </tr>
     * </table>
     *
     * @param str           the String to be word wrapped, may be null
     * @param wrapLength    the column to wrap the words at, less than 1 is treated as 1
     * @param newLineStr    the string to insert for a new line,
     *                      <code>null</code> uses the system property line separator
     * @param wrapLongWords true if long words (such as URLs) should be wrapped
     * @param wrapOn        regex expression to be used as a breakable characters,
     *                      if blank string is provided a space character will be used
     * @return a line with newlines inserted, <code>null</code> if null input
     */
    public static String wrap(final String str, int wrapLength, String newLineStr, final boolean
            wrapLongWords, String wrapOn) {
        if (str == null) {
            return null;
        }
        if (newLineStr == null) {
            newLineStr = LINE_SEPARATOR;
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        if (isBlank(wrapOn)) {
            wrapOn = " ";
        }
        final Pattern patternToWrapOn = Pattern.compile(wrapOn);
        final int inputLineLength = str.length();
        int offset = 0;
        final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

        while (offset < inputLineLength) {
            int spaceToWrapAt = -1;
            Matcher matcher = patternToWrapOn.matcher(str.substring(offset, Math.min(offset +
                    wrapLength + 1, inputLineLength)));
            if (matcher.find()) {
                //这里会跳过每一行的前导断词符，即前导空格
                if (matcher.start() == 0) {
                    offset += matcher.end();
                    continue;
                } else {
                    spaceToWrapAt = matcher.start() + offset;
                }
            }

            // only last line without leading spaces is left
            if (inputLineLength - offset <= wrapLength) {
                break;
            }

            while (matcher.find()) {
                spaceToWrapAt = matcher.start() + offset;
            }

            if (spaceToWrapAt >= offset) {
                // normal case
                wrappedLine.append(str.substring(offset, spaceToWrapAt));
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;

            } else {
                // really long word or URL
                if (wrapLongWords) {
                    // wrap really long word one line at a time
                    wrappedLine.append(str.substring(offset, wrapLength + offset));
                    wrappedLine.append(newLineStr);
                    offset += wrapLength;
                } else {
                    // do not wrap really long word, just extend beyond limit
                    matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
                    if (matcher.find()) {
                        spaceToWrapAt = matcher.start() + offset + wrapLength;
                    }

                    if (spaceToWrapAt >= 0) {
                        wrappedLine.append(str.substring(offset, spaceToWrapAt));
                        wrappedLine.append(newLineStr);
                        offset = spaceToWrapAt + 1;
                    } else {
                        wrappedLine.append(str.substring(offset));
                        offset = inputLineLength;
                    }
                }
            }
        }

        // Whatever is left in line is short enough to just pass through
        wrappedLine.append(str.substring(offset));

        return wrappedLine.toString();
    }

    // -----------------------------------------------------------------------

    /**
     * <p>
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     * </p>
     * <p>
     * If a {@code SecurityException} is caught, the return value is {@code null} and a message
     * is written to
     * {@code System.err}.
     * </p>
     *
     * @param property the system property name
     * @return the system property value or {@code null} if a security problem occurs
     */
    private static String getSystemProperty(final String property) {
        try {
            return System.getProperty(property);
        } catch (final SecurityException ex) {
            // we are not allowed to look at this property
            System.err.println("Caught a SecurityException reading the system property '" + property
                    + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }

    /**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     * <p>
     * </p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
