package me.gladear.simulator;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import org.json.JSONObject;

import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Sensor;
import me.gladear.simulator.utils.HttpRequest;
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

    try {
        var sensors = this.fetchSensors();

        while (true) {
            // Generate random geolocation and intensity
            var sensor = sensors.getRandomSensor();

            // Sensor is null if no sensor is available
            if (sensor != null) {
                var intensity = 1 + this.random.nextInt(Sensor.MAX_INITIAL_INTENSITY);

                sensor.setIntensity(intensity);
                sensors.setUnavailable(sensor);

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
    } catch (Exception ex) {
        ex.printStackTrace();
    }
  }

  private SensorHolder fetchSensors() throws IOException {
    var wsUrl = App.dotenv.get("SERVER_URL") + "api/sensors";
    var url = new URL(wsUrl);
    var request = new HttpRequest(url);

    var array = request.getJSONArray();

    var holder = SensorHolder.getInstance();

    for (var item: array) {
        var object = (JSONObject) item;

        var id = (byte) object.getInt("id");
        var lat = object.getDouble("lat");
        var lon = object.getDouble("lon");

        var sensor = new Sensor(
            id,
            new Geolocation(lat, lon)
        );

        holder.addSensor(sensor);
    }


    return holder;
  }

  private long getTimeToNextFire() {
    return MIN_BREAK + (Math.abs(this.random.nextLong()) % (MAX_BREAK - MIN_BREAK));
  }
}
