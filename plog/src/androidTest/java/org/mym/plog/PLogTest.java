package org.mym.plog;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mym.plog.config.PLogConfig;
import org.mym.plog.logger.SinglePipeLogger;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static android.util.Log.WARN;

/**
 * Created by muyangmin on Sep 12, 2016.
 *
 * @author muyangmin
 * @since 1.5.0
 */
@RunWith(AndroidJUnit4.class)
public class PLogTest {

    private static final String EMPTY_MSG = "EmptyMsg";
    private static final int EMPTY_LEVEL = DEBUG;

    private MemoryLogger mMockLogger;

    @Before
    public void setUp() {
        mMockLogger = new MemoryLogger();
        PLog.init(PLogConfig.newBuilder()
                .logger(mMockLogger)
                .emptyMsg(EMPTY_MSG)
                .emptyMsgLevel(EMPTY_LEVEL)
                .build());
    }

    @Test
    public void testLogV() {
        String testStr = "VERBOSE Log";
        PLog.v(testStr);
        assertLogInfo(VERBOSE, null, testStr);
    }

    @Test
    public void testLogD() {
        String testStr = "DEBUG Log";
        PLog.d(testStr);
        assertLogInfo(DEBUG, null, testStr);
    }

    @Test
    public void testLogI() {
        String testStr = "INFO Log";
        PLog.i(testStr);
        assertLogInfo(INFO, null, testStr);
    }

    @Test
    public void testLogW() {
        String testStr = "WARN Log";
        PLog.w(testStr);
        assertLogInfo(WARN, null, testStr);
    }

    @Test
    public void testLogE() {
        String testStr = "ERROR Log";
        PLog.e(testStr);
        assertLogInfo(ERROR, null, testStr);
    }

    @Test
    public void testLogTagV() {
        String testStr = "Test";
        String testTag = "TagForTest";
        PLog.v(testTag, testStr);
        assertLogInfo(VERBOSE, testTag, testStr);
    }

    @Test
    public void testLogTagD() {
        String testStr = "Test";
        String testTag = "TagForTest";
        PLog.d(testTag, testStr);
        assertLogInfo(DEBUG, testTag, testStr);
    }

    @Test
    public void testLogTagI() {
        String testStr = "Test";
        String testTag = "TagForTest";
        PLog.i(testTag, testStr);
        assertLogInfo(INFO, testTag, testStr);
    }

    @Test
    public void testLogTagW() {
        String testStr = "Test";
        String testTag = "TagForTest";
        PLog.w(testTag, testStr);
        assertLogInfo(WARN, testTag, testStr);
    }

    @Test
    public void testLogTagE() {
        String testStr = "Test";
        String testTag = "TagForTest";
        PLog.e(testTag, testStr);
        assertLogInfo(ERROR, testTag, testStr);
    }

    /**
     * If tag is null, then msg should be printed without exception.
     */
    @Test
    public void testLogTagNull() {
        String testMsg = "testLogTagNull";
        PLog.i(null, testMsg);
        assertLogInfo(INFO, null, testMsg);
    }

    /**
     * If msg is null, then empty msg should be printed in empty level.
     */
    @Test
    public void testLogMsgNull() {
        PLog.i(null);
        assertLogInfo(EMPTY_LEVEL, null, EMPTY_MSG);
    }

    /**
     * If msg is empty, then empty msg should be printed in empty level.
     */
    @Test
    public void testLogMsgEmpty() {
        PLog.i("");
        assertLogInfo(EMPTY_LEVEL, null, EMPTY_MSG);
    }

    @Test
    public void testVariableParamNull() {
        String testStr = "Test";
        PLog.i(testStr, (Object) null);
        assertLogInfo(INFO, null, testStr);
    }

    @Test
    public void testEmptyMsgAndLevel() {
        PLog.empty();
        assertLogInfo(EMPTY_LEVEL, null, EMPTY_MSG);
    }

    private void assertLogInfo(int level, String tag, String msg) {
        if (level > 0) {
            Assert.assertEquals(level, mMockLogger.getLastLogLevel());
        }
        if (tag != null) {
            Assert.assertEquals(tag, mMockLogger.getLastTag());
        }
        if (msg != null) {
            Assert.assertEquals(msg, mMockLogger.getLastLog());
        }
    }

    //Helper class for asserting logger behavior
    private static class MemoryLogger extends SinglePipeLogger {

        private int mLastLevel;
        private String mLastTag;
        private String mLastMsg;

        @Override
        protected void log(int level, String tag, String msg) {
            callDefaultLogger(level, tag, msg);
            mLastLevel = level;
            mLastTag = tag;
            mLastMsg = msg;
        }

        public String getLastLog() {
            return mLastMsg;
        }

        public int getLastLogLevel() {
            return mLastLevel;
        }

        public String getLastTag() {
            return mLastTag;
        }
    }

    private static class User {
        protected String mName;
    }
}
