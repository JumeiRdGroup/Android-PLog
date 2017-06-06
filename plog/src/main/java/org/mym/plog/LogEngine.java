package org.mym.plog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.mym.plog.config.PLogConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * This is the core logic for PLog 2.0: it contains dependencies of almost all interfaces, and
 * decide how to use them.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
final class LogEngine {
    private static final int STACK_TRACE_INDEX = 5;

    private static boolean HAS_WARN_NO_PRINTERS = false;

    private static List<Printer> mPrinters = new ArrayList<>();

    /*package*/
    static void setPrinters(Printer... printers) {
        if (printers != null && printers.length > 0) {
            if (!mPrinters.isEmpty()) {
                mPrinters.clear();
            }
            //Arrays.ArrayList cannot support remove operation thus cannot clear.
//            mPrinters = Arrays.asList(printers);
            Collections.addAll(mPrinters, printers);
        }
    }

    /*package*/
    static void appendPrinter(@NonNull Printer printer) {
        mPrinters.add(printer);
    }

    /*package*/
    static void handleLogRequest(LogRequest request) {
        if (request == null || (!request.isPrintTraceOnly() && TextUtils.isEmpty(request.getMsg())
                && (request.getParams() == null || request.getParams().length == 0))) {
            throw new IllegalArgumentException("Bad request: both msg and param are null/empty!");
        }

        if (mPrinters.isEmpty() && !HAS_WARN_NO_PRINTERS) {
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

        Category category = request.getCategory();
        if (category != null && !TextUtils.isEmpty(category.getName())) {
            tag = String.format("%s[Category:%s]", tag, category.getName());
        }

        //If intercepted by global interceptor, just do nothing and return
        String safeMsgForIntercept = request.getMsg() == null ? "" : request.getMsg();
        if ((config.getGlobalInterceptor() != null && config.getGlobalInterceptor().onIntercept
                (request.getLevel(), tag, request.getCategory(), safeMsgForIntercept))) {
            return;
        }

        for (Printer printer : mPrinters) {

            //Check intercept result
            if (printer.onIntercept(request.getLevel(), tag, request.getCategory(),
                    safeMsgForIntercept)) {
                //Skip and ignore this log
                continue;
            }

            // ---------- Build formatted(if need) content for this printer ----------

            //Format if allowed
            String content = request.getMsg();

            if (request.isPrintTraceOnly()) {
                StackTraceElement[] stack = getLogStackForDiagnosis();
                StringBuilder sb = new StringBuilder();
                if (stack == null) {
                    sb.append("Stack trace unavailable!");
                } else {
                    sb.append(String.format(Locale.US,
                            "FYI: Library offset = %d, globalOffset = %d, requestOffset = %d.\n",
                            STACK_TRACE_INDEX, config.getGlobalStackOffset(), request
                                    .getStackOffset()));
                    sb.append("Tips: VMStack(0) maybe not shown on some cases.\n");
                    for (int i = 1; i < stack.length; i++) {
                        StackTraceElement traceElement = stack[i];
                        sb.append(String.format(Locale.US, "\t%2d  %s\n",
                                i, traceElement));
                    }
                }
                content = sb.toString();
            }

            //If no formatter specified but has param, just toString.
            else if (printer.getFormatter() == null && content == null) {
                StringBuilder sb = new StringBuilder();
                Object[] params = request.getParams();
                for (int i = 0; i < params.length; i++) {
                    sb.append("param[")
                            .append(i)
                            .append("]=")
                            .append(params[i])
                            .append("\n")
                    ;
                }
                content = sb.toString();
            }

            //Else: assume formatter can handle all cases
            else if (printer.getFormatter() != null) {
                try {
                    content = printer.getFormatter().format(request.getMsg(), request.getParams());
                } catch (Exception ignored) {
                    if (BuildConfig.DEBUG) {
                        ignored.printStackTrace();
                    }
                }
            }

            // ---------- Soft wrap(if need) content for this printer ----------
            Style style = printer.getStyle();
            if (style == null) {
                style = DefaultStyle.INSTANCE;
            }

            //SoftWrap if allowed
            if (printer.getSoftWrapper() != null) {
                if (printer.getMaxLengthPerLine() <= 0) {
                    throw new IllegalArgumentException("max length should be a positive integer!");
                } else {
                    content = printer.getSoftWrapper().wrapLine(content,
                            printer.getMaxLengthPerLine());
                }
            }

            // ---------- Build Final content for this printer ----------

            StringBuilder outputSb = new StringBuilder(content.length() * 2);
            if (config.isNeedLineNumber() && element != null) {
                outputSb.append(generateLineInfo(element));
            }

            if (config.isNeedThreadInfo()) {
                outputSb.append(generateThreadInfo(Thread.currentThread()));
            }

            if (!TextUtils.isEmpty(style.msgPrefix())) {
                outputSb.append(style.msgPrefix());
            }
            outputSb.append(content);
            if (!TextUtils.isEmpty(style.msgSuffix())) {
                outputSb.append(style.msgSuffix());
            }

            // ---------- Call printer at last ----------
            printer.print(request.getLevel(), tag, outputSb.toString());
        }
    }

    @NonNull
    private static String prepareFinalTag(PLogConfig config, String explicitTag, @Nullable
            StackTraceElement element) {
        String tag = explicitTag;
        //Checking for auto tag
        if (TextUtils.isEmpty(tag) && config.isUseAutoTag() && element != null) {
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
        if (stack == null || stack.length <= offset) {
            return null;
        }
        return stack[offset];
    }

    /**
     * Note this is a emulated method to keep same level with user calling; normally user code
     * just call {@link #getLogStackElement(int)}.
     *
     * @see #getLogStackElement(int)
     */
    @Nullable
    private static StackTraceElement[] getLogStackForDiagnosis() {
        return Thread.currentThread().getStackTrace();
    }

    private static String generateAutoTag(@NonNull StackTraceElement element) {
        String className = element.getClassName();
        //parse to simple name
        String pkgPath[] = className.split("\\.");
        if (pkgPath.length > 0) {
            className = pkgPath[pkgPath.length - 1];
        }


        //IMPORTANT:
        // 因为Java语法允许在匿名类中继续包含具名的子类,因此必须逆序遍历,但是lastIndex方法没有endIndex参数。
        // 所以只能反向遍历, 每次截取最后一段执行subString, 如果全是数字,则继续往前遍历。
        // Since nested inner class in anonymous is allowed, here we must do reversal traverse
        // for the string.
        StringBuilder sbInnerClass = new StringBuilder();
        int index;
        String strLoop = className;
        while ((index = strLoop.lastIndexOf("$")) != -1) {
            String piece = strLoop.substring(index + 1); //skip dollar
            sbInnerClass.insert(0, piece);
            //Careful: if only judge 0-9, then A$1$2$3 case would get unexpected answer 2$3.
            if (!piece.matches("[0-9$]+")) {
                break;
            }
            //still anonymous class, continue loop
            sbInnerClass.insert(0, "$");
            //truncate last piece
            strLoop = strLoop.substring(0, index);
        }
        //delete first leading dollar
        if (sbInnerClass.length() > 0 && sbInnerClass.charAt(0) == '$') {
            sbInnerClass.deleteCharAt(0);
        }
        String innerClassName = sbInnerClass.toString();
        //This happens on class like MainActivity$1.
        if (TextUtils.isDigitsOnly(innerClassName)) {
            //Reset; use full name instead.
            innerClassName = null;
        }

        return TextUtils.isEmpty(innerClassName) ? className : innerClassName;
    }

    private static String generateLineInfo(@NonNull StackTraceElement element) {
        return String.format("[(%s:%s):%s]",
                TextUtils.isEmpty(element.getFileName()) ? "Unknown Source" : element.getFileName(),
                element.getLineNumber(),
                element.getMethodName());
    }

    @NonNull
    private static String generateThreadInfo(@Nullable Thread thread) {
        return thread == null ? " [Thread:N/A] " : " [" + thread.toString() + "] ";
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
