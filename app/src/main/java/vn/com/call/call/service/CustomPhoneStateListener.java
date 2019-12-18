package vn.com.call.call.service;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import vn.com.call.call.util.CallFlashManager;

public class CustomPhoneStateListener extends PhoneStateListener {

    private static long timeStartService;

    private Context context;

    public CustomPhoneStateListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                if (System.currentTimeMillis() - timeStartService > 2000) {
                    timeStartService = System.currentTimeMillis();
                    if (CallFlashManager.isEnableCallFlash(context)) {
                        Intent intent = new Intent(context, ServiceCallRing.class);
                        intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, incomingNumber);
                        context.startService(intent);
                    }
                }
                break;
            default:
                ServiceCallRing.stopWindowsAnim(context);
                break;
        }
    }

}
