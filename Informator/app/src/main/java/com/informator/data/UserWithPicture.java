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
    String points;
    ArrayList<String> friends;
    Bitmap profilePhoto;
    String id;
    String status;
    ArrayList<NearFriend> nearFriends;
    ArrayList<NearVirtualObject> nearVirtualObjects;
    ArrayList<VirtualObject> virtual_objects;
    Location currentLocation;
    DatabaseReference databaseReference;
    ListUserUpdateEventListener updateListener;

    public UserWithPicture(String fullName, String email, String phone, String username) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.virtual_objects=new ArrayList<>();
        this.friends = new ArrayList<>();
        points = "0";
        status = "online";
        this.nearFriends = new ArrayList<>();
        this.nearVirtualObjects = new ArrayList<>();
        initListeners();
    }
    public UserWithPicture(User user) {
        fullName = user.fullName;
        email = user.email;
        phone = user.phone;
        username = user.username;
        profilePhoto = null;
        this.virtual_objects=new ArrayList<>();
        this.friends = new ArrayList<>();
        points = user.points;
        status = "online";
        this.nearFriends = new ArrayList<>();
        this.nearVirtualObjects = new ArrayList<>();
        initListeners();
    }

    public UserWithPicture() {
        fullName = "";
        email = "";
        phone = "";
        username = "";
        this.virtual_objects=new ArrayList<>();
        this.friends = new ArrayList<>();
        points = "0";
        status = "online";
        this.nearFriends = new ArrayList<>();
        this.nearVirtualObjects = new ArrayList<>();
        initListeners();
    }
    public UserWithPicture(User user, Bitmap image) {
        fullName = user.fullName;
        email = user.email;
        phone = user.phone;
        username = user.username;
        id=user.id;
        profilePhoto = image;
        points = user.points;
        status = user.status;
        this.virtual_objects=new ArrayList<>();
        this.friends = new ArrayList<>();
        this.nearFriends = new ArrayList<>();
        this.nearVirtualObjects = new ArrayList<>();
        initListeners();
    }

    private void initListeners(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("friends").addChildEventListener(childEventListenerFrineds);
        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("virtual_objects").addChildEventListener(childEventListenerVirtualObjects);
        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("points").addChildEventListener(childEventListenerPoints);
        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("status").addChildEventListener(childEventListenerStatus);
        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("longitude").addChildEventListener(childEventListenerLongitude);
        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("latitude").addChildEventListener(childEventListenerLatitude);
    }


    public void addCurrentLocaation(Location location){
        currentLocation=location;
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    public void setVirtualObjectWithId(String id,Bitmap bitmap){
        for(VirtualObject virtualObject:this.virtual_objects){
            if(virtualObject.getId().compareTo(id)==0){
                virtualObject.setVirtual_object_image(bitmap);
            }
        }
    }

    ChildEventListener childEventListenerStatus = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            status = dataSnapshot.getValue(String.class);
            // TODO notify status has changed
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

    ChildEventListener childEventListenerLatitude= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            currentLocation.setLatitude(dataSnapshot.getValue(Double.class));
            // TODO notify location has changed
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

    ChildEventListener childEventListenerLongitude = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            currentLocation.setLongitude(dataSnapshot.getValue(Double.class));
            // TODO notify status has changed
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
            virtualObject.setUserRecommended(StoredData.getInstance().getUser().getUsername());
            virtual_objects.add(virtualObject);
            // promeni moje poene
            setPoints();
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

    ChildEventListener childEventListenerPoints= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String points = dataSnapshot.getValue(String.class);
            setPoints(points);
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


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLongitude(double longitude){
        this.currentLocation.setLongitude(longitude);
    }
    public void setLatitude(double latitude){
        this.currentLocation.setLatitude(latitude);
    }

    public double getLongitude(){
        return this.currentLocation.getLongitude();
    }

    public double getLatitude(){
        return this.currentLocation.getLatitude();
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public ArrayList<NearFriend> getNearFriends() {
        return nearFriends;
    }

    public void setNearFriends(ArrayList<NearFriend> nearFriends) {
        this.nearFriends = nearFriends;
    }

    public ArrayList<NearVirtualObject> getNearVirtualObjects() {
        return nearVirtualObjects;
    }

    public void setNearVirtualObjects(ArrayList<NearVirtualObject> nearVirtualObjects) {
        this.nearVirtualObjects = nearVirtualObjects;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points){
        this.points = points;
//        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("points").setValue(this.points);
    }

    public void setPoints(){
        this.points = String.valueOf(this.countPoints());
//        databaseReference.child(Constants.FIREBASE_CHILD).child(username).child("points").setValue(this.points);
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

    public int countPoints(){
        int numVO = virtual_objects.size();
        int points = 0;
        if(numVO != 0) {
            for (VirtualObject v : virtual_objects) {
                points += (int) (10 * (v.getNumberOfRates() * v.getRating()) / Math.sqrt(numVO));
            }
        }
        return  points;
    }
}
