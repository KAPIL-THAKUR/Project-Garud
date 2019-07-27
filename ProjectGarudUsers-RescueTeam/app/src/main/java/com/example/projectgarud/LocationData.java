package com.example.projectgarud;

public class LocationData {

        double latitude,longitude,magnitude;
        String name,geoLocation;

        public LocationData() {
        }


    public LocationData(String name) {
        this.name = name;
    }

    public LocationData(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

