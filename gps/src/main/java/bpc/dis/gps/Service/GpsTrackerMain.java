package bpc.dis.gps.Service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

import java.util.List;

import bpc.dis.gps.GpsTrackerStatus;
import bpc.dis.gps.MockLocationChecker;

@SuppressLint("MissingPermission")
public class GpsTrackerMain extends Service implements LocationListener {

    private Location location;
    private LocationListener locationListener;

    public GpsTrackerMain() {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location == null) {
                location = getLastKnowLocation();
            } else {
                if (location.getLatitude() == 0 || location.getLongitude() == 0) {
                    location = getLastKnowLocation();
                }
            }
            this.location = location;
            if (locationListener != null) {
                locationListener.onLocationChanged(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        try {
            if (locationListener != null) {
                locationListener.onProviderDisabled(provider);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        try {
            if (locationListener != null) {
                locationListener.onProviderEnabled(provider);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        try {
            if (locationListener != null) {
                locationListener.onStatusChanged(provider, status, extras);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private Location getLastKnowLocation() {
        Location location = new Location("");
        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            List<String> providers = locationManager.getAllProviders();
            if (!providers.isEmpty()) {
                for (String provider : providers) {
                    location = locationManager.getLastKnownLocation(provider);
                    if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
                        break;
                    }
                }
            }
        }
        return location;
    }

    public void initLocationAccurate(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (locationManager == null) {
            return;
        }
        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    60000,
                    0,
                    this
            );
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String bestProvider = locationManager.getBestProvider(criteria, true);
            if (bestProvider == null) {
                bestProvider = LocationManager.GPS_PROVIDER;
            }
//            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(
                    bestProvider,
                    60000,
                    0,
                    this
            );
        }
    }

    public Location getLocation() {
        return location;
    }

    public GpsTrackerStatus getGpsTrackerStatus(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (locationManager == null) {
            return GpsTrackerStatus.UNHANDLED;
        }

        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)) {
            return GpsTrackerStatus.ACCESSS_DENY;
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return GpsTrackerStatus.GPS_IS_OFF;
        }

        if (MockLocationChecker.thereIsAnyMockLocationApp(context)) {
            return GpsTrackerStatus.IS_FAKE_LOCATION;
        }

        return GpsTrackerStatus.TRACKED;
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

}