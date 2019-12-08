package model;

public class Geolocation {
  public final double lat;
  public final double lon;

  public Geolocation(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
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
