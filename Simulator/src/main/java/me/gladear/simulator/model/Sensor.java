package me.gladear.simulator.model;

import java.util.Set;

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
        System.out.printf("Sensor #%d: created\n", id);
    }

    public boolean addTruck(Truck truck) {
        return this.trucks.add(truck);
    }

    public boolean removeTruck(Truck truck) {
        return this.trucks.remove(truck);
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
        System.out.printf("Sensor #%d: intensity set to %d\n", this.id, intensity);
    }

    public void increaseIntensity() {
        this.setIntensity(this.intensity + 1);
    }

    @Override
    public String toJSONString() {
        var obj = new JSONObject();

        obj.put("id", this.id);
        obj.put("geolocation", this.geolocation.toJSONString());
        obj.put("intensity", this.intensity);

        return obj.toString();
    }
}
