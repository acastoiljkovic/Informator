package com.informator.services;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.informator.R;
import com.informator.StartActivity;
import com.informator.data.Constants;
import com.informator.data.NearFriend;
import com.informator.data.NearVirtualObject;
import com.informator.data.StoredData;
import com.informator.receivers.LocationTrackerRestarter;

import java.util.UUID;

public class LocationTracker extends Service {
    int test = 0;
    SharedPreferences sharedPreferences;
    private LocationManager locationManager;
    private LocationListener locationListener;
    DatabaseReference databaseReference;

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

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1,new Notification());

        super.onCreate();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {

        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void updateLocation(Location location){
        if(location != null) {
            FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                    .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME, ""))
                    .child("longitude").setValue(location.getLongitude());
            FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                    .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME, ""))
                    .child("latitude").setValue(location.getLatitude());
        }
    }

    private void notifyVO(NearVirtualObject vo){

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
        notificationManager.notify(vo.getTitle().hashCode(), builder.build());

        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("near_virtual_objects").child(vo.getTitle()).removeValue();
    }

    private void notifyFriend(NearFriend friend){
        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_info_24)
                .setContentTitle("Found Friend")
                .setContentText("Your friend with username : "+friend.getUsername()+" is near you !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(friend.getUsername().hashCode(), builder.build());

        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("near_friends").child(friend.getUsername()).removeValue();
    }




    private void InitializeListeners(){

        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("near_friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                notifyFriend(data.getValue(NearFriend.class));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_CHILD_USERS)
                .child(sharedPreferences.getString(Constants.SHARED_PREFERENCES_USERNAME,""))
                .child("near_virtual_objects")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                notifyVO(data.getValue(NearVirtualObject.class));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if(StoredData.getInstance().user.getStatus().equals("online")){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, LocationTrackerRestarter.class);
            sendBroadcast(broadcastIntent);
        }
        else{
        }

    }

}
