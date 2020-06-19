package com.informator.data;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.ArrayList;

public class UserWithPicture {
    String fullName;
    String email;
    String phone;
    String username;
    ArrayList<String> friends;
    Bitmap profilePhoto;
    String id;
    ArrayList<VirtualObject> virtual_objects;
    Location currentLocation;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserWithPicture(String fullName, String email, String phone, String username) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.virtual_objects=new ArrayList<>();
        this.friends = new ArrayList<>();
    }

    public UserWithPicture(User user, Bitmap image) {
        fullName = user.fullName;
        email = user.email;
        phone = user.phone;
        username = user.username;
        id=user.id;
        profilePhoto = image;
        this.virtual_objects=new ArrayList<>();
        this.friends = new ArrayList<>();
    }

    public UserWithPicture(User user) {
        fullName = user.fullName;
        email = user.email;
        phone = user.phone;
        username = user.username;
        profilePhoto = null;
        this.virtual_objects=new ArrayList<>();
        this.friends = new ArrayList<>();
    }

    public UserWithPicture() {
        fullName = "";
        email = "";
        phone = "";
        username = "";
        this.virtual_objects=new ArrayList<>();
        this.friends = new ArrayList<>();
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

    public void addVirtualObject(VirtualObject virtualObject){
        this.virtual_objects.add(virtualObject);
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

    public Bitmap getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Bitmap profilePhoto) {
        this.profilePhoto = Bitmap.createBitmap(profilePhoto);
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

    public ArrayList<VirtualObject> getListVO() {
        return virtual_objects;
    }

    public void setListVO(ArrayList<VirtualObject> listVO) {
        this.virtual_objects = listVO;
    }
}
