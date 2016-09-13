package org.mym.plog.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mym.plog.BuildConfig;

import java.lang.reflect.Field;

/**
 * <p>
 * This class provides method to format normal objects to string.
 * </p>
 * Created by muyangmin on 9/6/16.
 *
 * @author muyangmin
 * @since V1.3.0
 */
public class ObjectUtil {

    private static final String STR_OBJECT_EMPTY = "[null object]";
    /**
     * a-zA-Z\. matches for class name, while 0-9a-fA-F matches hashcode.
     * Use $ to match inner classes.
     */
    private static final String REGEX_STANDARD_HASHCODE = "[a-zA-Z\\.$]+@[0-9a-fA-F]+";

    /**
     * intent spaces for json format.
     * @see JSONObject#toString(int)
     * @see org.json.JSONArray#toString(int)
     */
    private static final int JSON_INDENT_SPACE = 4;

    /**
     * Format an object to a well-formed string.
     * <p>
     * If the class of target object overrides {@link Object#toString()}, then this method
     * simply returns the return value of that method. Otherwise it would try to access
     * declared fields and append after class name and hashcode. In this case the final
     * result maybe like this: <br>
     * <code>
     *     org.mym.plog.Driver@23ac3874[mName=Tank, mAge=199, mCar=Benz S400]
     * </code>
     * </p>
     *
     * @param obj the object to log.
     * @return see the doc above.
     */
    public static String objectToString(Object obj) {
        if (obj == null) {
            return STR_OBJECT_EMPTY;
        }
        String result = obj.toString();
        if (result.matches(REGEX_STANDARD_HASHCODE)) {
            result = parseObject(obj);
        }
        if (!TextUtils.isEmpty(result)){
            //Guess JSONObject, use double check to improve accuracy
            if ( result.startsWith("{") && result.endsWith("}")){
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    String fmtJson = jsonObject.toString(JSON_INDENT_SPACE);
                    if (!TextUtils.isEmpty(fmtJson)){
                        result = fmtJson;
                    }
                }catch (JSONException ignored){
                    //This is PLog's BuildConfig; so this was automatically disabled in jcenter release
                    if (BuildConfig.DEBUG){
                        ignored.printStackTrace();
                    }
                }
            }
            //Guess JSONArray
            else if (result.startsWith("[") && result.endsWith("]") && result.indexOf('{') != -1
                    && result.indexOf('}') != -1) {
                try{
                    JSONArray jsonArray = new JSONArray(result);
                    String fmtJson = jsonArray.toString(JSON_INDENT_SPACE);
                    if (!TextUtils.isEmpty(fmtJson)){
                        result = fmtJson;
                    }
                }catch (JSONException ignored){
                    //This is PLog's BuildConfig; so this was automatically disabled in jcenter release
                    if (BuildConfig.DEBUG){
                        ignored.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    private static String parseObject(Object obj) {
        // declare concat symbols here to define final format.
        // Current format is [a=1, b=2]
        final String FIELD_CONCAT_SYMBOL = ", ";
        final String FIELD_VALUE_SYMBOL = "=";
        final String OBJECT_VALUE_SYMBOL_LEFT = "[";
        final String OBJECT_VALUE_SYMBOL_RIGHT = "]";
        try {
            Class clz = obj.getClass();
            Field[] fields = clz.getDeclaredFields();
            StringBuilder sb = new StringBuilder();
//            sb.append(clz.getSimpleName())
//                    .append("@")
//                    .append(obj.hashCode())
            sb.append(obj.toString())
                    .append(OBJECT_VALUE_SYMBOL_LEFT);
            boolean appended = false;
            for (Field f : fields) {
                f.setAccessible(true);
                if (f.isAccessible()) {
                    // age=18,
                    sb.append(f.getName()).append(FIELD_VALUE_SYMBOL);
                    sb.append(f.get(obj));
                    sb.append(FIELD_CONCAT_SYMBOL);
                    appended = true;
                }
            }
            //delete last ", "
            if (appended) {
                sb.delete(sb.length() - FIELD_CONCAT_SYMBOL.length(), sb.length());
            }
            sb.append(OBJECT_VALUE_SYMBOL_RIGHT);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
