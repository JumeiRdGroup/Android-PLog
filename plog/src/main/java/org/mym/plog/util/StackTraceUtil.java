package org.mym.plog.util;

import android.text.TextUtils;

/**
 * This is a helper class to get current class name, methods, etc.
 * Created by muyangmin on Sep 09, 2016.
 *
 * @author muyangmin
 * @since 1.5.0
 */
public class StackTraceUtil {

    /**
     * Generate auto tag for logs.
     *
     * @param stackOffset stack offset, started from 0.
     * @return auto tag
     */
    public static String generateAutoTag(int stackOffset) {
        StackTraceElement[] currentStack = getCurrentStack();
        StackTraceElement element = currentStack[stackOffset];
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

    /**
     * Generate stack information by current stackOffset.
     *
     * @param keepInnerClass if set to true, then inner class information is inserted between line
     *                       number and method name.
     * @param stackOffset    stack offset, started from 0.
     * @return see the descriptions of params.
     */
    public static String generateStackInfo(boolean keepInnerClass, int stackOffset) {
        StackTraceElement[] currentStack = getCurrentStack();

        StackTraceElement element = currentStack[stackOffset];
        String className = element.getClassName();
        //parse to simple name
        String pkgPath[] = className.split("\\.");
        if (pkgPath.length > 0) {
            className = pkgPath[pkgPath.length - 1];
        }

        //If log in inner class, then class name contains '$', which cause IDE navigate file
        // function not working.
        int innerclassSymbolIndex = className.indexOf("$");
        //is inner class
        String innerClassName = null;
        if (innerclassSymbolIndex != -1) {
            //skip the first symbol
            innerClassName = className.substring(innerclassSymbolIndex + 1);
            className = className.substring(0, innerclassSymbolIndex);
        }

        String methodName = element.getMethodName();
        int lineNum = element.getLineNumber();
        //concat inner classname in method string.
        if (keepInnerClass && (!TextUtils.isEmpty(innerClassName))) {
            methodName = String.format("$%s#%s()", innerClassName, methodName);
        } else {
            methodName = String.format("#%s()", methodName);
        }

        return String.format("[(%s.java:%s)%s]", className, lineNum, methodName);
    }

    public static StackTraceElement getLogStackElement(int stackOffset){
        StackTraceElement[] currentStack = getCurrentStack();
        if (stackOffset >= currentStack.length || currentStack[stackOffset]==null) {
            return null;
        }

        StackTraceElement element = currentStack[stackOffset];
        String className = element.getClassName();
        //parse to simple name
        String pkgPath[] = className.split("\\.");
        if (pkgPath.length > 0) {
            className = pkgPath[pkgPath.length - 1];
        }
        StackTraceElement parsed = new StackTraceElement(className,
                element.getMethodName(), element.getFileName(), element.getLineNumber());
        return parsed;
    }

    private static StackTraceElement[] getCurrentStack() {
        return Thread.currentThread().getStackTrace();
    }
}
