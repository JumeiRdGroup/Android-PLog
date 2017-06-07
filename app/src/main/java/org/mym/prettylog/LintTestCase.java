package org.mym.prettylog;

import android.util.Log;

import org.mym.plog.PLog;

import java.util.Locale;

/**
 * This source is belong to main, I want it to be just like a normal biz code,
 * though its actual purpose is to show and check PLog lint issues.
 * Created by muyangmin on Jun 07, 2017.
 */
public final class LintTestCase {

    void testLintLogNotPLog() {
        System.out.print("This is a test code.");
        System.out.println("test lint not plog.");

        Log.d("Lint", "use android.util.Log class.");

        PLog.v("use PLog, this is right.");
    }

    void testLintNestedFormatInPLog() {
        PLog.d(String.format(Locale.US, "%s %s", "Hello", "World"));

        //emulate a normal formatting
        String result = String.format("This is %s right call.", 2);
    }
}
