package vn.com.call.call.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

import java.io.IOException;
import java.lang.reflect.Method;

public class CallUtil {

    private static final String MANUFACTURER_HTC = "HTC";

    public static void rejectCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            method.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            ITelephony telephonyInterface = (ITelephony) method.invoke(telephonyManager);

            telephonyInterface.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm);

            c = Class.forName(telephonyService.getClass().getName());
            m = c.getDeclaredMethod("endCall");
            m.setAccessible(true);
            m.invoke(telephonyService);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);
        } catch (Exception e) {
        }

        try {
            Class cls = Class.forName("com.android.internal.telephony.ITelephony");
            Class cls2 = cls.getClasses()[0];
            Class cls3 = Class.forName("android.os.ServiceManager");
            Class cls4 = Class.forName("android.os.ServiceManagerNative");
            Method method = cls3.getMethod("getService", new Class[]{String.class});
            Method method2 = cls4.getMethod("asInterface", new Class[]{IBinder.class});
            Binder binder = new Binder();
            binder.attachInterface(null, "fake");
            IBinder iBinder = (IBinder) method.invoke(method2.invoke(null, new Object[]{binder}), new Object[]{"phone"});
            cls.getMethod("endCall", new Class[0]).invoke(cls2.getMethod("asInterface", new Class[]{IBinder.class}).invoke(null, new Object[]{iBinder}), new Object[0]);
        } catch (Exception th) {
        }

        NotificationServiceCustom.rejectCall();
    }

    public static void acceptCall(final Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");

            // Disable access check
            method.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            ITelephony telephonyInterface = (ITelephony) method.invoke(telephonyManager);

            telephonyInterface.answerRingingCall();
        } catch (Exception e) {
        }

        try {
            Class cls = Class.forName("com.android.internal.telephony.ITelephony");
            Class cls2 = cls.getClasses()[0];
            Class cls3 = Class.forName("android.os.ServiceManager");
            Class cls4 = Class.forName("android.os.ServiceManagerNative");
            Method method = cls3.getMethod("getService", new Class[]{String.class});
            Method method2 = cls4.getMethod("asInterface", new Class[]{IBinder.class});
            Binder binder = new Binder();
            binder.attachInterface(null, "fake");
            IBinder iBinder = (IBinder) method.invoke(method2.invoke(null, new Object[]{binder}), new Object[]{"phone"});
            cls.getMethod("answerRingingCall", new Class[0]).invoke(cls2.getMethod("asInterface", new Class[]{IBinder.class}).invoke(null, new Object[]{iBinder}), new Object[0]);
        } catch (Exception e) {
        }

        AudioManager audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Runtime.getRuntime().exec("input keyevent " +
                            Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
                } catch (IOException e) {
                    // Runtime.exec(String) had an I/O problem, try to fall back
                    String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                    Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                            Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                    KeyEvent.KEYCODE_HEADSETHOOK));
                    Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                            Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                    KeyEvent.KEYCODE_HEADSETHOOK));

                    context.getApplicationContext().sendOrderedBroadcast(btnDown, enforcedPerm);
                    context.getApplicationContext().sendOrderedBroadcast(btnUp, enforcedPerm);
                }
            }

        }).start();

        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER)
                && !audioManager.isWiredHeadsetOn();

        if (broadcastConnected) {
            broadcastHeadsetConnected(context.getApplicationContext(), false);
        }

        try {
            try {
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));

                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));

                context.getApplicationContext().sendOrderedBroadcast(btnDown, enforcedPerm);
                context.getApplicationContext().sendOrderedBroadcast(btnUp, enforcedPerm);
            } catch (Exception e) {
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));

                context.getApplicationContext().sendOrderedBroadcast(btnDown, enforcedPerm);
                context.getApplicationContext().sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(context.getApplicationContext(), false);
            }
        }

        NotificationServiceCustom.answerCall();
    }

    private static void broadcastHeadsetConnected(Context context, boolean connected) {
        Intent intent = new Intent(Intent.ACTION_HEADSET_PLUG);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.putExtra("state", connected ? 1 : 0);
        intent.putExtra("name", "mysms");
        try {
            context.sendOrderedBroadcast(intent, null);
        } catch (Exception e) {
        }
    }

}
