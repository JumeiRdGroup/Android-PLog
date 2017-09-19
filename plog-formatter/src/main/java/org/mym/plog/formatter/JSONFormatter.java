package org.mym.plog.formatter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mym.plog.Formatter;

/**
 * Format standard JSON/JSONArray.
 *
 * @since 2.0.0
 */
public class JSONFormatter implements Formatter {

    private static final int INTENT_SPACES = 4;

    @Override
    public String format(String msg, Object... params) throws Exception {
//        if (msg == null || msg.length() < 2) { // at lease {} or [], so 1 char is absolutely
// wrong.
//            return msg;
//        }
//        //Guess JSONObject
//        if (msg.startsWith("{") && msg.endsWith("}")) {
//            //If not a json then JSONException is thrown, do not worry
//            JSONObject jsonObject = new JSONObject(msg);
//            msg = jsonObject.toString(getIntentSpaces());
//        }
//        //Guess JSONArray
//        else if (msg.startsWith("[") && msg.endsWith("]")) {
//            JSONArray jsonArray = new JSONArray(msg);
//            msg = jsonArray.toString(getIntentSpaces());
//        }
        if (params != null && params.length >= 1) {
            //ONLY recognize the first param.
            Object obj = params[0];
            if (obj instanceof JSONObject) {
                msg = ((JSONObject) obj).toString(INTENT_SPACES);
            } else if (obj instanceof JSONArray) {
                msg = ((JSONArray) obj).toString(INTENT_SPACES);
            }
        }
        return msg;
    }
//
//    @Override
//    public boolean isPreWrappedFormat() {
//        return true;
//    }

    /**
     * Indicate how many spaces should be used to intent.
     *
     * @see JSONObject#toString(int)
     * @see JSONArray#toString(int)
     */
    protected int getIntentSpaces() {
        return INTENT_SPACES;
    }
}
