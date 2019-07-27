package com.example.projectgarud;

public class BuildingSaftey {
    String name,address,mobile,coordinates;

    public BuildingSaftey() {
    }

    public BuildingSaftey(String name, String address, String mobile, String coordinates) {
        this.name = name;
        this.address = address;
        this.mobile = mobile;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
