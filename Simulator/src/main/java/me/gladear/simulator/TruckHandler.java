package me.gladear.simulator;

import java.io.IOException;

import org.json.JSONObject;

import me.gladear.simulator.comm.WebSocketClientEndpoint;
import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Sensor;
import me.gladear.simulator.model.Truck;
import me.gladear.simulator.utils.DriveComputer;
import me.gladear.simulator.utils.WSUtils;

class TruckHandler implements Runnable {
    private static final String ACTION_TRUCK_GEOLOCATION = "truck_geolocation";
    private static final String ACTION_TRUCK_AVAILABLE = "truck_available";
    private static final int TICK_TIME = 250;

    private final WebSocketClientEndpoint client;
    private final Truck truck;
    private Sensor destination;
    private boolean running;

    public TruckHandler(WebSocketClientEndpoint client, Truck truck) {
        this.client = client;
        this.truck = truck;
    }

    @Override
    public void run() {
        System.out.println("TruckHandler.run - Running for truck #" + this.truck.id);

        try {
            this.running = true;

            this.sendTruckTo(this.destination.geolocation);

            // The truck becomes available when the fire is extinguished,
            this.truck.addAvailabilityListener(available -> {
                if (!available) {
                    return;
                }

                this.sendTruckAvailable();

                try {
                    // Send the truck back to its station
                    this.sendTruckTo(this.truck.station.geolocation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTruckTo(Geolocation geolocation) throws IOException {
        var driveComputer = new DriveComputer(this.truck.getGeolocation(), geolocation, TICK_TIME);
        var drive = driveComputer.get();

        System.out.println("Truck #" + this.truck.id + " is now moving towards " + geolocation);

        for (var i = 0; this.running && this.client.isOpen() && i < drive.length; i++) {
            var newLocation = drive[i];

            this.truck.setGeolocation(newLocation);
            this.sendGeolocationUpdate(newLocation);

            // TODO Remove once GUI is up
            if (i == drive.length / 4) {
                System.out.println("Truck #" + this.truck.id + " has made 1/4 of its journey");
            } else if (i == drive.length / 2) {
                System.out.println("Truck #" + this.truck.id + " has made 2/4 of its journey");
            } else if (i == 3 * drive.length / 4) {
                System.out.println("Truck #" + this.truck.id + " has made 3/4 of its journey");
            }

            try {
                Thread.sleep(TICK_TIME);
            } catch (InterruptedException e) {
                // Well, fuck
            }
        }

        if (this.running) {
            System.out.println("Truck #" + this.truck.id + " has arrived to destination");
        }
    }

    public void stop() {
        this.running = false;
    }

    private void sendGeolocationUpdate(Geolocation geolocation) {
        var object = new JSONObject();
        object.put("id", this.truck.id);

        var geojson = new JSONObject();
        geojson.put("lat", geolocation.lat);
        geojson.put("lon", geolocation.lon);

        object.put("geolocation", geojson);

        this.client.sendMessage(WSUtils.createMessage(ACTION_TRUCK_GEOLOCATION, object));
    }

    private void sendTruckAvailable() {
        var object = new JSONObject();
        object.put("id", this.truck.id);

        this.client.sendMessage(WSUtils.createMessage(ACTION_TRUCK_AVAILABLE, object));
    }

    public Truck getTruck() {
        return truck;
    }

    public Sensor getDestination() {
        return destination;
    }

    public void setDestination(Sensor destination) {
        if (this.destination != null && this.destination.equals(this.truck.getSensor())) {
            this.destination.removeTruck(this.truck);
        }

        this.truck.setSensor(destination);
        this.destination = destination;
    }
}
