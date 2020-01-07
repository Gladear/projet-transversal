package me.gladear.simulator;

import org.json.JSONObject;

import me.gladear.simulator.comm.WebSocketClientEndpoint;
import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Sensor;
import me.gladear.simulator.model.Truck;
import me.gladear.simulator.utils.DriveComputer;
import me.gladear.simulator.utils.WSUtils;

class TruckHandler implements Runnable {
    private static final String ACTION_TRUCK_UPDATE = "truck_update";
    private static final long TICK_TIME = 250;

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

            var driveComputer = new DriveComputer(this.truck.getGeolocation(), this.destination.geolocation, TICK_TIME);
            var drive = driveComputer.get();

            for (var i = 0; this.running && this.client.isOpen() && i < drive.length; i++) {
                var newLocation = drive[i];

                this.truck.setGeolocation(newLocation);
                this.sendToClient(newLocation);

                try {
                    Thread.sleep(TICK_TIME);
                } catch (InterruptedException e) {
                    // Well, fuck
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.running = false;
    }

    private void sendToClient(Geolocation geolocation) {
        var object = new JSONObject();
        object.put("id", this.truck.id);

        var geojson = new JSONObject();
        geojson.put("lat", geolocation.lat);
        geojson.put("lon", geolocation.lon);

        object.put("geolocation", geojson);

        var msg = WSUtils.createMessage(ACTION_TRUCK_UPDATE, object);

        this.client.sendMessage(msg);
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
