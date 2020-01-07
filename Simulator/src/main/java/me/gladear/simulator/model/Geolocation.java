package me.gladear.simulator.model;

public class Geolocation {
    private static final double EARTH_RADIUS = 6371000d;
    public final double lat;
    public final double lon;

    public Geolocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Return distance to other geolocation in meters.
     *
     * Source: https://stackoverflow.com/a/837957
     */
    public double getDistance(Geolocation other) {
        var dLat = Math.toRadians(other.lat - this.lat);
        var dLng = Math.toRadians(other.lon - this.lon);
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(this.lat))
                * Math.cos(Math.toRadians(other.lat)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Geolocation other = (Geolocation) obj;
        if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
            return false;
        if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
            return false;
        return true;
    }
}
