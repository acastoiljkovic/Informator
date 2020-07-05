package com.informator.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Event {
    String title;
    String owner;
    String id;
    ArrayList<String> invitedFriends;
    double latitude;
    double longitude;
    String datetime;
    String alert;

    public Event(String title, String owner, ArrayList<String> invitedFriends, double latitude, double longitude, String datetime, String alert) {
        this.id = "none";
        this.title = title;
        this.owner = owner;
        this.invitedFriends = invitedFriends;
        this.latitude = latitude;
        this.longitude = longitude;
        this.datetime = datetime;
        this.alert = alert;
    }

    public Event() {
        this.id = "none";
        this.title = "untitled";
        this.owner = "none";
        this.invitedFriends = new ArrayList<>();
        this.latitude = 0;
        this.longitude = 0;
        SimpleDateFormat formater = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.datetime = formater.format(new Date());
        this.alert = "off";
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<String> getInvitedFriends() {
        return invitedFriends;
    }

    public void setInvitedFriends(ArrayList<String> invitedFriends) {
        this.invitedFriends = invitedFriends;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
}
