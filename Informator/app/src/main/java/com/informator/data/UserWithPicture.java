package com.informator.data;

import android.graphics.Bitmap;

public class UserWithPicture {
    String fullName;
    String email;
    String phone;
    String username;
    Bitmap profilePhoto;
    String id;

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
}
