package com.informator.data;

public class NearVirtualObject {
    private String title;
    private String user;
    private double latitude;
    private double longitude;

    public NearVirtualObject(String title, String user, double latitude, double longitude) {
        this.title = title;
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public NearVirtualObject() {
        this.title = "No name";
        this.user = "None";
        this.latitude = 0;
        this.longitude = 0;
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
