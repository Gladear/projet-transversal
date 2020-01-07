package me.gladear.simulator.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Sensor;

public class SensorHolder {
    private Sensor[] sensors;
    private List<Sensor> available;
    private Random random;

    public SensorHolder(Sensor[] sensors) {
        this.random = new Random();
        this.sensors = sensors;
        this.available = new ArrayList<>(Arrays.asList(sensors));
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

    public Sensor getNearGeolocation(Geolocation geolocation) {
        var nearest = (Sensor) null;
        var min = Double.POSITIVE_INFINITY;

        for (var sensor: this.sensors) {
            var distance = sensor.geolocation.getDistance(geolocation);

            if (distance < min) {
                min = distance;
                nearest = sensor;
            }
        }

        return nearest;
    }
}
