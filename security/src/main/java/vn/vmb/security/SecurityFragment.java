package vn.vmb.security;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import vn.vmb.security.listener.OnCreatePasswordListener;
import vn.vmb.security.listener.OnStepCreatePasswordListener;
import vn.vmb.security.listener.OnUnlockListener;

/**
 * Created by ngson on 20/09/2017.
 */

public class SecurityFragment extends Fragment implements OnStepCreatePasswordListener {
    private final String TAG = getClass().getSimpleName();


    private Setting mSetting;

    private String mSessionId;
    private TypeLock mTypeLock;
    private Action mAction;

    protected OnUnlockListener mOnUnlockListener;
    protected OnCreatePasswordListener mOnCreatePasswordListener;

    public static SecurityFragment newInstance(@NonNull String sessionId, @NonNull TypeLock typeLock, @NonNull Action action) {
        Bundle args = new Bundle();
        args.putString(AppLock.EXTRA_SESSION_ID, sessionId);
        args.putInt(AppLock.EXTRA_TYPE_LOCK, typeLock.ordinal());
        args.putInt(AppLock.EXTRA_ACTION, action.ordinal());

        SecurityFragment fragment = new SecurityFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mSessionId = args.getString(AppLock.EXTRA_SESSION_ID);
        mTypeLock = TypeLock.values()[args.getInt(AppLock.EXTRA_TYPE_LOCK)];
        mAction = Action.values()[args.getInt(AppLock.EXTRA_ACTION)];

        mSetting = AppLock.getSetting(mSessionId);
    }

    //    @BindView(R2.id.top)
    private FrameLayout mTopView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_security, container, false);

        mTopView = root.findViewById(R.id.top);

        if (mAction == Action.UNLOCK) {
            setupTopView(mTopView);
            mTypeLock = mSetting.getTypeLock();
        }

        BaseSecurityFragment fragment;

        if (mTypeLock == TypeLock.PASSCODE)
            fragment = PasscodeFragment.newInstance(mSessionId, mAction);
        else if (mTypeLock == TypeLock.PATTERN)
            fragment = PatternFragment.newInstance(mSessionId, mAction);
        else fragment = FingerprintFragment.newInstance(mSessionId, mAction);

        fragment.setOnCreatePasswordStepListener(this);
        fragment.setOnUnlockListener(mOnUnlockListener);

        getChildFragmentManager().beginTransaction().replace(R.id.lock, fragment).commit();

        return root;

    }

    public void setupTopView(FrameLayout topView) {

    }

    @Override
    public void onStep(StepCreatePassword step) {
        if (mOnCreatePasswordListener != null && step == StepCreatePassword.CREATE_PASSWORD_SUCCESS)
            mOnCreatePasswordListener.onCreatePasswordSuccess();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = getActivity();

        if (activity instanceof OnUnlockListener) mOnUnlockListener = (OnUnlockListener) activity;

        if (activity instanceof OnCreatePasswordListener)
            mOnCreatePasswordListener = (OnCreatePasswordListener) activity;
    }
}
