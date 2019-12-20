package me.gladear.emergencymanager.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtils {
    private HttpUtils() {}

    public static String getText(String url) throws IOException {
        var conn = createConnection(url, "GET");
        var content = getResponseText(conn);
        conn.disconnect();

        return content;
    }

    public static JSONObject getJSONObject(String url) throws IOException, JSONException {
        return new JSONObject(
            getText(url)
        );
    }

    public static JSONArray getJSONArray(String url) throws IOException, JSONException {
        return new JSONArray(
            getText(url)
        );
    }

    private static HttpURLConnection createConnection(String urlStr, String method) throws IOException {
        var url = new URL(urlStr);
        var conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        return conn;
    }

    private static String getResponseText(HttpURLConnection con) throws IOException {
        var in = new BufferedReader(
            new InputStreamReader(con.getInputStream())
        );

        var inputLine = "";
        var buffer = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }

        in.close();

        return buffer.toString();
    }
}
