package vn.com.call.editCall;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TelecomUtils {
    public final static String OnIncomingCallReceived ="onIncomingCallReceived";
    public final static String OnIncomingCallAnswered ="onIncomingCallAnswered";
    public final static String OnIncomingCallEnded ="onIncomingCallEnded";
    public final static String OnOutgoingCallStarted ="onOutgoingCallStarted";
    public final static String OnOutgoingCallEnded ="onOutgoingCallEnded";
    public final static String OnOutgoingCallAnswered ="onOutgoingCallAnswered";
    public final static String OnMissedCall ="onMissedCall";
    public static final PhoneAccountHandle getDefaultOutgoingPhoneAccount(Activity activity, TelecomManager telecomManager, String str) {

        if (telecomManager == null) {
            return null;
        }
        if (!CallerHelper.isMethodAvailable(Constants.TELECOM_MANAGER_CLASS, "getDefaultOutgoingPhoneAccount", String.class)) {
            return null;
        }
        if (activity.checkPermission( "android.permission.READ_PHONE_STATE", Process.myUid(),Process.myPid()) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return telecomManager.getDefaultOutgoingPhoneAccount(str);
            }
        }
        return null;
    }

    public static final String getDefaultDialerPackage(TelecomManager telecomManager) {
        if (telecomManager == null ) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return telecomManager.getDefaultDialerPackage();
        }
        return null;
    }
    private static final TelecomManager getTelecomManager(Context context) {
        Object systemService = context == null ? null : context.getSystemService(Context.TELECOM_SERVICE);
        return (TelecomManager) systemService;
    }

    public static String formatTime(int time, boolean check, int i3) {
        if ((i3 & 1) != 0) {
            check = false;
        }
        StringBuilder sb = new StringBuilder(8);
        int i4 = time / 3600;
        int i5 = (time % 3600) / 60;
        int i6 = time % 60;
        if (time >= 3600) {
            String format = String.format(Locale.getDefault(), "%02d", Arrays.copyOf(new Object[]{Integer.valueOf(i4)}, 1));
            sb.append(format);
            sb.append(":");
        } else if (check) {
            sb.append("0:");
        }
        String format2 = String.format(Locale.getDefault(), "%02d", Arrays.copyOf(new Object[]{Integer.valueOf(i5)}, 1));
        sb.append(format2);
        sb.append(":");
        String format3 = String.format(Locale.getDefault(), "%02d", Arrays.copyOf(new Object[]{Integer.valueOf(i6)}, 1));
        sb.append(format3);
        String sb2 = sb.toString();
        return sb2;
    }

    public static final void cancelMissedCallsNotification(Context context) {

        if (hasModifyPhoneStatePermission(context)) {
            try {

                getTelecomManager(context).cancelMissedCallsNotification();
            } catch (NullPointerException e10) {
                e10.printStackTrace();
            } catch (SecurityException e11) {
                e11.printStackTrace();
            }
        }
    }
    public static final boolean hasModifyPhoneStatePermission(Context context) {
        return isDefaultDialer(context) || hasPermission(context, "android.permission.MODIFY_PHONE_STATE");
    }
    private static boolean sWarningLogged;

    public static final boolean isDefaultDialer(Context context) {
        boolean equals = TextUtils.equals(context.getPackageName(), getDefaultDialerPackage(getTelecomManager(context)));
        if (equals) {
            sWarningLogged = false;
        } else if (!sWarningLogged) {
            sWarningLogged = true;
        }
        return equals;
    }
    static final boolean hasPermission(Context context, String str) {
        return chenk(context, str) == 0;
    }


    public static int chenk(Context context, String per) {
        if (per != null) {
            return context.checkPermission(per, Process.myPid(), Process.myUid());
        }
        throw new IllegalArgumentException("permission is null");

    }


}
