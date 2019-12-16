package me.gladear.simulator.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class HttpRequest {
    private final URL url;

    public HttpRequest(URL url) {
        this.url = url;
    }

    public String getText() throws IOException {
        var con = this.getConnection("GET");
        con.setRequestProperty("Content-Type", "application/json");

        var content = this.getResponseText(con);
        con.disconnect();

        return content;
    }

    public JSONObject getJSONObject() throws IOException {
        var text = this.getText();
        return new JSONObject(text);
    }

    public JSONArray getJSONArray() throws IOException {
        var text = this.getText();
        return new JSONArray(text);
    }

    private HttpURLConnection getConnection(String method) throws IOException {
        var con = (HttpURLConnection) this.url.openConnection();
        con.setRequestMethod(method);
        return con;
    }

    private String getResponseText(HttpURLConnection con) throws IOException {
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
