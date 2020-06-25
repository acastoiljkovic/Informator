package com.informator.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.informator.services.LocationTracker;

public class LocationTrackerRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LocationTrackerRestarter.class.getSimpleName(),"Location Tracker Service Restarted !");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, LocationTracker.class));
        } else {
            context.startService(new Intent(context, LocationTracker.class));
        }
    }
}
