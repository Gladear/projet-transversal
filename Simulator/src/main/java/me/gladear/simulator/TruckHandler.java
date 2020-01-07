package me.gladear.simulator;

import org.json.JSONObject;

import me.gladear.simulator.comm.WebSocketClientEndpoint;
import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Truck;
import me.gladear.simulator.utils.DriveComputer;
import me.gladear.simulator.utils.WSUtils;

class TruckHandler implements Runnable {
    private static final String ACTION_TRUCK_UPDATE = "truck_update";
    private static final long TICK_TIME = 250;

    private WebSocketClientEndpoint client;
    private final Truck truck;
    private Geolocation destination;
    private boolean running;

    public TruckHandler(WebSocketClientEndpoint client, Truck truck) {
        this.client = client;
        this.truck = truck;
    }

    @Override
    public void run() {
        try {
            this.running = true;

            var driveComputer = new DriveComputer(this.truck.getGeolocation(), this.destination, TICK_TIME);
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

        System.out.println("TruckHandler - Sending msg to client: " + msg);

        this.client.sendMessage(msg);
    }

    public Truck getTruck() {
        return truck;
    }

    public Geolocation getDestination() {
        return destination;
    }

    public void setDestination(Geolocation destination) {
        this.destination = destination;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((destination == null) ? 0 : destination.hashCode());
        result = prime * result + ((truck == null) ? 0 : truck.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TruckHandler other = (TruckHandler) obj;
        if (destination == null) {
            if (other.destination != null)
                return false;
        } else if (!destination.equals(other.destination))
            return false;
        if (truck == null) {
            if (other.truck != null)
                return false;
        } else if (!truck.equals(other.truck))
            return false;
        return true;
    }
}
