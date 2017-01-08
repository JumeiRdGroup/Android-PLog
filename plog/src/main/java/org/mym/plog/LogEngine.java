package org.mym.plog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.mym.plog.config.PLogConfig;
import org.mym.plog.util.ObjectUtil;
import org.mym.plog.util.StackTraceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the core logic for PLog 2.0: it contains dependencies of almost all interfaces, and
 * decide how to use them.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
final class LogEngine {
    private static final int STACK_TRACE_INDEX = 6;

    private static List<Printer> mPrinters = new ArrayList<>();

    /*package*/ static void setPrinters(Printer... printers) {
        if (printers != null && printers.length > 0) {
            mPrinters.clear();
            mPrinters = Arrays.asList(printers);
        }
    }

    /*package*/ static void handleLogRequest(LogRequest request) {
        Log.i("Stub!", ObjectUtil.objectToString(request));

        //Check tag
        PLogConfig config = PLog.getCurrentConfig();
        String tag = request.getTag();
        //Checking for auto tag
        if (TextUtils.isEmpty(tag) && config.isUseAutoTag()) {
            int offset = STACK_TRACE_INDEX + config.getGlobalStackOffset() + request
                    .getStackOffset();
            tag = StackTraceUtil.generateAutoTag(offset);
        }
        //Only concat when tag is not empty and config is specified to true
        if ((!TextUtils.isEmpty(tag)) && config.isForceConcatGlobalTag()) {
            tag = config.getGlobalTag() + "-" + tag;
        }
        //If still empty, using global
        else if (TextUtils.isEmpty(tag)) {
            tag = config.getGlobalTag();
        }

        for (Printer printer : mPrinters) {

            //Check ignore
            if (printer.intercept(request.getLevel(), tag, request.getCategory(),
                    request.getMsg())) {
                //Skip and ignore this log
                continue;
            }

            //Format if allowed
            String content = request.getMsg();
            if (printer.getFormatter() != null) {
                try {
                    content = printer.getFormatter().format(request.getMsg(), request.getParams());
                } catch (Exception ignored) {

                }
            }

            Style style = printer.getStyle();
            if (style == null) {
                style = DefaultStyle.INSTANCE;
            }

            //SoftWrap if allowed
            if (!printer.disallowSoftWrap()) {
                content = wrapLine(content, config.getMaxLengthPerLine(), style);
            }

            if (!TextUtils.isEmpty(style.msgPrefix())) {
                content = style.msgPrefix() + content;
            }
            if (!TextUtils.isEmpty(style.msgSuffix())) {
                content = content + style.msgSuffix();
            }

            //call printer at last
            printer.print(request.getLevel(), tag, content);
        }
    }

    /**
     * Soft wrap line rule implementation.
     *
     * @param logContent       log to be printed, NOT NULL
     * @param maxLengthPerLine max length
     * @return wrapped log
     */
    private static String wrapLine(String logContent, int maxLengthPerLine, @NonNull Style style) {
        //Safety Check
        assert logContent != null;

        if (logContent.isEmpty()) { // Not need to to StringBuilder and while loop
            return logContent;
        }

        String lineHeader = style.lineHeader();
        if (lineHeader == null) {
            lineHeader = "";
        }

        int currentIndex = 0;
        //Use a StringBuilder to build multi line text but print only once, solve #6
        StringBuilder sb = new StringBuilder(logContent.length()
                + logContent.length() / maxLengthPerLine); //plus \n symbol

        while (currentIndex < logContent.length()) {
            //compute max length of this line
            int currentLineLength = Math.min(maxLengthPerLine,
                    logContent.length() - currentIndex - lineHeader.length());

            //Force new line if \n appears, otherwise use our soft wrap.
            String subLine;

            int newlineIndex = logContent.indexOf("\n", currentIndex);
            int thisLineEnd = currentIndex + currentLineLength;

            //has \n in this line;
            if (newlineIndex != -1 && newlineIndex < thisLineEnd) {
                subLine = logContent.substring(currentIndex, newlineIndex);
                currentIndex = newlineIndex + 1;
            } else {
                subLine = logContent.substring(currentIndex, thisLineEnd);
                currentIndex = thisLineEnd;
            }

            //Not print yet, only append.
            sb.append(lineHeader).append(subLine);
            //Has more chars
            if (currentIndex < logContent.length()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static class DefaultStyle implements Style {

        /*package*/ static DefaultStyle INSTANCE = new DefaultStyle();

        @Nullable
        @Override
        public String msgPrefix() {
            return null;
        }

        @Nullable
        @Override
        public String msgSuffix() {
            return null;
        }

        @Nullable
        @Override
        public String lineHeader() {
            return null;
        }
    }
}
