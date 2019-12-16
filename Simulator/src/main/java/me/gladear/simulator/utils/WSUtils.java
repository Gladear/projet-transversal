package me.gladear.simulator.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class WSUtils {
    private WSUtils() {
    }

    public static String createMessage(String action, Object[] payload) {
        var obj = new JSONObject();
        obj.put("action", action);

        var array = new JSONArray(payload);
        obj.put("payload", array);

        return obj.toString();
    }

    public static String createMessage(String action, Object payload) {
        var obj = new JSONObject();
        obj.put("action", action);

        var object = new JSONObject(payload);
        obj.put("payload", object);

        return obj.toString();
    }
}
