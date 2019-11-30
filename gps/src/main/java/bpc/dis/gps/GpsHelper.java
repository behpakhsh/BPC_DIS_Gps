package bpc.dis.gps;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.provider.Settings;

public class GpsHelper {

    private static final String packageName = "com.android.settings";
    private static final String className = "com.android.settings.widget.SettingsAppWidgetProvider";

    private String unknown;
    private String kilometer;
    private String meter;

    public GpsHelper(Activity activity) {
        unknown = activity.getResources().getString(R.string.unknown);
        kilometer = activity.getResources().getString(R.string.kilometer);
        meter = activity.getResources().getString(R.string.meter);
    }

    public GpsHelper(String unknown, String kilometer, String meter) {
        this.unknown = unknown;
        this.kilometer = kilometer;
        this.meter = meter;
    }

    public static Intent getGpsBroadcastIntent() {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        return intent;
    }

    public static boolean gpsIsOn(Activity activity) {
        String locationProviders = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return locationProviders != null && !locationProviders.equals("");
    }

    public String getDistanceBetweenLocation(Location source, Location destination) {
        if (source == null || destination == null) {
            return unknown;
        }

        if ((source.getLatitude() == 0 && source.getLongitude() == 0) || (destination.getLatitude() == 0 && destination.getLongitude() == 0)) {
            return unknown;
        }

        float distance = source.distanceTo(destination);
        if (distance > 1000) {
            return ((int) distance / 1000) + " " + kilometer;
        } else {
            return ((int) distance) + " " + meter;
        }
    }

}
