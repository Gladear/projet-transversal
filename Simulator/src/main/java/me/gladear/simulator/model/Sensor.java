package me.gladear.simulator.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.json.JSONString;

public class Sensor implements JSONString {
    public static final int MAX_INITIAL_INTENSITY = 4;
    public static final int MAX_INTENSITY = 10;

    public final int id;
    public final Geolocation geolocation;
    private Set<Truck> trucks;
    private int intensity;

    public Sensor(int id, Geolocation geolocation) {
        this.id = id;
        this.geolocation = geolocation;
        this.trucks = new HashSet<>();

        System.out.printf("Sensor #%d: created\n", id);
    }

    public boolean addTruck(Truck truck) {
        return this.trucks.add(truck);
    }

    public boolean removeTruck(Truck truck) {
        return this.trucks.remove(truck);
    }

    public List<Truck> getNearbyTrucks() {
        return this.trucks.stream()
            .filter(truck -> {
                var geoloc = truck.getGeolocation();
                var distance = geoloc.getDistance(this.geolocation);
                return distance < Geolocation.NEARBY_DISTANCE;
            })
            .collect(Collectors.toList());
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        if (intensity >= 0 && intensity < MAX_INTENSITY) {
            System.out.printf("Sensor #%d: intensity " + (this.intensity < intensity ? "increased" : "decreased") + " to %d\n", this.id, intensity);
            this.intensity = intensity;
        }
    }

    public void increaseIntensity() {
        this.setIntensity(this.intensity + 1);
    }

    public void decreaseIntensity() {
        this.setIntensity(this.intensity - 1);
    }

    @Override
    public String toJSONString() {
        var obj = new JSONObject();

        obj.put("id", this.id);
        obj.put("intensity", this.intensity);

        return obj.toString();
    }
}
