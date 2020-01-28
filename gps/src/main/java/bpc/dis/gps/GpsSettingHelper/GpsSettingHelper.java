package bpc.dis.gps.GpsSettingHelper;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import bpc.dis.gps.GpsTrackerStatus;
import bpc.dis.gps.Service.GpsTracker;

public class GpsSettingHelper {

    public void turnOnGps(Context context, int reqCode, GpsSettingHelperListener gpsSettingHelperListener) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (new GpsTracker(context).getGpsTrackerStatus() == GpsTrackerStatus.IS_FAKE_LOCATION) {
                openSetting(context, reqCode, gpsSettingHelperListener);
            } else {
                if (gpsSettingHelperListener != null) {
                    gpsSettingHelperListener.gpsStatus(true);
                }
            }
        } else {
            openSetting(context, reqCode, gpsSettingHelperListener);
        }
    }

    public void openSetting(final Context context, final int reqCode, final GpsSettingHelperListener GpsSettingHelperListener) {
        LocationSettingsRequest locationSettingsRequest = getLocationSettingsRequest();
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if (GpsSettingHelperListener != null) {
                            GpsSettingHelperListener.gpsStatus(true);
                        }
                    }
                })
                .addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                    resolvableApiException.startResolutionForResult((Activity) context, reqCode);
                                } catch (IntentSender.SendIntentException ee) {
                                    ee.printStackTrace();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings.";
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private LocationSettingsRequest getLocationSettingsRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        return builder.build();
    }

}