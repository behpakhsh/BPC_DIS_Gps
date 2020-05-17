package bpc.dis.gps.RoutingNavigationHelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;

import java.util.Locale;

public class RoutingNavigationHelper {

    private boolean isWazeInstalled(Context context) {
        boolean isInstalled = true;
        try {
            context.getPackageManager().getPackageInfo("com.waze", 0);
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled = false;
        }
        return isInstalled;
    }

    public void startNavigation(Context context, double lat, double lng) {
        if (isWazeInstalled(context)) {
            String url = "https://waze.com/ul?q=" + lat + "," + lng + "&navigate=yes&zoom=17";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage("com.waze");
            context.startActivity(intent);
        } else {
            Uri uri = Uri.parse(String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", lat, lng));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
        }
    }

    @Deprecated
    public void startGeoNavigation(Context context, Location source, Location destination) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("geo:" + source.getLatitude() + "," + source.getLongitude() + "?q=" + destination.getLatitude() + "," + destination.getLongitude()));
        context.startActivity(intent);
    }

    @Deprecated
    public void startGeoNavigation(Context context, Location source, Location destination, String name) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("geo:" + source.getLatitude() + "," + source.getLongitude() + "?q=" + destination.getLatitude() + "," + destination.getLongitude() + " (" + name + ")"));
        context.startActivity(intent);
    }

    public void startGeoNavigation(Context context, Location destination, String name) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("geo:" + 0 + "," + 0 + "?q=" + destination.getLatitude() + "," + destination.getLongitude() + " (" + name + ")"));
        context.startActivity(intent);
    }

}