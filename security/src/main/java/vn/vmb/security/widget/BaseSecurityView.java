package vn.vmb.security.widget;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

import vn.vmb.security.Action;
import vn.vmb.security.AppLock;
import vn.vmb.security.Setting;
import vn.vmb.security.StepCreatePassword;
import vn.vmb.security.listener.OnStepCreatePasswordListener;
import vn.vmb.security.listener.OnUnlockListener;

/**
 * Created by ngson on 07/11/2017.
 */

public abstract class BaseSecurityView extends LinearLayout {
    private String mSessionId;
    private Action mAction;

    private OnStepCreatePasswordListener mStepOnCreatePasswordListener;
    private OnUnlockListener mOnUnlockListener;

    public BaseSecurityView(Context context, String sessionId, Action action) {
        super(context);

        mSessionId = sessionId;
        mAction = action;

        initViews();
    }

    protected abstract void initViews();

    public void setOnCreatePasswordStepListener(OnStepCreatePasswordListener listener) {
        mStepOnCreatePasswordListener = listener;
    }

    public void setOnUnlockListener(OnUnlockListener listener) {
        mOnUnlockListener = listener;
    }

    protected void notifyStepCreatePassword(StepCreatePassword step) {
        if (mStepOnCreatePasswordListener != null) mStepOnCreatePasswordListener.onStep(step);
    }

    protected void notifyStepUnlock(boolean isUnlockSuccess) {
        if (mOnUnlockListener != null) {
            if (isUnlockSuccess) mOnUnlockListener.onUnlockSuccess();
            else mOnUnlockListener.onUnlockFail();
        }
    }

    protected String getSessionId() {
        return mSessionId;
    }

    protected Action getAction() {
        return mAction;
    }

    protected Setting getSettingForSessionById() {
        return AppLock.getSetting(mSessionId);
    }
}
