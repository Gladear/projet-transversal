package me.gladear.simulator.model;

/**
 * A Truck is created in the simulation when the web server tells the simulator
 * that a truck is sent to fight a fire.
 *
 * Once sent, the geolocation of the truck is updated regularly, making it
 * advance towards it's destination.
 *
 * After the fire has been extinguished, the truck is sent back to it's station.
 * However, it can be reassigned to a new fire before it reaches the station.
 */
public class Truck {
    public final int id;
    public final Station station;
    private Geolocation geolocation;
    private Sensor sensor;

    public Truck(int id, Station station) {
        this.id = id;
        this.station = station;
        this.geolocation = station.geolocation;
        this.sensor = null;

        this.station.addTruck(this);
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        if (this.sensor != null) {
            this.sensor.removeTruck(this);
        }

        this.sensor = sensor;

        if (this.sensor != null) {
            this.sensor.addTruck(this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((geolocation == null) ? 0 : geolocation.hashCode());
        result = prime * result + id;
        result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
        result = prime * result + ((station == null) ? 0 : station.hashCode());
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
        Truck other = (Truck) obj;
        if (geolocation == null) {
            if (other.geolocation != null)
                return false;
        } else if (!geolocation.equals(other.geolocation))
            return false;
        if (id != other.id)
            return false;
        if (sensor == null) {
            if (other.sensor != null)
                return false;
        } else if (!sensor.equals(other.sensor))
            return false;
        if (station == null) {
            if (other.station != null)
                return false;
        } else if (!station.equals(other.station))
            return false;
        return true;
    }
}
