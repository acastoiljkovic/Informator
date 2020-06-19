package com.informator.data;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StoredData {

    private static  StoredData _instance = null;
    private ArrayList<VirtualObject> myVirtualObjects;
    private ArrayList<String> listFriendsUsername;
    private ArrayList<Bitmap> userPictures;
    private DatabaseReference databaseReference;
    private static final String FIREBASE_CHILD="users";
    ListUserUpdateEventListener updateListener;


    public UserWithPicture user;

    private StoredData(){
        myVirtualObjects=new ArrayList<VirtualObject>();
        listFriendsUsername=new ArrayList<String>();
        userPictures=new ArrayList<Bitmap>();
        databaseReference= FirebaseDatabase.getInstance().getReference();


    }

    public ArrayList<String> getListFriendsUsername(){
        return listFriendsUsername;
    }

    public  ArrayList<VirtualObject> getMyVirtualObjects(){
        return myVirtualObjects;
    }

    ChildEventListener childEventListenerFrineds= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String friendUsername=dataSnapshot.getValue(String.class);
            listFriendsUsername.add(friendUsername);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ChildEventListener childEventListenerVirtualObjects= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            VirtualObject virtualObject=dataSnapshot.getValue(VirtualObject.class);
            myVirtualObjects.add(virtualObject);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    public static StoredData getInstance(){
        if(_instance == null)
            _instance = new StoredData();

        return _instance;
    }

    public UserWithPicture getUser() {
        return user;
    }

    public void setUser(UserWithPicture user) {
        this.user = user;
        if(user != null) {
            databaseReference.child(FIREBASE_CHILD).child(user.getUsername()).child("friends").addChildEventListener(childEventListenerFrineds);
            databaseReference.child(FIREBASE_CHILD).child(user.getUsername()).child("virtual_objects").addChildEventListener(childEventListenerVirtualObjects);
        }
    }
}
