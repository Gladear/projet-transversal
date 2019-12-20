package me.gladear.emergencymanager.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class WSUtils {
    private WSUtils() {
    }

    public static String createMessage(String action, Object payload) throws JSONException {
        var obj = new JSONObject();

        obj.put("action", action);
        obj.put("payload", payload);

        return obj.toString();
    }
}
