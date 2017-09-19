package org.mym.plog.formatter.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mym.plog.BuildConfig;

import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

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
    //Sample complicated class name: AnimalFactory$CatFactory2$CatImpl
    private static final String REGEX_STANDARD_HASHCODE = "[a-zA-Z0-9\\.\\$]+@[0-9a-fA-F]+";

    /**
     * intent spaces for json format.
     * @see JSONObject#toString(int)
     * @see JSONArray#toString(int)
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
     * org.mym.plog.Driver@23ac3874[mName=Tank, mAge=199, mCar=Benz S400]
     * </code>
     * </p>
     *
     * @param obj                        the object to log.
     * @param usingSimpleClassNamePrefix Decide if parsed class using simple name;
     *                                   this is useful for decrease collection string length
     *                                   and large POJOs.
     * @param allowRecursiveDepth        if <=0, recursive format is disabled.
     */
    public static String objectToString(Object obj, boolean usingSimpleClassNamePrefix,
                                        int allowRecursiveDepth) {
        if (obj == null) {
            return STR_OBJECT_EMPTY;
        }
        String result = obj.toString();
        if (result.matches(REGEX_STANDARD_HASHCODE)) {
            result = allowRecursiveDepth <= 0 ? obj.toString()
                    : parseObject(obj, usingSimpleClassNamePrefix, --allowRecursiveDepth);
        } else if (obj instanceof AbstractList && ((AbstractList) obj).size() > 0) {
            //Assume all objects in list are same type, that is, **TYPE SAFE**
            Object flagObject = ((AbstractList) obj).get(0);
            //If this is not formatted class, recursively format
            if (flagObject.toString().matches(REGEX_STANDARD_HASHCODE)) {
                result = formatNormalList((List<?>) obj, usingSimpleClassNamePrefix,
                        allowRecursiveDepth);
            }
        } else if (!TextUtils.isEmpty(result)) {
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

    /**
     * This method is a copy of {@link AbstractCollection#toString()} but changes its parameter!
     * @see #objectToString(Object, boolean, int)
     */
    private static <E> String formatNormalList(List<E> list, boolean usingSimpleClassNamePrefix,
                                               final int allowRecursiveDepth) {
        Iterator<E> it = list.iterator();
        if (!it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (; ; ) {
            E e = it.next();
            sb.append(e == list ? "(this Collection)" : objectToString(e,
                    usingSimpleClassNamePrefix, allowRecursiveDepth));
            if (!it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

    /**
     * @see #objectToString(Object, boolean, int)
     */
    private static String parseObject(Object obj, boolean usingSimpleClassNamePrefix,
                                      final int allowRecursiveDepth) {
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
            if (usingSimpleClassNamePrefix) {
                sb.append(clz.getSimpleName());
            } else {
                sb.append(clz.getName())
                        .append("@")
                        .append(Integer.toHexString(obj.hashCode()));
            }
            //Add [ symbol
            sb.append(OBJECT_VALUE_SYMBOL_LEFT);
            boolean appended = false;
            for (Field f : fields) {
                boolean isAccessible = f.isAccessible();
                f.setAccessible(true);
                if (f.isAccessible()) {
                    // age=18,
                    sb.append(f.getName()).append(FIELD_VALUE_SYMBOL);
                    //In internal fields, do not use full class name to avoid too long log in
                    // arrays and collections, etc.
                    sb.append(objectToString(f.get(obj), true, allowRecursiveDepth));
                    sb.append(FIELD_CONCAT_SYMBOL);
                    appended = true;
                }
                //Restore accessibility
                f.setAccessible(isAccessible);
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
