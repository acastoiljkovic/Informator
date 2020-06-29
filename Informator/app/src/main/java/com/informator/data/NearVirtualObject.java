package com.informator.data;

public class NearVirtualObject {
    private String title;
    private String user;
    private double lat;
    private double lng;

    public NearVirtualObject(String title, String user, double latitude, double longitude) {
        this.title = title;
        this.user = user;
        this.lat = latitude;
        this.lng = longitude;
    }

    public NearVirtualObject() {
        this.title = "No name";
        this.user = "None";
        this.lat = 0;
        this.lng = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
