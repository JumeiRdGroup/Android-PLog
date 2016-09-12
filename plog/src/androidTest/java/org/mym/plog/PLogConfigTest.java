package org.mym.plog;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mym.plog.config.PLogConfig;

/**
 * Created by muyangmin on Sep 12, 2016.
 *
 * @author muyangmin
 * @since 1.5.0
 */
@RunWith(AndroidJUnit4.class)

public class PLogConfigTest {

    @Test
    public void testEmptyBuilder() {
        PLogConfig.newBuilder().build();
    }

    @Test
    public void testEmptyMsgNotNull() {
        PLogConfig config = PLogConfig.newBuilder()
                .emptyMsg(null)
                .build();
        assertConfigInvalid("Empty msg cannot be null", config);
    }

    @Test
    public void testGlobalTagNotNull() {
        PLogConfig config = PLogConfig.newBuilder()
                .globalTag(null)
                .build();
        assertConfigInvalid("Global tag cannot be null", config);
    }

    @Test
    public void testLoggerNotNull() {
        PLogConfig config = PLogConfig.newBuilder()
                .logger(null)
                .build();
        assertConfigInvalid("", config);
    }


    @Test
    public void testControllerNotNull() {
        PLogConfig config = PLogConfig.newBuilder()
                .controller(null)
                .build();
        assertConfigInvalid("", config);
    }

    private void assertConfigInvalid(String msg, PLogConfig config) {
        boolean checkMethodWorkFine = true;
        try {
            PLogConfig.checkConfigSafe(config);
        } catch (Exception expected) {
            checkMethodWorkFine = false;
        }
        Assert.assertTrue(msg, checkMethodWorkFine);
    }

}
