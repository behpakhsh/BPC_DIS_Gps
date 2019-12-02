package bpc.dis.gps.RoutingNavigationHelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.Locale;

public class RoutingNavigationHelper {

    private Context context;
    private PackageManager packageManager;

    public RoutingNavigationHelper(Context context) {
        this.packageManager = context.getPackageManager();
        this.context = context;
    }

    private boolean isWazeInstalled() {
        boolean isInstalled = true;
        try {
            packageManager.getPackageInfo("com.waze", 0);
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled = false;
        }
        return isInstalled;
    }

    public void startNavigation(double lat, double lng) {
        if (isWazeInstalled()) {
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

}