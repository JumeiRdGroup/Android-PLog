package org.mym.plog.formatter;

import android.text.TextUtils;

import org.mym.plog.Formatter;
import org.mym.plog.util.ObjectUtil;

/**
 * This class just formats msg
 * Created by muyangmin on Sep 09, 2016.
 *
 * @author muyangmin
 * @since 1.5.0
 */
public class StringFormatter implements Formatter {

    //For empty message but with object params
    private ObjectFormatter FMT_OBJECT = new ObjectFormatter();

    @Override
    public String format(String msg, Object... params) throws Exception {
        if (TextUtils.isEmpty(msg)) {
            if (params == null || params.length == 0) {
                throw new IllegalArgumentException("Neither msg nor params provided!");
            }
            return FMT_OBJECT.format(msg, params);
        }
        Object[] objects = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Object obj = params[i];
            //Wrapper classes shouldn't change to String, because maybe we need format numbers;
            // e.g %.2f
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
//
//    @Override
//    public boolean isPreWrappedFormat() {
//        return false;
//    }
}
