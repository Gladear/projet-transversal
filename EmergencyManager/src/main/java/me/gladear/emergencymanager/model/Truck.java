package me.gladear.emergencymanager.model;

import java.util.Collection;

public class Truck {
    public final int id;
    public final Geolocation geolocation;
    public final int capacity;
    public final boolean available;

    public Truck(int id, Geolocation geolocation, int capacity, boolean available) {
        this.id = id;
        this.geolocation = geolocation;
        this.capacity = capacity;
        this.available = available;
    }

    public static int getCapacity(Collection<Truck> trucks) {
        return trucks.stream().reduce(0, (total, truck) -> total + truck.capacity, (arg0, arg1) -> arg0 + arg1);
    }
}
