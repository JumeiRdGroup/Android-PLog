package org.mym.plog.formatter;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by muyangmin on Sep 09, 2016.
 *
 * @author muyangmin
 * @since 1.5.0
 */
public class JSONFormatter implements Formatter {

    private static final int INTENT_SPACES = 4;

    /**
     * @param params IGNORED BY JSON FORMATTER!
     */
    @Override
    public String format(String msg, Object... params) throws Exception {
        if (msg == null || msg.length() < 2) { // at lease {} or [], so 1 char is absolutely wrong.
            return msg;
        }
        //Guess JSONObject
        if (msg.startsWith("{") && msg.endsWith("}")) {
            //If not a json then JSONException is thrown, do not worry
            JSONObject jsonObject = new JSONObject(msg);
            msg = jsonObject.toString(getIntentSpaces());
        }
        //Guess JSONArray
        else if (msg.startsWith("[") && msg.endsWith("]")) {
            JSONArray jsonArray = new JSONArray(msg);
            msg = jsonArray.toString(getIntentSpaces());
        }
        return msg;
    }

    @Override
    public boolean isPreWrappedFormat() {
        return true;
    }

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
