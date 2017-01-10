package org.mym.plog.formatter;

import org.mym.plog.Formatter;
import org.mym.plog.formatter.util.ObjectUtil;

/**
 * This class JUST format pure OBJECTS.
 * Created by muyangmin on Sep 09, 2016.
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
        if (params.length == 1) {
            return ObjectUtil.objectToString(params[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            sb.append("param[")
                    .append(i)
                    .append("]=")
                    .append(ObjectUtil.objectToString(params[i]))
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
