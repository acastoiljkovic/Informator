package com.informator.data;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class VirtualObject {

    private String id;
    private String title;
    private String description;
    private float rating;
    private Bitmap virtual_object_image;
    private ArrayList<Post> posts;
    private double latitude;
    private double longitude;
    private int numberOfRates;
    private String userRecommended;
    private String date;
    private String typeOfVirtualObject;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypeOfVirtualObject() {
        return typeOfVirtualObject;
    }

    public void setTypeOfVirtualObject(String typeOfVirtualObject) {
        this.typeOfVirtualObject = typeOfVirtualObject;
    }

    public VirtualObject() {
        this.title = "title";
        this.description = "description";
        this.date="date";
        this.typeOfVirtualObject="type";
        this.virtual_object_image = null;
        this.numberOfRates=0;
        this.rating=0;
        this.posts=new ArrayList<Post>();
        this.userRecommended = "none";
    }

    public VirtualObject(String title, String description,double latitude,double longitude,String date,String type,String userRecommended) {
        this.title = title;
        this.description = description;
        this.latitude=latitude;
        this.longitude=longitude;
        this.date=date;
        this.typeOfVirtualObject=type;
        this.numberOfRates=0;
        this.rating=0;
        this.posts=new ArrayList<Post>();
        this.userRecommended = userRecommended;
    }

    public VirtualObject(String title, String description,double latitude,double longitude,float rating) {
        this.title = title;
        this.description = description;
        this.latitude=latitude;
        this.longitude=longitude;
        this.rating=rating;
        this.posts=new ArrayList<Post>();
        this.userRecommended = "none";
    }

    public void addPost(Post post){
        this.posts.add(post);
    }

    public String getUserRecommended() {
        return userRecommended;
    }

    public void setUserRecommended(String userRecommended) {
        this.userRecommended = userRecommended;
    }

    public int getNumberOfRates() {
        return numberOfRates;
    }

    public void setNumberOfRates(int numberOfRates) {
        this.numberOfRates = numberOfRates;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Bitmap getVirtual_object_image() {
        return virtual_object_image;
    }

    public void setVirtual_object_image(Bitmap virtual_object_image) {
        this.virtual_object_image = virtual_object_image;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
