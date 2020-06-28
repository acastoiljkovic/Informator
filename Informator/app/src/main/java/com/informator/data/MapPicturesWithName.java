package com.informator.data;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapPicturesWithName {

    private Map<String, Bitmap> images = null;

    public MapPicturesWithName() {
        images = new HashMap<>();
    }

    public MapPicturesWithName(Map<String, Bitmap> images) {
        this.images = images;
    }

    public Map<String, Bitmap> getImages() {
        return images;
    }

    public void setImages(Map<String, Bitmap> images) {
        this.images = images;
    }

    public Bitmap getImage(String name){

        return images.get(name);
    }

    public int size(){
        return images.size();
    }

    public void add(Bitmap picture, String name){
        images.put(name,picture);
    }


    public void clear(){
        images.clear();
    }
}


