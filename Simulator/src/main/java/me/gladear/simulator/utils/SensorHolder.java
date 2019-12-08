package me.gladear.simulator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.gladear.simulator.model.Sensor;

public class SensorHolder {
  private static SensorHolder instance = null;

  private List<Sensor> available;
  private Random random;

  public SensorHolder() {
    this.random = new Random();
    this.available = new ArrayList<>(60);
  }

  public void addSensor(Sensor sensor) {
    this.available.add(sensor);
  }

  public void setAvailable(Sensor sensor) {
    if (!this.available.contains(sensor)) {
      this.available.add(sensor);
    }
  }

  public void setUnavailable(Sensor sensor) {
    this.available.remove(sensor);
  }

  public Sensor getRandomSensor() {
    if (available.isEmpty()) {
      return null;
    }

    var index = this.random.nextInt(available.size());
    return this.available.get(index);
  }

  public static SensorHolder getInstance() {
    if (instance == null) {
      instance = new SensorHolder();
    }

    return instance;
  }
}
