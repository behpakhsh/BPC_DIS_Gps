package bpc.dis.gps;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;

import java.util.List;

public class MockLocationChecker {

    public static boolean isMockSettingsOn(Context context) {
        return !Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION)
                .equals("0");
    }

    public static boolean thereIsAnyMockLocationApp(Context context) {
        int count = 0;
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> applicationInfos = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : applicationInfos) {
            try {
                PackageInfo packageInfo = packageManager
                        .getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (String requestedPermission : requestedPermissions) {
                        if (requestedPermission.equals("android.permission.ACCESS_MOCK_LOCATION") &&
                                !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return count > 0;
    }

}