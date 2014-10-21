package org.yanex.vika.api.item;

public class PendingLocation {

  private final double latitude;
  private final double longitude;

  public PendingLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }
}
