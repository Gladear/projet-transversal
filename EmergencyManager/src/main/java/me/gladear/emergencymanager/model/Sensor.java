package me.gladear.emergencymanager.model;

import java.util.HashSet;
import java.util.Set;

public class Sensor {
    public final int id;
    public final Geolocation geolocation;
    private Set<Truck> trucks;
    private int intensity;

    public Sensor(int id, Geolocation geolocation) {
        this.id = id;
        this.geolocation = geolocation;
        this.trucks = new HashSet<>();
        this.intensity = 0;
    }

    public boolean requireHelp() {
        return this.intensity > 0 && trucks.isEmpty();
    }

    public int getIntensity() {
        return this.intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public void addTruck(Truck truck) {
        this.trucks.add(truck);
    }

    public void removeTruck(Truck truck) {
        this.trucks.remove(truck);
    }
}
