package org.mym.plog;

import android.graphics.Point;
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

    private static final int OBJECT_LEVEL = DEBUG;
    private static final int JSON_LEVEL = DEBUG;

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

    /**
     * test formatting a standard object that overrides toString properly.
     */
    @Test
    public void testVariableParamStandard() {
        String testStr = "Test %s";
        Point point = new Point(3, 4);
        PLog.d(testStr, point);
        assertLevel(DEBUG);
        assertMsgContains("Point");
    }

    @Test
    public void testVariableParamOrdinary() {
        String testStr = "Test %s";
        PLog.v(testStr, new User());
        assertLevel(VERBOSE);
        assertMsgContains("User");
    }

    /**
     * Test case that given arguments to format msg is not totally match.
     */
    @Test
    public void testVariableParamNotMatch() {
        String testStr = "Test %d";
        try {
            PLog.d(testStr, 4.3D); //DOUBLE IS NOT INTEGER!
            Assert.fail("formatting with not match type should fail!");
        } catch (Exception expected) {
            //Ignored
        }
    }

    /**
     * If msg is null or empty but arguments still specified, they should be formatted as well.
     */
    @Test
    public void testVariableParamWithNullMsg() {
        Point point = new Point(3, 4);
        PLog.d(null, point);
        assertLevel(DEBUG);
        assertMsgContains("Point");
    }

    /**
     * Print null object should be printed as P{}
     */
    @Test
    public void testObjectNull() {
        PLog.objects((Object) null);
    }

    @Test
    public void testObjectSingle() {
        User user = new User("Rose");
        PLog.objects(user);
        assertLevel(OBJECT_LEVEL);
        assertMsgContains("User", "Rose");
    }

    @Test
    public void testObjectMulti() {
        User user = new User("Rose");
        Point point = new Point(100, 128);
        PLog.objects(user, point);
        assertLevel(OBJECT_LEVEL);
        assertMsgContains("User", "Rose", "Point", "100", "128");
    }

    @Test
    public void testObjectPrimitive() {
        Long millisecond = System.currentTimeMillis();
        Double pi = Math.PI;
        Integer integer = 233;
        PLog.objects(millisecond, integer, pi);
        assertLevel(OBJECT_LEVEL);
        assertMsgContains("233", "3.14");
    }

    @Test
    public void testJsonNull() throws Exception {
        try {
            PLog.json(null);
            Assert.fail("Print null json should throw an exception!");
        } catch (Exception expected) {
            //Ignored
        }
    }

    @Test
    public void testJsonEmpty() throws Exception {
        try {
            PLog.json("");
            Assert.fail("Print empty json should throw an exception!");
        } catch (Exception expected) {
            //Ignored
        }
    }

    @Test
    public void testJsonWellFormed() {
        String jsonStr = "{\"array\":[1,2,3],\"boolean\":true,\"null\":null," +
                "\"number\":123,\"object\":{\"a\":\"b\",\"c\":\"d\",\"e\":\"f\"}," +
                "\"string\":\"Hello " +
                "World\"}";
        PLog.json(jsonStr);
        assertLevel(JSON_LEVEL);
        assertMsgContains("array", "\n"); //assume contains all
    }

    @Test
    public void testJsonMalFormed() {
        String json = "JSON_MAL_FORMED";
        try {
            PLog.json(json);
            Assert.fail("Print malformed json should throw an exception!");
        } catch (Exception expected) {
            //Ignored
        }
    }

    private void assertLevel(int level) {
        Assert.assertTrue(mMockLogger.getLastLogLevel() == level);
    }

    private void assertMsgContains(String... keys) {
        String lastLog = mMockLogger.getLastLog();
        for (String content : keys) {
            String failMsg = String.format("Expected contains %s but content is %s",
                    content, lastLog);
            Assert.assertTrue(failMsg, lastLog.contains(content));
        }
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

        public User() {
        }

        public User(String mName) {
            this.mName = mName;
        }
    }
}
