package me.gladear.simulator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.gladear.simulator.model.Sensor;

public class SensorHolder {
  private Sensor[] sensors;
  private List<Sensor> available;
  private Random random;

  public SensorHolder(Sensor[] sensors) {
    this.random = new Random();
    this.sensors = sensors;
    this.available = new ArrayList<>(sensors.length);
  }

  public Sensor[] getSensors() {
      return this.sensors;
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
}
