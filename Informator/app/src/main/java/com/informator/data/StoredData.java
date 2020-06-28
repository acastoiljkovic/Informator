package com.informator.data;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.informator.services.LocationTracker;

import java.util.ArrayList;

public class StoredData {

    private static  StoredData _instance = null;
    public UserWithPicture user;
    public VirtualObject virtualObject;

    private StoredData(){
    }

    public static StoredData getInstance(){
        if(_instance == null)
            _instance = new StoredData();

        return _instance;
    }

    public VirtualObject getVirtualObject() {
        return virtualObject;
    }

    public void setVirtualObject(VirtualObject virtualObject) {
        this.virtualObject = virtualObject;
    }

    public UserWithPicture getUser() {
        return user;
    }

    public void setUser(UserWithPicture user) {
        this.user = user;

    }
}
