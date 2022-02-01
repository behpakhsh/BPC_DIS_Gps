package bpc.dis.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;

public class GpsHelper {

    private String unknown;
    private String kilometer;
    private String meter;

    public GpsHelper(Activity activity) {
        unknown = activity.getResources().getString(R.string.unknown);
        kilometer = activity.getResources().getString(R.string.kilometer);
        meter = activity.getResources().getString(R.string.meter);
    }

    public GpsHelper(Context context) {
        unknown = context.getResources().getString(R.string.unknown);
        kilometer = context.getResources().getString(R.string.kilometer);
        meter = context.getResources().getString(R.string.meter);
    }

    public GpsHelper(String unknown, String kilometer, String meter) {
        this.unknown = unknown;
        this.kilometer = kilometer;
        this.meter = meter;
    }

    public static Intent getGpsBroadcastIntent() {
        Intent intent = new Intent();
        String packageName = "com.android.settings";
        String className = "com.android.settings.widget.SettingsAppWidgetProvider";
        intent.setClassName(packageName, className);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        return intent;
    }

    public static boolean gpsIsOn(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            String gpsStatus = android.provider.Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsStatus != null) {
                return false;
            }
            return !gpsStatus.equals("0");
        } else {
            String locationProviders = Settings.Secure.
                    getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return locationProviders != null && !locationProviders.equals("");
        }
    }

    public static boolean gpsIsOnInFragment(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            String gpsStatus = android.provider.Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsStatus != null) {
                return false;
            }
            return !gpsStatus.equals("0");
        } else {
            String locationProviders = Settings.Secure.
                    getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return locationProviders != null && !locationProviders.equals("");
        }
    }

    public String getDistanceBetweenLocationWithUnit(Location source, Location destination) {
        if (source == null || destination == null) {
            return unknown;
        }

        if ((source.getLatitude() == 0 && source.getLongitude() == 0) ||
                (destination.getLatitude() == 0 && destination.getLongitude() == 0)) {
            return unknown;
        }

        float distance = source.distanceTo(destination);
        return getDistanceByTitle(distance);
    }

    public float getDistanceBetweenLocation(Location source, Location destination) {
        if (source == null || destination == null) {
            return 0;
        }

        if ((source.getLatitude() == 0 && source.getLongitude() == 0) ||
                (destination.getLatitude() == 0 && destination.getLongitude() == 0)) {
            return 0;
        }
        return source.distanceTo(destination);
    }

    public String getDistanceByTitle(float distance) {
        if (distance > 1000) {
            return ((int) distance / 1000) + " " + kilometer;
        } else {
            return ((int) distance) + " " + meter;
        }
    }

}