package com.informator.data;

public class NearFriend {
    private String user;
    private double latitude;
    private double longitude;

    public NearFriend(String user, double latitude, double longitude) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public NearFriend() {
        this.user = "None";
        this.latitude = 0;
        this.longitude = 0;

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
