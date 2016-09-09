package org.mym.plog.formatter;

import org.mym.plog.util.ObjectUtil;

/**
 * This class just formats msg
 * Created by muyangmin on Sep 09, 2016.
 *
 * @author muyangmin
 * @since 1.5.0
 */
public class StringFormatter implements Formatter {

    @Override
    public String format(String msg, Object... params) throws Exception {
        Object[] objects = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Object obj = params[i];
            //Wrapper classes
            if (obj instanceof Boolean || obj instanceof Byte || obj instanceof Short
                    || obj instanceof Integer || obj instanceof Long || obj instanceof Float
                    || obj instanceof Double) { //Void is useless
                objects[i] = obj;
            } else {
                objects[i] = ObjectUtil.objectToString(params[i]);
            }
        }
        return String.format(msg, objects);
    }

    @Override
    public boolean isPreWrappedFormat() {
        return false;
    }
}
