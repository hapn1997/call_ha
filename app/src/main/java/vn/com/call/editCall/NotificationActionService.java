package vn.com.call.editCall;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.telecom.Call;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import vn.com.call.ui.CallActivity;

public class NotificationActionService  extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public NotificationActionService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.getAction() == Constants.NOTIFICATION_ACTION_ACCEPT) {
            CallerHelper companion = CallerHelper.getInstances();

            List<Call> allCalls = companion == null ? null : companion.getAllCalls();
            if (!(allCalls == null || allCalls.isEmpty())) {
                if (allCalls.size() == 1) {

                    Intent intent1 = new Intent(NotificationActionService.this, CallActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent1.putExtra(TelecomUtils.OnIncomingCallReceived,TelecomUtils.OnIncomingCallReceived);
                    NotificationActionService.this.startActivity(intent1);

                }
            }
        }else  if (intent.getAction() == Constants.NOTIFICATION_ACTION_DECLINE) {
            List<Call> allCalls = CallerHelper.getInstances().getAllCalls();
            if (!(allCalls == null || allCalls.isEmpty())) {
                if (allCalls.size() == 1) {
                    NotificationUtils.removeNotificationFromID(NotificationActionService.this, Constants.ACCEPT_DECLINE_NOTIFICATION_ID);
                }
            }
        }
    }
}
