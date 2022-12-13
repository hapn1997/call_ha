package vn.com.call.editCall;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public final class CallLogNotificationsService extends IntentService {
    public CallLogNotificationsService() {
        super(CallLogNotificationsService.class.getName());
    }

    public static final void initService(Context context, int i6, String str) {
        Intent intent = new Intent(context, CallLogNotificationsService.class);
        intent.setAction(Constants.ACTION_UPDATE_MISSED_CALL_NOTIFICATIONS);
        intent.putExtra(Constants.EXTRA_MISSED_CALL_COUNT, i6);
        intent.putExtra(Constants.EXTRA_MISSED_CALL_NUMBER, str);
        if (context != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startService(intent);
                }
            } catch (IllegalStateException unused) {
            }
        }
    }

    @Override
    public void onHandleIntent(Intent intent) {
        String action;
        if (intent != null && TelecomUtils.hasPermission(getApplicationContext(), "android.permission.READ_CALL_LOG") && (action = intent.getAction()) != null) {
            if (action.equals(Constants.ACTION_UPDATE_MISSED_CALL_NOTIFICATIONS)) {
                int intExtra = intent.getIntExtra(Constants.EXTRA_MISSED_CALL_COUNT, -1);
                String stringExtra = intent.getStringExtra(Constants.EXTRA_MISSED_CALL_NUMBER);
                Context applicationContext = getApplicationContext();
                NotificationUtils.updateMissedCallNotification(intExtra, stringExtra, applicationContext);
            } else if (action.equals(Constants.ACTION_MARK_NEW_MISSED_CALLS_AS_OLD)) {
                CallLogNotificationsHelper.Instants.removeMissedCallNotifications(getApplicationContext());
            }
        }
    }
}