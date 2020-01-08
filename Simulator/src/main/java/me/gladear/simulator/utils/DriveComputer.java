package me.gladear.simulator.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import me.gladear.simulator.model.Geolocation;

public class DriveComputer {
    private final Geolocation departure;
    private final Geolocation destination;
    private final int tick_length;

    public DriveComputer(Geolocation departure, Geolocation destination, int tick_length) {
        this.departure = departure;
        this.destination = destination;
        this.tick_length = tick_length;
    }

    public Geolocation[] get() throws IOException {
        var object = this.getRouteObject();

        var routes = object.getJSONArray("routes");
        var route = routes.getJSONObject(0);

        var duration = (int) route.getFloat("duration");

        var geometry = route.getJSONObject("geometry");
        var coordinates = geometry.getJSONArray("coordinates");

        var nbTicks = duration * 1000 / this.tick_length;
        var nbCoord = coordinates.length();

        var geolocations = new Geolocation[nbTicks];

        var relation = (double) nbTicks / (nbCoord - 1);

        geolocations[0] = this.parseJSON(coordinates.getJSONArray(0));

        for (var geolocIndex = 1; geolocIndex < nbTicks - 1; geolocIndex++) {
            var prevJSONIndex = (int) (geolocIndex / relation);
            var nextJSONIndex = prevJSONIndex + 1;

            var prevGeolocIndex = prevJSONIndex * relation;
            var nextGeolocIndex = nextJSONIndex * relation;

            var prevGeoloc = this.parseJSON(coordinates.getJSONArray(prevJSONIndex));
            var nextGeoloc = this.parseJSON(coordinates.getJSONArray(nextJSONIndex));

            var offset = (geolocIndex - prevGeolocIndex) / (nextGeolocIndex - prevGeolocIndex);

            var newGeoloc = new Geolocation(prevGeoloc.lat + (offset * (nextGeoloc.lat - prevGeoloc.lat)),
                    prevGeoloc.lon + (offset * (nextGeoloc.lon - prevGeoloc.lon)));

            geolocations[geolocIndex] = newGeoloc;
        }

        geolocations[nbTicks - 1] = this.parseJSON(coordinates.getJSONArray(nbCoord - 1));

        // DEBUG
        // http://geojson.io/

        // var fos = new FileWriter(new File(System.getenv("HOME") + "/workspace/path.json"));

        // for (var geoloc : geolocations) {
        //     fos.append(
        //         "    {\n"
        //         + "        \"type\": \"Feature\",\n"
        //         + "        \"geometry\": {\n"
        //         + "            \"type\": \"Point\",\n"
        //         + "            \"coordinates\": [\n"
        //         + "                " + geoloc.lon + ",\n"
        //         + "                " + geoloc.lat + "\n"
        //         + "            ]\n"
        //         + "        },\n"
        //         + "        \"properties\": {}\n"
        //         + "    },\n"
        //     );
        // }

        // fos.flush();
        // fos.close();

        return geolocations;
    }

    private JSONObject getRouteObject() throws IOException {
        var url = "http://router.project-osrm.org/route/v1/driving/" + this.departure.lon + "," + this.departure.lat
                + ";" + this.destination.lon + "," + this.destination.lat + "?geometries=geojson";

        var object = (JSONObject) null;

        for (var i = 0; i < 100 && object == null; i++) {
            try {
                object = HttpUtils.getJSONObject(url);
            } catch (IOException e) {
                System.out.println(e.getMessage());

                if (!e.getMessage().contains("HTTP response code: 429")) {
                    throw e;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    // Fuck it
                }
            }
        }

        if (!object.getString("code").equals("Ok")) {
            throw new IOException("Couldn't fetch route from API");
        }

        return object;
    }

    private Geolocation parseJSON(JSONArray data) {
        return new Geolocation(data.getFloat(1), data.getFloat(0));
    }
}
