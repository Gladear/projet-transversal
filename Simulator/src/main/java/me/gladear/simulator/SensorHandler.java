package me.gladear.simulator;

import java.util.Random;

import me.gladear.simulator.model.Sensor;
import me.gladear.simulator.utils.SensorHolder;

class SensorHandler implements Runnable {
    private static final int MIN_TICKS_UPDATE = 4;
    private static final int MAX_TICKS_UPDATE = 10;

    public final Sensor sensor;
    private final SensorHolder sensors;
    private Random random;

    SensorHandler(Sensor sensor, SensorHolder sensors) {
        this.sensor = sensor;
        this.sensors = sensors;
        this.random = new Random();
    }

    @Override
    public void run() {
        // Increase the intensity of the fire as long as
        // no fire trucks is next to it.

        while (this.sensor.getIntensity() > 0) {
            var ticksToNextUpdate = this.getTicksToNextUpdate();

            try {
                Thread.sleep(App.TICK_TIME * ticksToNextUpdate);
            } catch (InterruptedException ex) {
                // Whathever, intensity will be updated
                // sooner than expected ¯\_(ツ)_/¯
            }

            var nearbyTrucks = this.sensor.getNearbyTrucks();

            if (nearbyTrucks.isEmpty()) {
                this.sensor.increaseIntensity();
            } else {
                this.sensor.decreaseIntensity();
            }
        }

        // Make the sensor available for a new fire
        this.sensors.setAvailable(sensor);
    }

    private int getTicksToNextUpdate() {
        return MIN_TICKS_UPDATE + this.random.nextInt(MAX_TICKS_UPDATE - MIN_TICKS_UPDATE);
    }
}
