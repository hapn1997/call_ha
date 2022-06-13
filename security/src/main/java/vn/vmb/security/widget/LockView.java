package vn.vmb.security.widget;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import vn.vmb.security.Action;
import vn.vmb.security.AppLock;
import vn.vmb.security.R;
import vn.vmb.security.Setting;
import vn.vmb.security.StepCreatePassword;
import vn.vmb.security.TypeLock;
import vn.vmb.security.listener.OnCreatePasswordListener;
import vn.vmb.security.listener.OnStepCreatePasswordListener;
import vn.vmb.security.listener.OnUnlockListener;

/**
 * Created by ngson on 07/11/2017.
 */

public class LockView extends FrameLayout implements OnStepCreatePasswordListener {
    private BaseSecurityView mSecurityView;
    private Setting mSetting;

    private String mSessionId;
    private TypeLock mTypeLock;
    private Action mAction;

    protected OnUnlockListener mOnUnlockListener;
    protected OnCreatePasswordListener mOnCreatePasswordListener;

    public LockView(Context context, Bundle args) {
        super(context);

        setClickable(true);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        initSession(args);
    }

    public void initSession(Bundle args) {
        mSessionId = args.getString(AppLock.EXTRA_SESSION_ID);
        mTypeLock = TypeLock.values()[args.getInt(AppLock.EXTRA_TYPE_LOCK)];
        mAction = Action.values()[args.getInt(AppLock.EXTRA_ACTION)];

        mSetting = AppLock.getSetting(mSessionId);

        if (mAction == Action.UNLOCK) {
            mTypeLock = mSetting.getTypeLock();
        }

        if (mTypeLock == TypeLock.PASSCODE) mSecurityView = new PasscodeView(getContext(), mSessionId, mAction);
        else if (mTypeLock == TypeLock.PATTERN) mSecurityView = new PatternView(getContext(), mSessionId, mAction);
        else mSecurityView = new FingerprintView(getContext(), mSessionId, mAction);

        mSecurityView.setOnCreatePasswordStepListener(this);
        mSecurityView.setOnUnlockListener(mOnUnlockListener);

        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        addView(mSecurityView, params);
    }

    @Override
    public void onStep(StepCreatePassword step) {
        if (mOnCreatePasswordListener != null && step == StepCreatePassword.CREATE_PASSWORD_SUCCESS)
            mOnCreatePasswordListener.onCreatePasswordSuccess();
    }

    public void setOnUnlockListener(OnUnlockListener listener) {
        mOnUnlockListener = listener;
        mSecurityView.setOnUnlockListener(listener);
    }
}
