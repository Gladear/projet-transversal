package me.gladear.simulator;

import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;
import me.gladear.simulator.model.Geolocation;
import me.gladear.simulator.model.Sensor;
import me.gladear.simulator.utils.HttpUtils;
import me.gladear.simulator.utils.SensorHolder;

public class App {
    public static final long TICK_TIME = 1000l;
    public static final Dotenv dotenv = Dotenv.load();

    private App() {
        try {
            // # 0. Fetch the required data
            var sensors = this.fetchSensors();

            // # 1. Handle simulator server
            var simuServerHandler = new Thread(new SimuServerHandler(sensors));
            simuServerHandler.start();

            // # 2. Start a thread that ignites fires
            var fireLiter = new Thread(new FireLiter(sensors));
            fireLiter.start();

            // # 3. Handle trucks
            var truckHandler = new Thread(new TruckManager(sensors));
            truckHandler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SensorHolder fetchSensors() throws IOException {
        var url = App.dotenv.get("SERVER_URL") + "api/sensors";
        var data = HttpUtils.getJSONArray(url);

        var sensors = new Sensor[data.length()];

        for (int i = 0, len = data.length(); i < len; i++) {
            var object = data.getJSONObject(i);

            var id = object.getInt("id");
            var geolocation = object.getJSONObject("geolocation");
            var lat = geolocation.getDouble("lat");
            var lon = geolocation.getDouble("lon");

            sensors[i] = new Sensor(id, new Geolocation(lat, lon));
        }

        return new SensorHolder(sensors);
    }

    public static void main(final String[] args) throws Exception {
        new App();
    }
}
