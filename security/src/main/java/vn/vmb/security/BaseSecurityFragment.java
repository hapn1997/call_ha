package vn.vmb.security;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.vmb.security.listener.OnStepCreatePasswordListener;
import vn.vmb.security.listener.OnUnlockListener;

/**
 * Created by ngson on 20/09/2017.
 */

abstract class BaseSecurityFragment extends Fragment {
    private final String TAG = super.getClass().getSimpleName();

    private String mSessionId;
    private Action mAction;

    private OnStepCreatePasswordListener mStepOnCreatePasswordListener;
    private OnUnlockListener mOnUnlockListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mSessionId = args.getString(AppLock.EXTRA_SESSION_ID);
        mAction = Action.values()[args.getInt(AppLock.EXTRA_ACTION, 0)];

        Log.wtf(TAG, mSessionId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);

        onCreateView(savedInstanceState, rootView);

        return rootView;
    }

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

    protected abstract int getLayoutId();

    protected abstract void onCreateView(@Nullable Bundle savedInstanceState, View view);

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
