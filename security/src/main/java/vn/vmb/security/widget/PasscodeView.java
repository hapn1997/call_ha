package vn.vmb.security.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import vn.vmb.security.Action;
import vn.vmb.security.R;
import vn.vmb.security.Setting;
import vn.vmb.security.StepCreatePassword;
import vn.vmb.security.TypeLock;

import static vn.vmb.security.PasscodeFragment.MAX_LENGTH;

/**
 * Created by ngson on 07/11/2017.
 */

public class PasscodeView extends BaseSecurityView implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private Setting mSetting;

    private String mPassword;
    private String mPasswordConfirm;

    private EditText mPasscode;
    private TextView mCurrentStep;
    private View mNext;

    public PasscodeView(Context context, String sessionId, Action action) {
        super(context, sessionId, action);
    }

    @Override
    public void initViews() {
        Log.wtf("PasscodeView", "initViews");
        mSetting = getSettingForSessionById();

        inflate(getContext(), R.layout.fragment_passcode, this);

        mPasscode = findViewById(R.id.passcode);
        mCurrentStep = findViewById(R.id.current_step);
        mNext = findViewById(R.id.next);

        findViewById(R.id.zero).setOnClickListener(this);
        findViewById(R.id.one).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.three).setOnClickListener(this);
        findViewById(R.id.four).setOnClickListener(this);
        findViewById(R.id.five).setOnClickListener(this);
        findViewById(R.id.six).setOnClickListener(this);
        findViewById(R.id.seven).setOnClickListener(this);
        findViewById(R.id.eight).setOnClickListener(this);
        findViewById(R.id.nine).setOnClickListener(this);
        findViewById(R.id.backspace).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);

        if (getAction() == Action.CREATE_PASSWORD) {
            notifyStepCreatePassword(StepCreatePassword.SET_PASSWORD);
            mNext.setVisibility(View.VISIBLE);
            mCurrentStep.setVisibility(View.VISIBLE);
        } else {
            mNext.setVisibility(View.GONE);
            mCurrentStep.setVisibility(View.GONE);
        }

        mPasscode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (getAction() == Action.UNLOCK) {
                    String password = mPasscode.getText().toString();

                    if (password.length() == MAX_LENGTH) {
                        if (password.equals(mSetting.getPassword())) notifyStepUnlock(true);
                        else {
                            notifyStepUnlock(false);
                            mPasscode.setTextColor(Color.RED);
                            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                            shake.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    mPasscode.setText("");
                                    mPasscode.setTextColor(Color.BLACK);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                            mPasscode.startAnimation(shake);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void append(String text) {
        if (mPasscode.length() < 6) mPasscode.append(text);
    }

    @Override
    protected void notifyStepCreatePassword(StepCreatePassword step) {
        super.notifyStepCreatePassword(step);

        if (step == StepCreatePassword.SET_PASSWORD) mCurrentStep.setText(R.string.set_password);
        else if (step == StepCreatePassword.CONFIRM_PASSWORD) mCurrentStep.setText(R.string.confirm_password);
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();

        if (idView == R.id.zero) append("0");
        else if (idView == R.id.one) append("1");
        else if (idView == R.id.two) append("2");
        else if (idView == R.id.three) append("3");
        else if (idView == R.id.four) append("4");
        else if (idView == R.id.five) append("5");
        else if (idView == R.id.six) append("6");
        else if (idView == R.id.seven) append("7");
        else if (idView == R.id.eight) append("8");
        else if (idView == R.id.nine) append("9");
        else if (idView == R.id.backspace) {
            if (mPasscode.length() > 0) {
                String text = mPasscode.getText().toString();

                mPasscode.setText(text.substring(0, text.length() - 1));
            }
        } else if (idView == R.id.next) {
            if (mPasscode.length() < MAX_LENGTH) Toast.makeText(getContext(), R.string.alert_not_enough_digits, Toast.LENGTH_SHORT).show();
            else if (getAction() == Action.CREATE_PASSWORD) {
                if (mPassword == null) {
                    mPassword = mPasscode.getText().toString();

                    mPasscode.setText("");
                    notifyStepCreatePassword(StepCreatePassword.CONFIRM_PASSWORD);
                } else {
                    if (mPassword.equals(mPasscode.getText().toString())) {
                        mSetting.putPassword(mPassword);
                        notifyStepCreatePassword(StepCreatePassword.CREATE_PASSWORD_SUCCESS);
                        mSetting.setTypeLock(TypeLock.PASSCODE);
                    }
                }
            }
        }
    }
}
