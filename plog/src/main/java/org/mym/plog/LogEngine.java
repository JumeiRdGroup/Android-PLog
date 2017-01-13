package org.mym.plog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.mym.plog.config.PLogConfig;
import org.mym.plog.util.StackTraceUtil;
import org.mym.plog.util.WordUtil;

import java.security.interfaces.ECKey;
import java.util.ArrayList;
import java.util.Collections;
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

    private static boolean HAS_WARN_NO_PRINTERS = false;

    private static List<Printer> mPrinters = new ArrayList<>();

    /*package*/ static void setPrinters(Printer... printers) {
        if (printers != null && printers.length > 0) {
            if (!mPrinters.isEmpty()) {
                mPrinters.clear();
            }
            //Arrays.ArrayList cannot support remove operation thus cannot clear.
//            mPrinters = Arrays.asList(printers);
            Collections.addAll(mPrinters, printers);
        }
    }

    /*package*/ static void handleLogRequest(LogRequest request) {
        if (request == null || (TextUtils.isEmpty(request.getMsg())
                && (request.getParams() == null || request.getParams().length == 0))) {
            throw new IllegalArgumentException("Bad request: both msg and param are null/empty!");
        }

        if (mPrinters.isEmpty() && !HAS_WARN_NO_PRINTERS){
            mPrinters.add(new DebugPrinter(true));
            PLog.e("No printer prepared, did you forgot it?");
            HAS_WARN_NO_PRINTERS = true;
        }

        //Check tag
        PLogConfig config = PLog.getCurrentConfig();

        int offset = STACK_TRACE_INDEX + config.getGlobalStackOffset() + request
                .getStackOffset();
        StackTraceElement element = getLogStackElement(offset);
        String tag = prepareFinalTag(config, request.getTag(), element);

//        String tag = request.getTag();
//        //Checking for auto tag
//        if (TextUtils.isEmpty(tag) && config.isUseAutoTag()) {
//            tag = StackTraceUtil.generateAutoTag(offset);
//        }
//        //Only concat when tag is not empty and config is specified to true
//        if ((!TextUtils.isEmpty(tag)) && config.isForceConcatGlobalTag()) {
//            tag = config.getGlobalTag() + "-" + tag;
//        }
//        //If still empty, using global
//        else if (TextUtils.isEmpty(tag)) {
//            tag = config.getGlobalTag();
//        }

        for (Printer printer : mPrinters) {

            //Check intercept result
            if (printer.onIntercept(request.getLevel(), tag, request.getCategory(),
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
            if (!printer.isSoftWrapDisallowed()) {
//                content = wrapLine(content, config.getMaxLengthPerLine(), style);
                content = WordUtil.wrap(content, config.getMaxLengthPerLine());
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

    @NonNull
    private static String prepareFinalTag(PLogConfig config, String explicitTag, StackTraceElement element) {
        String tag = explicitTag;
        //Checking for auto tag
        if (TextUtils.isEmpty(tag) && config.isUseAutoTag()) {
            tag = generateAutoTag(element);
        }
        //Only concat when tag is not empty and config is specified to true
        if ((!TextUtils.isEmpty(tag)) && config.isForceConcatGlobalTag()) {
            tag = config.getGlobalTag() + "-" + tag;
        }
        //If still empty, using global
        else if (TextUtils.isEmpty(tag)) {
            tag = config.getGlobalTag();
        }
        return tag;
    }

    @Nullable
    private static StackTraceElement getLogStackElement(int offset) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        //VMStack->ThreadStack->getLogStack->handleLogRequest->LogRequest.execute()
        //->CallerCode.
        if (stack==null || stack.length <= offset){
            return null;
        }
        return stack[offset];
    }

    private static String generateAutoTag(@NonNull StackTraceElement element){
        String className = element.getClassName();
        //parse to simple name
        String pkgPath[] = className.split("\\.");
        if (pkgPath.length > 0) {
            className = pkgPath[pkgPath.length - 1];
        }
        return className;
    }

    private static String generateLineInfo(@NonNull StackTraceElement element){
        return String.format("(%s:%s):%s", element.getFileName(), element.getLineNumber(),
                element.getMethodName());
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
