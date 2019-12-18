package vn.vmb.security;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;

import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by ngson on 20/09/2017.
 */

public class AppLock {
    private final static String TAG = AppLock.class.getSimpleName();

    public static final String EXTRA_SESSION_ID = "session_id";
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_TYPE_LOCK = "type_lock";

    private static HashMap<String, Setting> sSettings = new HashMap<>();

    public static void initAppLock(Context context, @NonNull String sessionId, @NonNull String password) {
        if (password == null || sessionId == null) throw new IllegalStateException("password, sessionId should be not null");
        else {
            Setting setting = new Setting(context, sessionId, password);
            sSettings.put(sessionId, setting);
            setting.setLock(true);
        }
    }

    public static boolean isLock(@NonNull String sessionId) {
        Log.wtf(TAG, sessionId);
        if (getSetting(sessionId) == null) throw new IllegalStateException("need init before use");

        return getSetting(sessionId).isLock();
    }

    public static void setLock(boolean isLock, @NonNull String sessionId) {
        if (getSetting(sessionId) == null) throw new IllegalStateException("need init before use");

        getSetting(sessionId).setLock(isLock);
    }

    public static TypeLock getTypeLock(@NonNull String sessionId) {
        return getSetting(sessionId).getTypeLock();
    }

    public static void setTypeLock(@NonNull String sessionId, TypeLock typeLock) {
        getSetting(sessionId).setTypeLock(typeLock);
    }

    public static void setKeepUnlockInMillisecond(@NonNull String sessionId, long millisecond) {
        if (getSetting(sessionId) == null) throw new IllegalStateException("need init before use");

        getSetting(sessionId).setKeepUnLockInMillisecond(millisecond);
    }

    public static long getKeepUnlockInMillisecond(@NonNull String sessionId) {
        return getSetting(sessionId).getKeepUnLockInMillisecond();
    }

    public static boolean isNeedCreatePassword(@NonNull String sessionId) {
        return getSetting(sessionId).isNeedCreatePassword();
    }

    public static Setting getSetting(String sessionId) {
        Log.wtf(TAG, sSettings.size() + " " + sSettings.keySet().toString() + " " + sessionId);
        return sSettings.get(sessionId);
    }

    public static boolean isSupportFingerprint(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);

            return fingerprintManager.isHardwareDetected();
        }

        return false;
    }
}
