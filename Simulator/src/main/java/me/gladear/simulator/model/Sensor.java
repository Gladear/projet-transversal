package me.gladear.simulator.model;

import java.util.Set;

public class Sensor {
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
}
