package com.informator.data;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class User {
    String fullName;
    String email;
    String phone;
    String username;
    String id;
    String points;
    ArrayList<String> friends;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User(String fullName, String email, String phone, String username) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.friends = new ArrayList<>();
        points = "0";
    }

    public User() {
        fullName = "";
        email = "";
        phone = "";
        username = "";
        this.friends = new ArrayList<>();
        points = "0";
    }

    public String getFriend(int index){
        return friends.get(index);
    }

    public ArrayList<String> getFriends(){
        return friends;
    }

    public void addFriend(String username){
        friends.add(username);
    }

    public int getNumberOfFriends(){
        return friends.size();
    }

    public void removeFriend(String username){
        friends.remove(username);
    }
    public void setFriends(ArrayList<String> friends){
        this.friends = friends;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
