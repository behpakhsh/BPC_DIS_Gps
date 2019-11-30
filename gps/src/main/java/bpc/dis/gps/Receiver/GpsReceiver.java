package bpc.dis.gps.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class GpsReceiver extends BroadcastReceiver {

    private GpsReceiverListener gpsReceiverListener;

    public GpsReceiver() {

    }

    public GpsReceiver(GpsReceiverListener gpsReceiverListener) {
        this.gpsReceiverListener = gpsReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (gpsReceiverListener != null) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null) {
                    boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    if ((isGpsEnabled || isNetworkEnabled)) {
                        gpsReceiverListener.onGpsStateChanged(true);
                    } else {
                        gpsReceiverListener.onGpsStateChanged(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
