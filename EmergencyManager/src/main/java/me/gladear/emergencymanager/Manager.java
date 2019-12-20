package me.gladear.emergencymanager;

import org.json.JSONException;
import org.json.JSONObject;

import me.gladear.emergencymanager.comm.WebSocketClientEndpoint;

class Manager implements WebSocketClientEndpoint.MessageHandler {
    private final String ACTION_FIRE_UPDATE = "fire_update";

    private final WebSocketClientEndpoint client;

    public Manager(WebSocketClientEndpoint client) {
        this.client = client;
    }

    @Override
    public void handleMessage(String message) {
        try {
            var object = new JSONObject(message);
            var action = object.getString("action");
            var payload = object.get("payload");

            this.handleAction(action, payload);
        } catch (JSONException e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
            System.out.println("Message: " + message);
        }
    }

    private void handleAction(String action, Object payload) {
        switch (action) {
            case ACTION_FIRE_UPDATE:
                System.out.println(payload.toString());
                break;
            default:
                System.out.println("Unknown action: " + action);
                break;
        }
    }
}
