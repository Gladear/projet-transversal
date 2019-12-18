package me.gladear.simulator;

import java.util.Random;

import me.gladear.simulator.model.Sensor;
import me.gladear.simulator.utils.SensorHolder;

class FireLiter implements Runnable {
  private static final long MIN_BREAK = 3000l;
  private static final long MAX_BREAK = 20000l;

  private final Random random;
  private final SensorHolder sensors;

  FireLiter(SensorHolder sensors) {
    this.sensors = sensors;
    this.random = new Random();
  }

  @Override
  public void run() {
    // Retrieve the sensors
    // They will be used to lit the fires within
    // their range

    try {
        while (true) {
            // Generate random geolocation and intensity
            var sensor = this.sensors.getRandomSensor();

            // Sensor is null if no sensor is available
            if (sensor != null) {
                var intensity = 1 + this.random.nextInt(Sensor.MAX_INITIAL_INTENSITY);

                sensor.setIntensity(intensity);
                this.sensors.setUnavailable(sensor);

                // Each fire has a thread that increases
                // it's intensity as long as no fire truck
                // is nearby
                var handler = new FireHandler(sensor, sensors);
                var thread = new Thread(handler);
                thread.start();
            }

            try {
                var timeToNextfire = this.getTimeToNextFire();
                Thread.sleep(timeToNextfire);
            } catch(InterruptedException ex) {
                // Whathever, next fire will be sooner
                // than expected ¯\_(ツ)_/¯
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
  }

  private long getTimeToNextFire() {
    return MIN_BREAK + (Math.abs(this.random.nextLong()) % (MAX_BREAK - MIN_BREAK));
  }
}
