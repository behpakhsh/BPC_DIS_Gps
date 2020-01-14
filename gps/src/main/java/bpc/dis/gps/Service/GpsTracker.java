package bpc.dis.gps.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

import bpc.dis.gps.GpsTrackerStatus;
import bpc.dis.gps.MockLocationChecker;

public class GpsTracker extends Service implements android.location.LocationListener {

    private GpsTrackerStatus gpsTrackerStatus;
    private Context activity;
    private Location location;

    public GpsTracker() {

    }

    public GpsTracker(Context activity) {
        this.activity = activity;
        initLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void initLocation() {

        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            gpsTrackerStatus = GpsTrackerStatus.UNHANDLED;
            return;
        }

        if ((ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            gpsTrackerStatus = GpsTrackerStatus.ACCESSS_DENY;
            location = new Location("");
            return;
        }

        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGpsEnabled && !isNetworkEnabled) {
            gpsTrackerStatus = GpsTrackerStatus.GPS_IS_OFF;
            location = new Location("");
            return;
        }


        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            gpsTrackerStatus = GpsTrackerStatus.TRACKED;
        }
        if (isGpsEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            gpsTrackerStatus = GpsTrackerStatus.TRACKED;
        }

        if (location == null) {
            gpsTrackerStatus = GpsTrackerStatus.UNHANDLED;
        }

        if (MockLocationChecker.thereIsAnyMockLocationApp(activity)) {
            if (MockLocationChecker.isMockSettingsOn(activity)) {
                gpsTrackerStatus = GpsTrackerStatus.IS_FAKE_LOCATION;
            }
        }

    }

    public double getLatitude() {
        if (location == null) {
            return 0;
        }
        return location.getLatitude();
    }

    public double getLongitude() {
        if (location == null) {
            return 0;
        }
        return location.getLongitude();
    }

    public Location getLocation() {
        return location;
    }

    public GpsTrackerStatus getGpsTrackerStatus() {
        return gpsTrackerStatus;
    }

}