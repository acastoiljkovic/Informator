package com.informator.data;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DatabaseManager {


    private static FirebaseDatabase database;
    private static DatabaseReference mDatabase;
    private static FirebaseStorage storage;
    private static StorageReference storageRef;

    private static DatabaseManager _instance = null;

    private DatabaseManager(){
        try{
            database = FirebaseDatabase.getInstance();
            storage = FirebaseStorage.getInstance(Constants.URL_STORAGE);
            storageRef = storage.getReference();
            mDatabase = database.getReference();
        }
        catch (Exception e){
            Log.e("DatabaseManager",e.getMessage());
        }
    }


    public static DatabaseManager getInstance(){
        if(_instance == null)
            _instance = new DatabaseManager();
        return _instance;
    }

}
