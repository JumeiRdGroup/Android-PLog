package org.mym.prettylog;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import org.mym.plog.AbsPrinter;
import org.mym.plog.Category;
import org.mym.plog.Formatter;
import org.mym.plog.PrintLevel;
import org.mym.plog.Printer;
import org.mym.plog.SoftWrapper;
import org.mym.plog.Style;
import org.mym.plog.formatter.DefaultFormatter;

/**
 * This is a sample of customizing view printer.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
public class TextViewPrinter extends AbsPrinter {

    private TextView mTextView;
    private Handler mHandler;

    public TextViewPrinter(@NonNull TextView mTextView) {
        this.mTextView = mTextView;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean onIntercept(@PrintLevel int level, @NonNull String tag, @Nullable Category
            category, @NonNull String msg) {
        //intercept nothing
        return false;
    }

    @Nullable
    @Override
    public Formatter getFormatter() {
        //we can explicit set formatter here
        return new DefaultFormatter();
    }

    @Override
    public void print(final @PrintLevel int level, final @NonNull String tag,
                      final @NonNull String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(createPrintText(level, tag, msg));
            }
        });
    }

    private String createPrintText(final int level, final String tag, final String msg) {
        String levelChar = "V";
        switch (level) {
            case Log.VERBOSE:
                levelChar = "V";
                break;
            case Log.DEBUG:
                levelChar = "D";
                break;
            case Log.INFO:
                levelChar = "I";
                break;
            case Log.WARN:
                levelChar = "W";
                break;
            case Log.ERROR:
                levelChar = "E";
                break;
            case Log.ASSERT:
                levelChar = "A";
                break;
        }
        return String.format("%s/%s: %s", levelChar, tag, msg);
    }
}
