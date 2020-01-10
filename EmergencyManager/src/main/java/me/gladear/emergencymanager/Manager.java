package me.gladear.emergencymanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

            if (sensor == null) {
                var geolocation = payload.getJSONObject("geolocation");
                var lat = geolocation.getDouble("lat");
                var lon = geolocation.getDouble("lon");

                var geoloc = new Geolocation(lat, lon);
                sensor = new Sensor(id, geoloc);

                this.sensors.put(id, sensor);
            }

            if (intensity <= 0) {
                // Free the sensor
                this.sensors.remove(id);

                // Sensors and trucks are sorted by decreasing intensity / capacity
                var inNeed = this.findSortedSensorsInNeed().iterator();
                var available = sensor.getTrucks();

                while (inNeed.hasNext() && !available.isEmpty()) {
                    var nextSensor = inNeed.next();

                    var required = findSuitableTrucks(available,
                            nextSensor.getIntensity() - nextSensor.getTrucksCapacity());
                    this.sendTrucks(required, nextSensor);
                }

                sensor.release();

                return;
            }

            // Intensity may be used later to know
            // if the sensor requires trucks to come
            sensor.setIntensity(intensity);

            if (!sensor.requireHelp()) {
                return;
            }

            // Get trucks from the server
            var trucks = this.getTrucks();

            // Filter available trucks
            var available = trucks.stream().filter(truck -> truck.available).collect(Collectors.toList());

            var toSend = findSuitableTrucks(available, intensity - sensor.getTrucksCapacity());
            this.sendTrucks(toSend, sensor);
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
            var capacity = object.getInt("capacity");
            var available = object.getBoolean("available");

            var truck = new Truck(id, new Geolocation(lat, lon), capacity, available);
            trucks.add(truck);
        }

        return trucks;
    }

    private Stream<Sensor> findSortedSensorsInNeed() {
        return this.sensors.values().stream().filter(Sensor::requireHelp)
                .sorted((sensor, other) -> sensor.getIntensity() - other.getIntensity());
    }

    private void sendTruck(Truck truck, Sensor sensor) {
        // Add the truck to the trucks assigned to the sensor
        sensor.addTruck(truck);

        // Send "send_truck" action to client
        var msg = new JSONObject();

        msg.put("truck_id", truck.id);
        msg.put("sensor_id", sensor.id);

        this.client.sendMessage(WSUtils.createMessage("send_truck", msg));
    }

    private void sendTrucks(Collection<Truck> trucks, Sensor sensor) {
        trucks.forEach((truck) -> this.sendTruck(truck, sensor));
    }

    private static Set<Truck> findSuitableTrucks(Collection<Truck> trucks, int capacity) {
        var available = sort(trucks);
        var required = new HashSet<Truck>();
        var currentCapacity = 0;

        while (currentCapacity < capacity && !available.isEmpty()) {
            var truck = findTruckWithLowestCapacity(available, capacity - currentCapacity);
            currentCapacity += truck.capacity;
            available.remove(truck);
            required.add(truck);
        }

        return required;
    }

    /**
     * Returns the truck with highest capacity available if none is enough.
     *
     * @param trucks      - List of trucks sorted by increasing capacity
     * @param minCapacity - Minimum required capacity for the truck
     */
    private static Truck findTruckWithLowestCapacity(List<Truck> trucks, int minCapacity) {
        return trucks.stream().filter(truck -> truck.capacity >= minCapacity).findFirst()
                .orElse(trucks.get(trucks.size() - 1));
    }

    private static List<Truck> sort(Collection<Truck> trucks) {
        return trucks.stream().sorted((truck, other) -> other.capacity - truck.capacity).collect(Collectors.toList());
    }
}
