package com.informator.services;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.informator.MainActivity;
import com.informator.R;
import com.informator.StartActivity;
import com.informator.data.Constants;
import com.informator.data.NearFriend;
import com.informator.data.NearVirtualObject;
import com.informator.data.StoredData;

public class LocationTracker extends Service implements ChildEventListener {
    int test = 0;
    SharedPreferences sharedPreferences;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        InitializeListeners();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocation(currentLocation);
        }
        else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
            Location currentLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            updateLocation(currentLocation);
        }
        else
        {
            Toast.makeText(this,"Nije ukljucen ni gps provider ni network provider nemoguce je pronaci lokaciju",Toast.LENGTH_LONG).show();

        }


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // test, radi
        //        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
//                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
//                .child("longitude").setValue(25.77887);






//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if(test == 2) {
//                    notifyFriend(new NearFriend("test", 15, 15));
//                    Toast.makeText(LocationTracker.this, "proslo 2", Toast.LENGTH_SHORT).show();
//
//                }
//                else if (test == 4){
//                    notifyVO(new NearVirtualObject("vo","user",15,15));
//                    Toast.makeText(LocationTracker.this, "proslo 3", Toast.LENGTH_SHORT).show();
//                }
//                test++;
//
//            }
//        }, 1000);


        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void updateLocation(Location location){
        Toast.makeText(this, "Lokacija se menja", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("longitude").setValue(location.getLongitude());
        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("latitude").setValue(location.getLatitude());
    }

    private void notifyVO(NearVirtualObject vo){

        Toast.makeText(this, "Objekat", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_info_24)
                .setContentTitle("Found Virtual Object")
                .setContentText("Virtual object : "+vo.getTitle()+ ", recommended by "+vo.getUser()+" is near you !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("near_virtual_objects").child(vo.getTitle()).removeValue();
    }

    private void notifyFriend(NearFriend friend){

        Toast.makeText(this, "Prijatelj", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_info_24)
                .setContentTitle("Found Friend")
                .setContentText("Your friend with username : "+friend.getUser()+" is near you !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("near_friends").child(friend.getUser()).removeValue();
    }




    private void InitializeListeners(){

        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("points")
                .addChildEventListener(this);

        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("phone")
                .addChildEventListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
        .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
        .child("status").setValue("offline");
        Toast.makeText(this, "Service has been stopped!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        try {
            notifyFriend(dataSnapshot.getValue(NearFriend.class));
        }
        catch (Exception e){
            notifyVO(dataSnapshot.getValue(NearVirtualObject.class));
        }
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
}
