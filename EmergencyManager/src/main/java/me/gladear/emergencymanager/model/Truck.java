package me.gladear.emergencymanager.model;

public class Truck {
    public final int id;
    public final Geolocation geolocation;
    public final boolean available;

    public Truck(int id, Geolocation geolocation, boolean available) {
        this.id = id;
        this.geolocation = geolocation;
        this.available = available;
    }
}
