package org.mym.prettylog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

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
public class TextViewPrinter implements Printer {

    private static final String DISCLAIMER = "NOTE: This view printer is just for fun, please see logcat " +
            "or file output to get a practical experience.\n\n";
    private TextView mTextView;
    private StringBuilder mBuffer;

    public TextViewPrinter(@NonNull TextView mTextView) {
        this.mTextView = mTextView;
        mTextView.setText(DISCLAIMER);
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
        return new DefaultFormatter();
    }

    @Nullable
    @Override
    public Style getStyle() {
        return null;
    }

//    @Override
//    public boolean isSoftWrapDisallowed() {
//        return false;
//    }

    @Override
    public SoftWrapper getSoftWrapper() {
        return null;
    }

    @Override
    public int getMaxLengthPerLine() {
        return 0;
    }

    @Override
    public void print(@PrintLevel int level, @NonNull String tag, @NonNull String msg) {
        if (mBuffer == null || mBuffer.length() + msg.length() > DISCLAIMER.length() + 300) {
            mBuffer = new StringBuilder().append(DISCLAIMER);
        }
        mBuffer.append(msg).append("\n");
        mTextView.setText(mBuffer);
    }
}
