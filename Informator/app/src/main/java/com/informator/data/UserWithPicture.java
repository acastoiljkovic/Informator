package com.informator.data;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    DatabaseReference databaseReference;
    ListUserUpdateEventListener updateListener;


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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("friends").addChildEventListener(childEventListenerFrineds);
        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("virtual_objects").addChildEventListener(childEventListenerVirtualObjects);

    }

<<<<<<< HEAD
=======
    ChildEventListener childEventListenerVirtualObjects= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            VirtualObject virtualObject=dataSnapshot.getValue(VirtualObject.class);
            virtual_objects.add(virtualObject);
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

    ChildEventListener childEventListenerFrineds= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String friendUsername=dataSnapshot.getValue(String.class);
            friends.add(friendUsername);
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


>>>>>>> master
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

    public int getNumberOfFriends(){
        return friends.size();
    }


    public void removeFriend(String username){
        friends.remove(username);
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
