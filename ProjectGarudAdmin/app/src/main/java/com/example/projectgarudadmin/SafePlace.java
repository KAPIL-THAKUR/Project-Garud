package com.example.projectgarudadmin;

public class SafePlace {
    String safeplace,address;
    double latitude,longitude;

    public SafePlace() {
    }

    public SafePlace(String safeplace, String address, double latitude, double longitude) {
        this.safeplace = safeplace;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getSafeplace() {
        return safeplace;
    }

    public void setSafeplace(String safeplace) {
        this.safeplace = safeplace;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
