package me.gladear.simulator;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

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

        var sensors = new ArrayList<Sensor>(data.length());

        for (var item : data) {
            var object = (JSONObject) item;

            var id = object.getInt("id");
            var geolocation = object.getJSONObject("geolocation");
            var lat = geolocation.getDouble("lat");
            var lon = geolocation.getDouble("lon");

            var sensor = new Sensor(id, new Geolocation(lat, lon));
            sensors.add(sensor);
        }

        return new SensorHolder(sensors.toArray(new Sensor[]{}));
    }

    public static void main(final String[] args) throws Exception {
        new App();
    }
}
