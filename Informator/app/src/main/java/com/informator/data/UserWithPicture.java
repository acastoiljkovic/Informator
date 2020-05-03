package com.informator.data;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class UserWithPicture {
    String fullName;
    String email;
    String phone;
    String username;
    Bitmap profilePhoto;
    String id;
    ArrayList<VirtualObject> listVO;

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
        this.listVO=new ArrayList<>();
    }

    public UserWithPicture(User user, Bitmap image) {
        fullName = user.fullName;
        email = user.email;
        phone = user.phone;
        username = user.username;
        profilePhoto = image;
        this.listVO=new ArrayList<>();
    }
    public UserWithPicture(User user) {
        fullName = user.fullName;
        email = user.email;
        phone = user.phone;
        username = user.username;
        profilePhoto = null;
        this.listVO=new ArrayList<>();
    }

    public UserWithPicture() {
        fullName = "";
        email = "";
        phone = "";
        username = "";
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
        return listVO;
    }

    public void setListVO(ArrayList<VirtualObject> listVO) {
        this.listVO = listVO;
    }
}
