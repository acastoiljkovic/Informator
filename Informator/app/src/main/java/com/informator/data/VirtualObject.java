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

    public VirtualObject(String title, String description, Bitmap virtual_object_image) {
        this.title = title;
        this.description = description;
        this.virtual_object_image = virtual_object_image;
        this.rating=0;
        this.posts=new ArrayList<Post>();
    }

    public VirtualObject(String title, String description) {
        this.title = title;
        this.description = description;
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
}
