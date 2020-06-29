package com.informator.data;

public class NearFriend {
    private String username;
    private double lat;
    private double lng;

    public NearFriend(String username, double lat, double lng) {
        this.username = username;
        this.lat = lat;
        this.lng = lng;
    }

    public NearFriend() {
        this.username = "None";
        this.lat = 0;
        this.lng = 0;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
