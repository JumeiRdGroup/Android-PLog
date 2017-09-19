package org.mym.plog.formatter;

import org.mym.plog.Formatter;
import org.mym.plog.PLog;
import org.mym.plog.formatter.util.ObjectUtil;

/**
 * Recursively format all type of objects.
 *
 * @author muyangmin
 * @since 1.5.0
 */
public class ObjectFormatter implements Formatter {

    /**
     * @param msg IGNORED!
     */
    @Override
    public String format(String msg, Object... params) throws Exception {
        if (params.length < 1) {
            throw new IllegalArgumentException("No objects need to be formatted!");
        }

        int maxRecursiveDepth = PLog.getCurrentConfig().getMaxRecursiveDepth();

        if (params.length == 1) {
            return ObjectUtil.objectToString(params[0], false, maxRecursiveDepth);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            sb.append("param[")
                    .append(i)
                    .append("]=")
                    .append(ObjectUtil.objectToString(params[i], false, maxRecursiveDepth))
                    .append("\n")
            ;
        }
        return sb.toString();
    }
//
//    @Override
//    public boolean isPreWrappedFormat() {
//        return false;
//    }
}
