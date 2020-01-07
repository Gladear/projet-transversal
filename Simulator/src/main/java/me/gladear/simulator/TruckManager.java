package me.gladear.simulator;

import java.net.URI;
import java.util.WeakHashMap;

import org.json.JSONObject;

import me.gladear.simulator.comm.WebSocketClientEndpoint;
import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Station;
import me.gladear.simulator.model.Truck;
import me.gladear.simulator.utils.Pair;
import me.gladear.simulator.utils.SensorHolder;

class TruckManager implements Runnable {
    private static final String ACTION_SEND_TRUCK = "send_truck";

    private WebSocketClientEndpoint client;
    private final SensorHolder sensors;
    private WeakHashMap<Integer, Pair<TruckHandler, Thread>> trucks;

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

                System.out.println("TruckManager - action '" + action + "' called with payload '" + payload + "'");

                if (ACTION_SEND_TRUCK.equals(action)) {
                    this.handleSendTruck((JSONObject) payload);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSendTruck(JSONObject payload) {
        // Parse data from JSON
        var truckId = payload.getInt("id");

        var from = this.parseGeolocation(payload.getJSONObject("from"));
        var to = this.parseGeolocation(payload.getJSONObject("to"));

        // Retrieve an eventual existing truck handler
        var pair = this.trucks.get(truckId);
        var truckHandler = (TruckHandler) null;
        var thread = (Thread) null;

        if (pair == null) {
            // If no handler is assigned to the truck
            // the truck is at its station, so we create
            // a station at the current location of the truck
            var truck = new Truck(truckId, new Station(from));

            // Start a thread that will handle the truck
            // during it's whole life
            truckHandler = new TruckHandler(this.client, truck);
            thread = new Thread(truckHandler);

            // Add the handler to the truck map
            this.trucks.put(truckId, new Pair<TruckHandler, Thread>(truckHandler, thread));
        } else {
            // If the handler exists, we make the truck
            // start from it's current position
            truckHandler = pair.key;
            thread = pair.value;

            from = truckHandler.getTruck().getGeolocation();
            truckHandler.stop();
        }

        // Define the new destination of the truck
        var sensor = this.sensors.getNearGeolocation(to);
        truckHandler.setDestination(sensor.geolocation);

        if (thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // No one cares
            }
        }

        thread.start();
    }

    private Geolocation parseGeolocation(JSONObject object) {
        return new Geolocation(
            object.getFloat("lat"),
            object.getFloat("lon")
        );
    }
}
