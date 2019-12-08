package me.gladear.simulator.model;

/**
 * A Truck is created in the simulation when the
 * web server tells the simulator that a truck
 * is sent to fight a fire.
 *
 * Once sent, the geolocation of the truck is
 * updated regularly, making it advance towards
 * it's destination.
 *
 * After the fire has been extinguished, the
 * truck is sent back to it's station.
 * However, it can be reassigned to a new fire
 * before it reaches the station.
 */
public class Truck {
  public final Station station;
  private Geolocation geolocation;
  private Sensor sensor;

  public Truck(Station station, Sensor sensor) {
    this.station = station;
    this.sensor = sensor;
    this.geolocation = station.geolocation;

    this.station.addTruck(this);
    this.sensor.addTruck(this);
  }

  public Geolocation getGeolocation() {
    return geolocation;
  }

  public void setGeolocation(Geolocation geolocation) {
    this.geolocation = geolocation;
  }

  public Sensor getSensor() {
    return sensor;
  }

  public void setSensor(Sensor sensor) {
    this.sensor.removeTruck(this);
    this.sensor = sensor;
    this.sensor.addTruck(this);
  }
}
