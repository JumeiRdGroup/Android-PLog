package org.mym.plog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.mym.plog.config.PLogConfig;

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
    private static final int STACK_TRACE_INDEX = 5;

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

    /*package*/
    static void appendPrinter(@NonNull Printer printer) {
        mPrinters.add(printer);
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

        Category category = request.getCategory();
        if (category != null && !TextUtils.isEmpty(category.getName())) {
            tag = String.format("%s[Category:%s]", tag, category.getName());
        }

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
            //If no formatter specified but has param, just toString.
            if (printer.getFormatter() == null && content == null) {
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
            if (printer.getFormatter() != null) {
                try {
                    content = printer.getFormatter().format(request.getMsg(), request.getParams());
                } catch (Exception ignored) {
                    //TODO consider remove this on release version
                    ignored.printStackTrace();
                }
            }

            Style style = printer.getStyle();
            if (style == null) {
                style = DefaultStyle.INSTANCE;
            }

            //SoftWrap if allowed
            if (printer.getSoftWrapper() != null) {
//                content = wrapLine(content, config.getMaxLengthPerLine(), style);
                if (printer.getMaxLengthPerLine() <= 0) {
                    throw new IllegalArgumentException("max length should be a positive integer!");
                } else {
                    content = printer.getSoftWrapper().wrapLine(content,
                            printer.getMaxLengthPerLine());
                }
//                content = WordBreakWrapper.wrap(content, config.getMaxLengthPerLine());
            }

            StringBuilder outputSb = new StringBuilder(content.length() * 2);
            if (config.isKeepLineNumber() && element!=null) {
                outputSb.append(generateLineInfo(element));
            }

            if (!TextUtils.isEmpty(style.msgPrefix())) {
//                content = style.msgPrefix() + content;
                outputSb.append(style.msgPrefix());
            }
            outputSb.append(content);
            if (!TextUtils.isEmpty(style.msgSuffix())) {
//                content = content + style.msgSuffix();
                outputSb.append(style.msgSuffix());
            }

            //call printer at last
            printer.print(request.getLevel(), tag, outputSb.toString());
        }
    }

    @NonNull
    private static String prepareFinalTag(PLogConfig config, String explicitTag, @Nullable StackTraceElement element) {
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

    private static String generateLineInfo(@NonNull StackTraceElement element){
        return String.format("[(%s:%s):%s]",
                TextUtils.isEmpty(element.getFileName()) ? "Unknown Source" : element.getFileName(),
                element.getLineNumber(),
                element.getMethodName());
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
