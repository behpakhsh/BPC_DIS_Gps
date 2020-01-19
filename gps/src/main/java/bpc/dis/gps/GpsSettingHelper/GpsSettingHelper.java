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

    private Context context;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationManager locationManager;
    private int reqCode;

    public GpsSettingHelper(Context context, int reqCode) {
        this.context = context;
        this.reqCode = reqCode;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mSettingsClient = LocationServices.getSettingsClient(context);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        mLocationSettingsRequest = builder.build();
        builder.setAlwaysShow(true);
    }

    public void turnGPSOn(GpsSettingHelperListener GpsSettingHelperListener) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (new GpsTracker(context).getGpsTrackerStatus() == GpsTrackerStatus.IS_FAKE_LOCATION) {
                openSetting(GpsSettingHelperListener);
            } else {
                if (GpsSettingHelperListener != null) {
                    GpsSettingHelperListener.gpsStatus(true);
                }
            }
        } else {
            openSetting(GpsSettingHelperListener);
        }
    }

    private void openSetting(final GpsSettingHelperListener GpsSettingHelperListener) {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
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
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) context, reqCode);
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

    public interface GpsSettingHelperListener {

        void gpsStatus(boolean isGPSEnable);

    }

}