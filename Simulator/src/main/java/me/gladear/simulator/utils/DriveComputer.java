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

        var coordinates = this.parseCoordinates(route.getJSONObject("geometry").getJSONArray("coordinates"));
        var distances = this.getDistancesAsPercent(coordinates);

        var nbTicks = duration * 1000 / this.tick_length;
        var nbCoord = coordinates.length;

        var geolocations = new Geolocation[nbTicks];

        for (var geolocIdx = 0; geolocIdx < nbTicks - 1; geolocIdx++) {
            var progress = (double) geolocIdx / nbTicks;
            var prevCoordIdx = 0;
            var nextCoordIdx = 0;

            while (progress >= distances[nextCoordIdx]) {
                nextCoordIdx += 1;
            }

            prevCoordIdx = nextCoordIdx - 1;

            if (nextCoordIdx != distances.length) {
                var prevProgress = distances[prevCoordIdx];
                var nextProgress = distances[nextCoordIdx];

                var offset = (progress - prevProgress) / (nextProgress - prevProgress);

                var prevCoord = coordinates[prevCoordIdx];
                var nextCoord = coordinates[nextCoordIdx];

                geolocations[geolocIdx] = new Geolocation(prevCoord.lat + (offset * (nextCoord.lat - prevCoord.lat)),
                        prevCoord.lon + (offset * (nextCoord.lon - prevCoord.lon)));
            } else {
                geolocations[geolocIdx] = coordinates[prevCoordIdx];
            }
        }

        geolocations[nbTicks - 1] = coordinates[nbCoord - 1];

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
                if (!e.getMessage().contains("HTTP response code: 429")) {
                    throw e;
                }

                System.out.print("\rFailed to get route from ORSM. Try #" + i);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    // Fuck it
                }
            }
        }

        System.out.println();

        if (!object.getString("code").equals("Ok")) {
            throw new IOException("Couldn't fetch route from API");
        }

        return object;
    }

    private Geolocation[] parseCoordinates(JSONArray coordinates) {
        var len = coordinates.length();
        var geolocations = new Geolocation[len];

        for (var i = 0; i < len; i++) {
            var data = coordinates.getJSONArray(i);
            geolocations[i] = new Geolocation(data.getFloat(1), data.getFloat(0));
        }

        return geolocations;
    }

    private double[] getDistancesAsPercent(Geolocation[] geolocations) {
        var length = geolocations.length;

        var distances = new double[length];
        var total = 0d;

        distances[0] = 0;

        // Make cumulative sum
        for (var i = 1; i < length; i++) {
            total += geolocations[i].getDistance(geolocations[i - 1]);
            distances[i] = total;
        }

        // Convert to a percentage
        for (var i = 0; i < length; i++) {
            distances[i] /= total;
        }

        return distances;
    }
}
