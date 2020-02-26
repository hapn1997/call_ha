package vn.com.call.call.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telecom.TelecomManager;
import android.view.KeyEvent;

import java.lang.reflect.Field;
import java.util.List;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NotificationServiceCustom extends NotificationListenerService {

    public static NotificationServiceCustom myService;
    private static PendingIntent intentAnswer, intentDecline;
    private static MediaController mediaController;

    private final IBinder binder = new ServiceBinder();

    public NotificationServiceCustom() {
    }

    public class ServiceBinder extends Binder {
        NotificationServiceCustom getService() {
            return NotificationServiceCustom.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        myService = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        String action = intent.getAction();
        if (SERVICE_INTERFACE.equals(action)) {
            return super.onBind(intent);
        } else {
            return binder;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        myService = this;
        getMediaManager();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        myService = null;
        resetAll();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        myService = this;
        try {
            Notification notification = sbn.getNotification();
            if (notification.actions != null) {
                for (Notification.Action action : notification.actions) {
                    try {
                        if (action.title.toString().equalsIgnoreCase("Answer") ||
                                action.title.toString().equalsIgnoreCase("接听") ||
                                action.title.toString().equalsIgnoreCase("إجابة") ||
                                action.title.toString().equalsIgnoreCase("Antworten") ||
                                action.title.toString().equalsIgnoreCase("Répondre") ||
                                action.title.toString().equalsIgnoreCase("Responda") ||
                                action.title.toString().equalsIgnoreCase("उत्तर") ||
                                action.title.toString().equalsIgnoreCase("Responder") ||
                                action.title.toString().equalsIgnoreCase("Risposta") ||
                                action.title.toString().equalsIgnoreCase("Cevap") ||
                                action.title.toString().equalsIgnoreCase("Menjawab") ||
                                action.title.toString().equalsIgnoreCase("Trả lời")) {
                            intentAnswer = action.actionIntent;
                        } else if (action.title.toString().toLowerCase().contains("decline")
                                || action.title.toString().toLowerCase().contains("bỏ qua")
                        ) {
                            intentDecline = action.actionIntent;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMediaManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                MediaSessionManager mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
                List<MediaController> activeSessions = mediaSessionManager.getActiveSessions(
                        new ComponentName(NotificationServiceCustom.this, NotificationServiceCustom.class));
                for (MediaController mediaController : activeSessions) {
                    if ("com.android.server.telecom".equals(mediaController.getPackageName())) {
                        NotificationServiceCustom.mediaController = mediaController;
                        break;
                    }
                }
                mediaSessionManager.addOnActiveSessionsChangedListener(
                        new MediaSessionManager.OnActiveSessionsChangedListener() {
                            @Override
                            public void onActiveSessionsChanged(List<MediaController> list) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    try {
                                        for (MediaController mediaController : list) {
                                            if ("com.android.server.telecom".equals(mediaController.getPackageName())) {
                                                if (NotificationServiceCustom.mediaController == null) {
                                                    NotificationServiceCustom.mediaController = mediaController;
                                                }
                                                return;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        new ComponentName(this, NotificationServiceCustom.class));
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        myService = this;
    }

    public static void answerCall() {
        try {
            if (intentAnswer != null)
                intentAnswer.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        callAcceptStepStart();
        resetAll();
    }

    public static void rejectCall() {
        try {
            if (intentDecline != null)
                intentDecline.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        resetAll();
    }

    private static void resetAll() {
        intentAnswer = null;
        intentDecline = null;
        mediaController = null;
    }

    private static Object getField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object iWantThis = field.get(obj);
            return iWantThis;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void callAcceptStepStart() {
        try {
            callAcceptStep1();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            callAcceptStep2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            callAcceptStep3();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Runtime.getRuntime().exec("input keyevent " + Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void callAcceptStep1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                MediaSessionManager mSManager = (MediaSessionManager) myService.getSystemService(Context.MEDIA_SESSION_SERVICE);
                List<MediaController> activeSessions = mSManager.getActiveSessions(
                        new ComponentName(myService, NotificationServiceCustom.class));
                for (MediaController mediaController : activeSessions) {
                    if ("com.android.server.telecom".equals(mediaController.getPackageName())) {
                        mediaController.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private static void callAcceptStep2() {
        AudioManager audioManager = (AudioManager) myService.getSystemService(Context.AUDIO_SERVICE);
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
        KeyEvent keyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
        audioManager.dispatchMediaKeyEvent(keyEvent);
        audioManager.dispatchMediaKeyEvent(keyEvent2);
    }

    private static void callAcceptStep3() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TelecomManager telecomManager = (TelecomManager) myService.getSystemService(Context.TELECOM_SERVICE);
            telecomManager.acceptRingingCall();
        }
    }

}