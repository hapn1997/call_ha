package vn.com.call.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.ConnectionService;
import android.telecom.InCallService;
import android.view.View;
import android.widget.ImageView;
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
import vn.com.call.utils.CallUtils;

public class CallActivity extends AppCompatActivity {
     RelativeLayout call_toggle_speaker,call_dialpad,call_toggle_microphone;
     ImageView call_end,iv_mute,iv_speak,ivKeyboard;
     boolean isONOffSpeaker;
     boolean isMuteConfig;
     ConstraintLayout ongoing_call_holder,incoming_call_holder;
    ShimmerTextView shimmerTextView;
    TextView caller_name_label,call_status_label,call_decline_label,tvDecline;
    SlideToActView slideToActView2;
    int countTime = 0;
    public Timer timer = new Timer();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
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
           callAnwser();
        }
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
        Shimmer shimmer = new Shimmer();
        shimmer.start(shimmerTextView);
    }
    @Subscribe()
    public void onMessageEvent(String state) {
       if (state == TelecomUtils.OnIncomingCallReceived){
           incoming_call_holder.setVisibility(View.VISIBLE);
           ongoing_call_holder.setVisibility(View.GONE);
           return;
       }else  if (state == TelecomUtils.OnIncomingCallAnswered){
         callAnwser();
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
           NotificationUtils.removeNotificationFromID(this, Constants.ACCEPT_DECLINE_NOTIFICATION_ID);
           OnOutgoingCallEnded();

           return;
       }
    }
    private  void OnOutgoingCallEnded(){
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
}