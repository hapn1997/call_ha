package vn.vmb.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.securepreferences.SecurePreferences;

/**
 * Created by ngson on 20/09/2017.
 */

public class Setting {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private Handler mHandler = new Handler();
    private Runnable mUpdateLockState = new Runnable() {
        @Override
        public void run() {
            setLock(true);
        }
    };

    private final String STATE_LOCK = "state_lock";
    private final String TYPE_LOCK = "type_lock";
    private final String PASSWORD = "password";
    private final String KEEP_UNLOCK_IN = "keep_unlock_in";
    private final String UNLOCK_AT = "unlock_at";

    public Setting(Context context, String sessionId, String password) {
        mPreferences = new SecurePreferences(context, password, sessionId);
        mEditor = mPreferences.edit();
    }

    public void setTypeLock(TypeLock typeLock) {
        mEditor.putInt(TYPE_LOCK, typeLock.ordinal());
        mEditor.apply();
    }

    public TypeLock getTypeLock() {
        return TypeLock.values()[mPreferences.getInt(TYPE_LOCK, 0)];
    }

    public void putPassword(String value) {
        mEditor.putString(PASSWORD, value);
        mEditor.apply();
    }

    public void setKeepUnLockInMillisecond(long millisecond) {
        mEditor.putLong(KEEP_UNLOCK_IN, millisecond);
        mEditor.apply();
    }

    public long getKeepUnLockInMillisecond() {
        return mPreferences.getLong(KEEP_UNLOCK_IN, 0l);
    }

    public String getPassword() {
        return mPreferences.getString(PASSWORD, null);
    }

    public void setLock(boolean lock) {
        if (lock) {
            long unlockTime = System.currentTimeMillis() - unlockAt();

            if (unlockTime > getKeepUnLockInMillisecond()) mEditor.putBoolean(STATE_LOCK, lock);
            else mHandler.postDelayed(mUpdateLockState, getKeepUnLockInMillisecond() - unlockTime);
        } else {
            mEditor.putBoolean(STATE_LOCK, lock);
            mEditor.putLong(UNLOCK_AT, System.currentTimeMillis());
        }

        mEditor.apply();
    }

    public boolean isLock() {
        mHandler.removeCallbacks(mUpdateLockState);

        return mPreferences.getBoolean(STATE_LOCK, false) && !isNeedCreatePassword();
    }

    private long unlockAt() {
        return mPreferences.getLong(UNLOCK_AT, 0l);
    }

    public boolean isNeedCreatePassword() {
        return getPassword() == null;
    }
}
