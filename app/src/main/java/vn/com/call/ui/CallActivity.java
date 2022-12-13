package vn.com.call.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.ConnectionService;
import android.telecom.InCallService;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ncorti.slidetoact.SlideToActView;
import com.phone.thephone.call.dialer.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import vn.com.call.db.ContactHelper;
import vn.com.call.editCall.Constants;
import vn.com.call.editCall.NotificationActionService;
import vn.com.call.editCall.NotificationUtils;
import vn.com.call.editCall.TelecomUtils;
import vn.com.call.ui.main.MainActivity;
import vn.com.call.utils.CallUtils;

public class CallActivity extends AppCompatActivity implements View.OnClickListener {
     RelativeLayout call_toggle_speaker,call_dialpad,call_toggle_microphone;
     ImageView call_end,iv_mute,iv_speak,ivKeyboard;
     boolean isONOffSpeaker;
     boolean isMuteConfig;
     ConstraintLayout ongoing_call_holder,incoming_call_holder,keyboard_view;
    ShimmerTextView shimmerTextView;
    TextView caller_name_label,call_status_label,call_decline_label,tvDecline;
    SlideToActView slideToActView2;
    ImageView imgNum1,imgNum0,imgNum2,imgNum3,imgNum4,imgNum5,imgNum6,imgNum7,imgNum8,imgNum9,imgNumStar,imgNumHash,btnIncomingDecline2;
    TextView txtKeypadDial,txtHideKeypad;
    LinearLayout sendMess,ll_remind;
    int countTime = 0;
    public Timer timer = new Timer();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        PowerManager powerManager;
        PowerManager.WakeLock wakeLock;
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, String.valueOf(getApplicationContext()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);

        }
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if(!wakeLock.isHeld()) {
                    wakeLock.acquire(600000L);
                    return;
                }

//                if(wakeLock.isHeld()) {
//                    wakeLock.release();
//                    return;
//
//                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_FASTEST);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);


        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null);
        }

        call_end= findViewById(R.id.call_end);
        call_toggle_speaker= findViewById(R.id.call_toggle_speaker);
        call_toggle_microphone= findViewById(R.id.call_toggle_microphone);
        call_dialpad= findViewById(R.id.call_dialpad);
        iv_mute= findViewById(R.id.iv_mute);
        iv_speak= findViewById(R.id.iv_speak);
        ivKeyboard= findViewById(R.id.iv_keyboard);
        ongoing_call_holder= findViewById(R.id.ongoing_call_holder);
        incoming_call_holder= findViewById(R.id.incoming_call_holder);
        shimmerTextView= findViewById(R.id.shimmer_tv);
        caller_name_label= findViewById(R.id.caller_name_label);
        call_status_label= findViewById(R.id.call_status_label);
        slideToActView2= findViewById(R.id.stop_alarm);
        call_decline_label= findViewById(R.id.call_decline_label);
        tvDecline= findViewById(R.id.tvDecline);
        keyboard_view= findViewById(R.id.keyboard_view);
        sendMess= findViewById(R.id.sendMess);
        ll_remind= findViewById(R.id.ll_remind);
        initKeyPad();
        if (slideToActView2 != null) {
            slideToActView2.setOuterColor(Color.parseColor("#969a95"));
        }
        String stateRing = getIntent().getStringExtra(TelecomUtils.OnIncomingCallReceived);
        String stateRing1 = getIntent().getStringExtra(TelecomUtils.OnIncomingCallAnswered);
        if (stateRing != null && stateRing.equals(TelecomUtils.OnIncomingCallReceived)){
            incoming_call_holder.setVisibility(View.VISIBLE);
            ongoing_call_holder.setVisibility(View.GONE);

        }
        if (stateRing1 != null && stateRing1.equals(TelecomUtils.OnIncomingCallAnswered)){
            incoming_call_holder.setVisibility(View.GONE);
            ongoing_call_holder.setVisibility(View.VISIBLE);
            Call call = CallUtils.callMain;
            if (call!=null){
                call.answer(0);
            }
        }
        ivKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboard_view.setVisibility(View.VISIBLE);
            }
        });
        tvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnOutgoingCallEnded();
            }
        });
        slideToActView2.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                incoming_call_holder.setVisibility(View.GONE);
                ongoing_call_holder.setVisibility(View.VISIBLE);
                Call call = CallUtils.callMain;
                if (call!=null){
                    call.answer(0);
                }
            }
        });


        call_end.setOnClickListener(v -> {
            Call call = CallUtils.callMain;
            if (call!=null){
                if (call.getState() == Call.STATE_RINGING){
                    call.reject(false,null);
                }else {
                    call.disconnect();
                }
                finish();
            }
        });
        iv_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallUtils.inCallService != null) {
                    AudioManager systemService = (AudioManager)getApplicationContext().getSystemService(AUDIO_SERVICE);
                    if (isONOffSpeaker){
                        isONOffSpeaker = false;
                        iv_speak.setBackgroundResource(R.drawable.cirlce_call_bg);

                    }else {
                        isONOffSpeaker = true;
                        iv_speak.setBackgroundResource(R.drawable.custom_background_button_passcode_click);

                    }
                    systemService.setSpeakerphoneOn(isONOffSpeaker);
                    if (isONOffSpeaker){
                        CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_SPEAKER);

                    }else  CallUtils.inCallService.setAudioRoute(CallAudioState.ROUTE_EARPIECE);

                }

            }
        });
        if (CallUtils.callMain != null) {
            Call.Details details2 = CallUtils.callMain.getDetails();
            Uri handle5 = details2.getHandle() ;
            String contactNameEmptyIfNotAvailable = ContactHelper.getContactName(getApplicationContext(), (details2 == null || (handle5) == null) ? null : handle5.getSchemeSpecificPart());
            Call.Details details3 = CallUtils.callMain.getDetails();
            Uri handle4 = details3.getHandle() ;


            String schemeSpecificPart = (details3 == null || (handle4) == null) ? null : handle4.getSchemeSpecificPart();
            caller_name_label.setText(contactNameEmptyIfNotAvailable == null || contactNameEmptyIfNotAvailable.length() == 0 ? schemeSpecificPart : contactNameEmptyIfNotAvailable);
        }
        iv_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CallUtils.inCallService != null) {
                    AudioManager systemService = (AudioManager)getApplicationContext().getSystemService(AUDIO_SERVICE);
                    if (isMuteConfig){
                        isMuteConfig = false;
                        iv_mute.setBackgroundResource(R.drawable.custom_background_button_passcode_click);

                    }else {
                        isMuteConfig = true;
                        iv_mute.setBackgroundResource(R.drawable.cirlce_call_bg);


                    }
                    systemService.setMicrophoneMute(!isMuteConfig);

                        CallUtils.inCallService.setMuted(!isMuteConfig);

                }

            }
        });
        sendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View mView = LayoutInflater.from(CallActivity.this).inflate(R.layout.drop_down_layout, null, false);
                final PopupWindow popUp = new PopupWindow(mView, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, false);
                popUp.setTouchable(true);
                popUp.setFocusable(true);
                popUp.setOutsideTouchable(true);
                mView.findViewById(R.id.message_1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (requestSmsPermission()){
                            sendSMS(((TextView)mView.findViewById(R.id.message_1)).getText().toString());
                        }
                        popUp.dismiss();
                    }
                }); mView.findViewById(R.id.message_2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (requestSmsPermission()){
                            sendSMS(((TextView)mView.findViewById(R.id.message_2)).getText().toString());

                        }

                        popUp.dismiss();

                    }
                }); mView.findViewById(R.id.message_3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (requestSmsPermission()){
                            sendSMS(((TextView)mView.findViewById(R.id.message_3)).getText().toString());

                        }

                        popUp.dismiss();

                    }
                }); mView.findViewById(R.id.message_4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (requestSmsPermission()){
                            sendSMS(((TextView)mView.findViewById(R.id.message_4)).getText().toString());

                        }

                        popUp.dismiss();

                    }
                });
                //Solution
                popUp.showAtLocation(sendMess, Gravity.TOP, 0, 800);

            }
        });
        ll_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View mView = LayoutInflater.from(CallActivity.this).inflate(R.layout.drop_remind_layout, null, false);
                final PopupWindow popUp = new PopupWindow(mView, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, false);
                popUp.setTouchable(true);
                popUp.setFocusable(true);
                popUp.setOutsideTouchable(true);

                //Solution
                popUp.showAtLocation(sendMess, Gravity.TOP, 0, 800);

            }
        });
            Shimmer shimmer = new Shimmer();
        shimmer.start(shimmerTextView);
    }
    public void sendSMS( String msg) {
        String phoneNo = "";
        Call.Details details2 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            details2 = CallUtils.callMain.getDetails();
            Uri handle5 = details2.getHandle() ;
            phoneNo = handle5.getSchemeSpecificPart();

        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent ...",
                    Toast.LENGTH_LONG).show();
            OnOutgoingCallEnded();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    @Subscribe()
    public void onMessageEvent(String state) {
       if (state == TelecomUtils.OnIncomingCallReceived){
           incoming_call_holder.setVisibility(View.VISIBLE);
           ongoing_call_holder.setVisibility(View.GONE);
           return;
       }else  if (state == TelecomUtils.OnIncomingCallAnswered){
//         callAnwser();
           return;
       }else  if (state == TelecomUtils.OnIncomingCallEnded){
           OnOutgoingCallEnded();
           return;
       }else  if (state == TelecomUtils.OnOutgoingCallStarted){
           incoming_call_holder.setVisibility(View.GONE);
           ongoing_call_holder.setVisibility(View.VISIBLE);
           return;
       }else  if (state == TelecomUtils.OnOutgoingCallEnded){
           OnOutgoingCallEnded();
           return;
       }else  if (state == TelecomUtils.OnOutgoingCallAnswered){
          callAnwser();
           return;
       }else  if (state == TelecomUtils.OnMissedCall){
           OnOutgoingCallEnded();

           return;
       }
    }
    private  void OnOutgoingCallEnded(){
        NotificationUtils.removeNotificationFromID(this, Constants.ACCEPT_DECLINE_NOTIFICATION_ID);

        timer.cancel();
        Call call = CallUtils.callMain;
        if (call!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (call.getState() == Call.STATE_RINGING){
                    call.reject(false,null);
                }else {
                    call.disconnect();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    call_status_label.setText(TelecomUtils.formatTime(countTime, false, 1) + " (" + getApplicationContext().getString(R.string.call_ended) + ')');
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();

                        }
                    }, 2000L);
                }
            });

        }

    }
    private void callAnwser(){
        NotificationUtils.removeNotificationFromID(this, Constants.ACCEPT_DECLINE_NOTIFICATION_ID);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                countTime++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        call_status_label.setText(TelecomUtils.formatTime(countTime,false,1));
                    }
                });
            }
        }, 1000L, 1000L);
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }
    private void initKeyPad(){
        imgNum0  = findViewById(R.id.imgNum0);
        imgNum1  = findViewById(R.id.imgNum1);
        imgNum2  = findViewById(R.id.imgNum2);
        imgNum3  = findViewById(R.id.imgNum3);
        imgNum4  = findViewById(R.id.imgNum4);
        imgNum5  = findViewById(R.id.imgNum5);
        imgNum6  = findViewById(R.id.imgNum6);
        imgNum7  = findViewById(R.id.imgNum7);
        imgNum8  = findViewById(R.id.imgNum8);
        imgNum9  = findViewById(R.id.imgNum9);
        imgNumStar  = findViewById(R.id.imgNumStar);
        imgNumHash  = findViewById(R.id.imgNumHash);
        btnIncomingDecline2  = findViewById(R.id.btnIncomingDecline2);
        txtHideKeypad  = findViewById(R.id.txtHideKeypad);
        txtKeypadDial  = findViewById(R.id.txtKeypadDial);

        imgNum0.setOnClickListener(this);
        imgNum1.setOnClickListener(this);
        imgNum2.setOnClickListener(this);
        imgNum3.setOnClickListener(this);
        imgNum4.setOnClickListener(this);
        imgNum5.setOnClickListener(this);
        imgNum6.setOnClickListener(this);
        imgNum7.setOnClickListener(this);
        imgNum8.setOnClickListener(this);
        imgNum9.setOnClickListener(this);
        imgNumHash.setOnClickListener(this);
        imgNumStar.setOnClickListener(this);
        btnIncomingDecline2.setOnClickListener(this);
        txtHideKeypad.setOnClickListener(this);

    }
    private static final int PERMISSION_SEND_SMS = 123;

    private boolean requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(CallActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(CallActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
            return false;
        } else {
            return true;
            // permission already granted run sms send
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    private void sendSms(String phoneNumber, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imgNum0:
                txtKeypadDial.append("0");
                break;
            case R.id.imgNum1:
                txtKeypadDial.append("1");
                break;
            case R.id.imgNum2:
                txtKeypadDial.append("2");
                break;
            case R.id.imgNum3:
                txtKeypadDial.append("3");
                break;
            case R.id.imgNum4:
                txtKeypadDial.append("4");
                break;
            case R.id.imgNum5:
                txtKeypadDial.append("5");
                break;
            case R.id.imgNum6:
                txtKeypadDial.append("6");
                break;
            case R.id.imgNum7:
                txtKeypadDial.append("7");
                break;
            case R.id.imgNum8:
                txtKeypadDial.append("8");
                break;
            case R.id.imgNum9:
                txtKeypadDial.append("9");
                break;
            case R.id.imgNumStar:
                txtKeypadDial.append("*");
                break;
            case R.id.imgNumHash:
                txtKeypadDial.append("#");
                break;
            case R.id.btnIncomingDecline2:
                OnOutgoingCallEnded();

                break;
            case R.id.txtHideKeypad:
                keyboard_view.setVisibility(View.GONE);

                break;

        }

    }
}