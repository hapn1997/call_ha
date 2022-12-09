package vn.com.call.call.service;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.greenrobot.eventbus.EventBus;

import rx.subjects.BehaviorSubject;
import vn.com.call.editCall.CallerHelper;
import vn.com.call.editCall.NotificationUtils;
import vn.com.call.editCall.TelecomUtils;
import vn.com.call.ui.CallActivity;
import vn.com.call.utils.CallUtils;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService  extends InCallService {
    OnListenerCall onListenerCall   = new OnListenerCall();
    Call mCall;
    public class OnListenerCall extends Call.Callback{
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
             if (call.getState() == Call.STATE_HOLDING){
                Toast.makeText(getApplicationContext(),"STATE_HOLDING",Toast.LENGTH_SHORT).show();

            }else  if (call.getState() == Call.STATE_ACTIVE){
                EventBus.getDefault().post(TelecomUtils.OnOutgoingCallAnswered);

                Toast.makeText(getApplicationContext(),"STATE_ACTIVE",Toast.LENGTH_SHORT).show();

            }else  if (call.getState() == Call.STATE_PULLING_CALL){
                Toast.makeText(getApplicationContext(),"STATE_PULLING_CALL",Toast.LENGTH_SHORT).show();

            }else  if (call.getState() == Call.STATE_DIALING){
                Toast.makeText(getApplicationContext(),"STATE_DIALING",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CallerHelper companion = CallerHelper.getInstances();
        if (companion != null) {
            companion.setInCallService(this);
        }

    }

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        mCall = call;
        Intent intent = new Intent(this, CallActivity.class);
        CallUtils.callMain = call;
        CallUtils.inCallService = this;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
        call.registerCallback(onListenerCall);
        if (call.getState() == Call.STATE_RINGING){
            NotificationUtils.createAcceptDeclineNotification(call,getApplicationContext());
        }
    }
    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        mCall = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            mCall.unregisterCallback(onListenerCall);
        }
        CallerHelper companion = CallerHelper.getInstances();
        if (companion != null) {
            companion.clearInCallService();
        }


    }
}
