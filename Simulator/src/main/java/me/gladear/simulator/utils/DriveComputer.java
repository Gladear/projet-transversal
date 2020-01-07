package me.gladear.simulator.utils;

import me.gladear.simulator.model.Geolocation;

public class DriveComputer {
    private static final double VEHICULE_SPEED = 50 / 3.6; // 50km/h in m/s

    private final Geolocation departure;
    private final Geolocation destination;
    private final long tick_length;

    public DriveComputer(Geolocation departure, Geolocation destination, long tick_length) {
        this.departure = departure;
        this.destination = destination;
        this.tick_length = tick_length;
    }

    public Geolocation[] get() {
        var distance = this.departure.getDistance(this.destination);
        var duration = distance / VEHICULE_SPEED;
        var ticks = (int) (duration * 1000 / tick_length);

        var dLat = (this.destination.lat - this.departure.lat) / ticks;
        var dLon = (this.destination.lon - this.departure.lon) / ticks;

        var drive = new Geolocation[ticks];

        var cLat = this.departure.lat;
        var cLon = this.departure.lon;

        for (var i = 0; i < ticks; i++) {
            cLat += dLat;
            cLon += dLon;

            drive[i] = new Geolocation(cLat, cLon);
        }

        return drive;
    }
}
