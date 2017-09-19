package org.mym.plog;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Print all message to logcat.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DebugPrinter extends AbsPrinter {

    private boolean isDebug;

    public DebugPrinter(boolean isDebug) {
        this(isDebug, 100);
    }

    public DebugPrinter(boolean isDebug, int maxLengthPerLine) {
        super(maxLengthPerLine);
        this.isDebug = isDebug;
    }

    @CheckResult
    @Override
    public boolean onIntercept(@PrintLevel int level, @NonNull String tag,
                               @Nullable Category category, @NonNull String msg) {
        //Intercept all logs if not in debug
        return !isDebug || super.onIntercept(level, tag, category, msg);
    }

    @SuppressLint("LogNotPLog")
    @Override
    public void print(@PrintLevel int level, @NonNull String tag, @NonNull String msg) {
        if (msg.length() > 4000) {
            //log an warning message before chunk
            PLog.w("Your message is longer than 4K chars; log will be truncated as parts!");

            int chunkSize = 3800;
            int chunkCount = msg.length() / chunkSize;     // integer division
            //fix last part if exist
            if (chunkCount * chunkSize < msg.length()) {
                chunkCount++;
            }

            for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
                int max = chunkSize * (chunkIndex + 1);
                if (max <= msg.length()) {
                    print(level, tag, "Chunk " + (chunkIndex + 1) + " / " + chunkCount + ":" +
                            msg.substring(chunkIndex * chunkSize, max));

                    SystemClock.sleep(32);

                } else {
                    print(level, tag, "Chunk " + (chunkIndex + 1) + " / " + chunkCount + ":" +
                            msg.substring(chunkIndex * chunkSize));
                }
            }
            return;
        }


        switch (level) {
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.ASSERT:
                Log.wtf(tag, msg);
                break;
        }
    }
}
