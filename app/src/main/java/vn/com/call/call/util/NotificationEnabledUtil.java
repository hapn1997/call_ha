package vn.com.call.call.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class NotificationEnabledUtil {

    public static boolean isServicePermission(Context context) {
        String notificationListenerString = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        if (notificationListenerString == null ||
                !notificationListenerString.contains(context.getPackageName())) {
            return false;
        } else {
            return true;
        }
    }

    public static void startService(Context context) {
        String notificationListenerString = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        if (notificationListenerString == null ||
                !notificationListenerString.contains(context.getPackageName())) {
            return;
        } else {
            if (!isMyServiceRunning(context, NotificationServiceCustom.class.getName())) {
                Intent intent = new Intent(context.getApplicationContext(), NotificationServiceCustom.class);
                intent.setAction("android.settings.NOTIFICATION_LISTENER_SETTINGS");
                context.getApplicationContext().startService(intent);
            }
        }
    }

    public static void requestPermission(Context context) {
        Intent requestIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        requestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(requestIntent);
    }

    private static boolean isMyServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName()) &&
                    context.getApplicationContext().getPackageName().equals(service.service.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
