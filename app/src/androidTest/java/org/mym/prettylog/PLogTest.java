package org.mym.prettylog;

import android.app.Activity;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mym.plog.PLog;
import org.mym.plog.config.PLogConfig;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.click;

/**
 * test class for PLog's basic function.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PLogTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
    Activity activity = null;

    @Before
    public void setUp() {
        activity = mActivityRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);

        PLog.init(PLogConfig.newBuilder()
                .keepInnerClass(true)
                .keepLineNumber(true)
                .useAutoTag(true)
                .forceConcatGlobalTag(true)
                .maxLengthPerLine(180)
                .build());
    }

    @Test
    @Ignore
    public void testAllSampleCase(){
        //Use scrollTo() before click() to avoid click button not visible at least 90%.
        //Please see http://stackoverflow.com/a/28836033/4944176

        onView(withId(R.id.btn_basic_usage)).perform(scrollTo(), click());
        onView(withId(R.id.btn_log_empty)).perform(scrollTo(), click());
        onView(withId(R.id.btn_log_tags)).perform(scrollTo(), click());
        onView(withId(R.id.btn_log_long)).perform(scrollTo(), click());
        onView(withId(R.id.btn_log_objects)).perform(scrollTo(), click());
        onView(withId(R.id.btn_log_throwable)).perform(scrollTo(), click());
        onView(withId(R.id.btn_log_json)).perform(scrollTo(), click());
        onView(withId(R.id.btn_timing_logger)).perform(scrollTo(), click());
        onView(withId(R.id.btn_auto_tag)).perform(scrollTo(), click());
        onView(withId(R.id.btn_tag_anonymous)).perform(scrollTo(), click());
        onView(withId(R.id.btn_tag_nested)).perform(scrollTo(), click());
        //Customize logger is not belong to library usage test, but sample code should pass test
        onView(withId(R.id.btn_customize_logger)).perform(scrollTo(), click());
        onView(withId(R.id.btn_stack_offset)).perform(scrollTo(), click());
        onView(withId(R.id.btn_log_using_wrapper_class)).perform(scrollTo(), click());

    }
}
