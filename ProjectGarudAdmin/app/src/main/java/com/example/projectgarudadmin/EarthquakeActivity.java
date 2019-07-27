package com.example.projectgarudadmin;

public class EarthquakeActivity {
    String geoLocation;
    double magnitude;

    public EarthquakeActivity() {
    }

    public EarthquakeActivity(String geoLocation, double magnitude) {
        this.geoLocation = geoLocation;
        this.magnitude = magnitude;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }
}
