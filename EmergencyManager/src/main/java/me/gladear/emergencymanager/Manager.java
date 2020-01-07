package me.gladear.emergencymanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import me.gladear.emergencymanager.comm.WebSocketClientEndpoint;
import me.gladear.emergencymanager.model.Geolocation;
import me.gladear.emergencymanager.model.Sensor;
import me.gladear.emergencymanager.model.Truck;
import me.gladear.emergencymanager.utils.HttpUtils;
import me.gladear.emergencymanager.utils.WSUtils;

class Manager implements WebSocketClientEndpoint.MessageHandler {
    private final String ACTION_FIRE_UPDATE = "fire_update";

    private final WebSocketClientEndpoint client;
    private final Map<Integer, Sensor> sensors;

    public Manager(WebSocketClientEndpoint client) {
        this.client = client;
        this.sensors = new HashMap<>();
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
                this.manageFireUpdate((JSONObject) payload);
                break;
            default:
                System.out.println("Unknown action: " + action);
                break;
        }
    }

    private void manageFireUpdate(JSONObject payload) {
        try {
            var id = payload.getInt("id");
            var intensity = payload.getInt("intensity");

            var sensor = this.sensors.get(id);
            var exists = sensor != null;

            if (!exists) {
                var geolocation = payload.getJSONObject("geolocation");
                var lat = geolocation.getDouble("lat");
                var lon = geolocation.getDouble("lon");

                var geoloc = new Geolocation(lat, lon);
                sensor = new Sensor(id, geoloc);

                this.sensors.put(id, sensor);
            }

            // Intensity may be used later to know
            // if the sensor requires trucks to come
            sensor.setIntensity(intensity);

            if (intensity <= 0) {
                // TODO The truck is available for some other fire
                return;
            }

            if (exists) {
                return;
            }

            // Get trucks from the server
            var trucks = this.getTrucks();

            // Filter available trucks
            var available = trucks.stream()
                .filter(truck -> truck.available)
                .collect(Collectors.toList());

            // If no truck is available,
            // we'll wait for one to be,
            // and handle the case above
            if (available.isEmpty()) {
                return;
            }

            // Compute which truck is to be sent
            var sent = available.get(0);

            // Send a message to the server telling them
            // which truck to send
            var msg = new JSONObject();

            msg.put("id", sent.id);

            var geojson = new JSONObject();
            geojson.put("lat", sensor.geolocation.lat);
            geojson.put("lon", sensor.geolocation.lon);

            msg.put("geolocation", geojson);

            this.client.sendMessage(
                WSUtils.createMessage("send_truck", msg)
            );
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Truck> getTrucks() throws IOException, JSONException {
        var url = App.dotenv.get("SERVER_API") + "/trucks";
        var data = HttpUtils.getJSONArray(url);

        var trucks = new ArrayList<Truck>(data.length());

        for (var item : data) {
            var object = (JSONObject) item;

            var id = object.getInt("id");
            var geolocation = object.getJSONObject("geolocation");
            var lat = geolocation.getDouble("lat");
            var lon = geolocation.getDouble("lon");
            var available = object.getBoolean("available");

            var truck = new Truck(id, new Geolocation(lat, lon), available);
            trucks.add(truck);
        }

        return trucks;
    }
}
