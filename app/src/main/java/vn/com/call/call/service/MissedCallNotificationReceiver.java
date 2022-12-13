package vn.com.call.call.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import vn.com.call.editCall.CallLogNotificationsService;
import vn.com.call.editCall.Constants;

public class MissedCallNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences.Editor editor = null;
        if (intent != null && intent.getAction().equals(Constants.ACTION_SHOW_MISSED_CALLS_NOTIFICATION)) {
            int intExtra = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_COUNT, -1);
            if (context != null) {
                try {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MAIN_PREFS, 0);
                    if (sharedPreferences != null) {
                        editor = sharedPreferences.edit();
                    }
                } catch (Exception unused) {
                    return;
                }
            }
            if (editor != null) {
                editor.putInt("MissedCount", intExtra);
            }
            if (editor != null) {
                editor.apply();
            }
            CallLogNotificationsService.initService(context, intExtra, intent.getStringExtra(Constants.EXTRA_NOTIFICATION_PHONE_NUMBER));
        }

    }
}
