package vn.com.call.call.service;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import vn.com.call.editCall.TelecomUtils;
import vn.com.call.ui.CallActivity;
import vn.com.call.utils.CallUtils;

public class CallReceiver extends  CallActionReceiver {
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        EventBus.getDefault().post(TelecomUtils.OnIncomingCallReceived);
//        Intent intent = new Intent(ctx, CallActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//        ctx.startActivity(intent);
        Toast.makeText(ctx,"onIncomingCallReceived",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        EventBus.getDefault().post(TelecomUtils.OnIncomingCallAnswered);

        Toast.makeText(ctx,"onIncomingCallAnswered",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        EventBus.getDefault().post(TelecomUtils.OnIncomingCallEnded);
        Toast.makeText(ctx,"onIncomingCallEnded",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

        EventBus.getDefault().post(TelecomUtils.OnOutgoingCallStarted);
        Toast.makeText(ctx,"onOutgoingCallStarted",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        EventBus.getDefault().post(TelecomUtils.OnOutgoingCallEnded);
        Toast.makeText(ctx,"onOutgoingCallEnded",Toast.LENGTH_SHORT).show();



    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        EventBus.getDefault().post(TelecomUtils.OnMissedCall);
        Toast.makeText(ctx,"onMissedCall",Toast.LENGTH_SHORT).show();


    }
}
