package me.gladear.simulator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Station is created when a truck is first
 * sent to fight a fire. The original geolocation
 * of the truck is used as the geolocation of the
 * station.
 *
 * Multiple trucks may belong to the same
 * station. All the stations are references
 * in a map.
 */
public class Station {
  private static final Map<Geolocation, Station> stations = new HashMap<>();

  public final Geolocation geolocation;
  private Set<Truck> trucks;

  public Station(Geolocation geolocation) {
    this.geolocation = geolocation;
    this.trucks = new HashSet<>();

    // Add the station to the map of stations
    stations.put(geolocation, this);
  }

  /**
   * Adds a truck that belong to the station.
   *
   * When a truck is created, it automatically
   * adds itself to the trucks of the station.
   *
   * @param truck - The truck to be added
   */
  void addTruck(Truck truck) {
    this.trucks.add(truck);
  }
}
