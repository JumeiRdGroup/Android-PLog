package org.mym.plog.timing;

import android.os.SystemClock;
import android.util.Log;

import org.mym.plog.PLog;

import java.util.ArrayList;

/**
 * IMPORTANT:This class is copied from AOSP standard class {@link android.util.TimingLogger}.
 * CHANGES:
 * 1. removed disable logic. using PLog's timing logger means you totally wants to log these messages.
 * 2. redirect output logic to PLog standard log flow.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class TimingLogger {

    /** Stores the time of each split. */
    private ArrayList<Long> mSplits;
    /** Stores the labels for each split. */
    private ArrayList<String> mSplitLabels;
    /**
     * The Log tag to use for checking Log.isLoggable and for
     * logging the timings.
     */
    private String mTag;
    /** A label to be included in every log. */
    private String mLabel;

    /**
     * Create and initialize a TimingLogger object that will log using
     * the specific tag. If the Log.isLoggable is not enabled to at
     * least the Log.VERBOSE level for that tag at creation time then
     * the addSplit and dumpToLog call will do nothing.
     * @param tag the log tag to use while logging the timings
     * @param label a string to be displayed with each log
     */
    public TimingLogger(String tag, String label) {
        reset(tag, label);
    }

    public void reset(String tag, String label) {
        mTag = tag;
        mLabel = label;
        reset();
    }

    public void reset() {
//        mDisabled = !Log.isLoggable(mTag, Log.VERBOSE);
        if (mSplits == null) {
            mSplits = new ArrayList<>();
            mSplitLabels = new ArrayList<>();
        } else {
            mSplits.clear();
            mSplitLabels.clear();
        }
        addSplit(null);
    }

    public void addSplit(String splitLabel) {
        long now = SystemClock.elapsedRealtime();
        mSplits.add(now);
        mSplitLabels.add(splitLabel);
    }

    public void dumpToLog() {
        StringBuilder sb = new StringBuilder();
//        callLogger(mTag, mLabel + ": begin");
        sb.append(mLabel).append(":begin").append("\n");
        final long first = mSplits.get(0);
        long now = first;
        for (int i = 1; i < mSplits.size(); i++) {
            now = mSplits.get(i);
            final String splitLabel = mSplitLabels.get(i);
            final long prev = mSplits.get(i - 1);

//            callLogger(mTag, mLabel + ":      " + (now - prev) + " ms, " + splitLabel);
            sb.append(mLabel).append(":      ").append(now - prev).append(" ms, ")
                    .append(splitLabel)
                    .append("\n");
        }
//        callLogger(mTag, mLabel + ": end, " + (now - first) + " ms");
        sb.append(mLabel).append(": end, ").append(now - first).append(" ms")
                .append("\n");
        callLogger(mTag, sb.toString());
    }

    private void callLogger(String tag, String msg){
        PLog.level(Log.DEBUG).tag(tag).msg(msg).execute();
    }
}
