package app;

import java.util.Random;

import model.Sensor;
import utils.SensorHolder;

class FireHandler implements Runnable {
  private static final int MIN_TICK_INCREASE = 4;
  private static final int MAX_TICK_INCREASE = 10;

  public final Sensor sensor;
  private Random random;

  FireHandler(Sensor sensor) {
    this.sensor = sensor;
    this.random = new Random();
  }

  @Override
  public void run() {
    // Increase the intensity of the fire as long as
    // no fire trucks is next to it.

    var tickToNextIncrease = this.getTickToNextIncrease();
    var currTick = 0;

    while (this.sensor.getIntensity() > 0) {
      currTick += 1;

      if (this.sensor.getIntensity() < Sensor.MAX_INTENSITY && currTick >= tickToNextIncrease) {
        this.sensor.increaseIntensity();
        tickToNextIncrease = this.getTickToNextIncrease();
        currTick = 0;
      }

      try {
        Thread.sleep(App.TICK_TIME);
      } catch (InterruptedException ex) {
        // Whathever, intensity will increase or decrease
        // sooner than expected ¯\_(ツ)_/¯
      }
    }

    // Make the sensor available for a new fire
    var sensorHolder = SensorHolder.getInstance();
    sensorHolder.setAvailable(sensor);
  }

  private int getTickToNextIncrease() {
    return MIN_TICK_INCREASE + this.random.nextInt(MAX_TICK_INCREASE - MIN_TICK_INCREASE);
  }
}
