package com.informator.data;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class CustomLocation {
    float accuracy;
    double altitude;
    float bearing;
    float bearingAccuracyDegrees;
    boolean complete;
    long elapsedRealtimeNanos;
    boolean fromMockProvider;
    double latitude;
    double longitude;
    String provider;
    float speed;
    float speedAccuracyMetersPerSecond;
    long time;
    float verticalAccuracyMeters;

    public CustomLocation(float accuracy, double altitude, float bearing, float bearingAccuracyDegrees, boolean complete, long elapsedRealtimeNanos, boolean fromMockProvider, double latitude, double longitude, String provider, float speed, float speedAccuracyMetersPerSecond, long time, float verticalAccuracyMeters) {
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.bearing = bearing;
        this.bearingAccuracyDegrees = bearingAccuracyDegrees;
        this.complete = complete;
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
        this.fromMockProvider = fromMockProvider;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider;
        this.speed = speed;
        this.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond;
        this.time = time;
        this.verticalAccuracyMeters = verticalAccuracyMeters;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Location getLocation(){
        Location ret = new Location(Location.convert(0,0));

        ret.setAccuracy(accuracy);
        ret.setAltitude(altitude);
        ret.setBearing(bearing);
        ret.setBearingAccuracyDegrees(bearingAccuracyDegrees);
        ret.setElapsedRealtimeNanos(elapsedRealtimeNanos);
        ret.setLatitude(latitude);
        ret.setLongitude(longitude);
        ret.setProvider(provider);
        ret.setSpeed(speed);
        ret.setSpeedAccuracyMetersPerSecond(speedAccuracyMetersPerSecond);
        ret.setTime(time);
        ret.setVerticalAccuracyMeters(verticalAccuracyMeters);

        return ret;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public double getBearingAccuracyDegrees() {
        return bearingAccuracyDegrees;
    }

    public void setBearingAccuracyDegrees(float bearingAccuracyDegrees) {
        this.bearingAccuracyDegrees = bearingAccuracyDegrees;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public double getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNanos) {
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public boolean isFromMockProvider() {
        return fromMockProvider;
    }

    public void setFromMockProvider(boolean fromMockProvider) {
        this.fromMockProvider = fromMockProvider;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public double getSpeedAccuracyMetersPerSecond() {
        return speedAccuracyMetersPerSecond;
    }

    public void setSpeedAccuracyMetersPerSecond(float speedAccuracyMetersPerSecond) {
        this.speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond;
    }

    public double getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getVerticalAccuracyMeters() {
        return verticalAccuracyMeters;
    }

    public void setVerticalAccuracyMeters(float verticalAccuracyMeters) {
        this.verticalAccuracyMeters = verticalAccuracyMeters;
    }
}
