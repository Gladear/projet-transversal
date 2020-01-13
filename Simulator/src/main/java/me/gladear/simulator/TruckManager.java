package me.gladear.simulator;

import java.io.IOException;
import java.net.URI;
import java.util.WeakHashMap;

import org.json.JSONObject;

import me.gladear.simulator.comm.WebSocketClientEndpoint;
import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Station;
import me.gladear.simulator.model.Truck;
import me.gladear.simulator.utils.SensorHolder;

class TruckManager implements Runnable {
    private static final String ACTION_SEND_TRUCK = "send_truck";

    private WebSocketClientEndpoint client;
    private final SensorHolder sensors;
    private WeakHashMap<Integer, TruckHandler> trucks;

    public TruckManager(SensorHolder sensors) {
        this.sensors = sensors;
        this.trucks = new WeakHashMap<>();
    }

    @Override
    public void run() {
        try {
            var url = new URI(App.dotenv.get("SERVER_WS"));
            this.client = new WebSocketClientEndpoint(url);

            this.client.addMessageHandler(message -> {
                var object = new JSONObject(message);

                var action = object.getString("action");
                var payload = object.get("payload");

                try {
                    if (ACTION_SEND_TRUCK.equals(action)) {
                        this.handleSendTruck((JSONObject) payload);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSendTruck(JSONObject payload) throws IOException {
        // Parse data from JSON
        var truckData = payload.getJSONObject("truck");
        var truckId = truckData.getInt("id");

        var from = this.parseGeolocation(truckData.getJSONObject("geolocation"));
        var to = this.parseGeolocation(payload.getJSONObject("geolocation"));

        // Retrieve an eventual existing truck handler
        var truckHandler = this.trucks.get(truckId);

        // Define the new destination of the truck
        var sensor = this.sensors.getNearGeolocation(to);
        
        if (truckHandler == null) {
            var capacity = truckData.getInt("capacity");

            // If no handler is assigned to the truck
            // the truck is at its station, so we create
            // a station at the current location of the truck
            var truck = new Truck(truckId, capacity, new Station(from));

            // Start a thread that will handle the truck
            // during it's whole life
            truckHandler = new TruckHandler(this.client, truck);
            truckHandler.setDestination(sensor);

            new Thread(truckHandler).start();

            // Add the handler to the truck map
            this.trucks.put(truckId, truckHandler);
        } else {
            truckHandler.setDestination(sensor);
        }
    }

    private Geolocation parseGeolocation(JSONObject object) {
        return new Geolocation(
            object.getFloat("lat"),
            object.getFloat("lon")
        );
    }
}
