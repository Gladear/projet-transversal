package me.gladear.simulator;

import java.util.Random;

import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Sensor;
import me.gladear.simulator.utils.SensorHolder;

class FireLiter implements Runnable {
  private static final long MIN_BREAK = 3000l;
  private static final long MAX_BREAK = 20000l;

  private final Random random;

  FireLiter() {
    this.random = new Random();
  }

  @Override
  public void run() {
    // Retrieve the sensors
    // They will be used to lit the fires within
    // their range

    // TODO Ask the web server for location of sensors
    var sensorHolder = SensorHolder.getInstance();
    sensorHolder.addSensor(new Sensor((byte) 0x01, new Geolocation(45.783386, 4.864920)));
    sensorHolder.addSensor(new Sensor((byte) 0x02, new Geolocation(45.779439, 4.865912)));
    sensorHolder.addSensor(new Sensor((byte) 0x03, new Geolocation(45.786719, 4.881763)));
    sensorHolder.addSensor(new Sensor((byte) 0x04, new Geolocation(45.781908, 4.871804)));
    sensorHolder.addSensor(new Sensor((byte) 0x05, new Geolocation(45.783436, 4.877360)));
    sensorHolder.addSensor(new Sensor((byte) 0x06, new Geolocation(45.784383, 4.869151)));

    while (true) {
      // Generate random geolocation and intensity
      var sensor = sensorHolder.getRandomSensor();

      // Sensor is null if no sensor is available
      if (sensor != null) {
        var intensity = 1 + this.random.nextInt(Sensor.MAX_INITIAL_INTENSITY);

        sensor.setIntensity(intensity);
        sensorHolder.setUnavailable(sensor);

        // Each fire has a thread that increases
        // it's intensity as long as no fire truck
        // is nearby
        var handler = new FireHandler(sensor);
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
  }

  private long getTimeToNextFire() {
    return MIN_BREAK + (Math.abs(this.random.nextLong()) % (MAX_BREAK - MIN_BREAK));
  }
}
