package vn.vmb.security;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ngson on 20/09/2017.
 */

public class PasscodeFragment extends BaseSecurityFragment implements View.OnClickListener {
    public static final int MAX_LENGTH = 6;

    private Setting mSetting;

    private String mPassword;
    private String mPasswordConfirm;

    private void append(String text) {
        if (mPasscode.length() < 6) mPasscode.append(text);
    }

    public static PasscodeFragment newInstance(String sessionId, Action action) {
        Bundle args = new Bundle();
        args.putString(AppLock.EXTRA_SESSION_ID, sessionId);
        args.putInt(AppLock.EXTRA_ACTION, action.ordinal());

        PasscodeFragment fragment = new PasscodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSetting = getSettingForSessionById();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_passcode;
    }

    //    @BindView(R2.id.current_step)
    private TextView mCurrentStep;
    //    @BindView(R2.id.passcode)
    private EditText mPasscode;
    //    @BindView(R2.id.next)
    private View mNext;


//    @OnClick(R2.id.one)
//    void one() {
//        append("1");
//    }
//
//    @OnClick(R2.id.two)
//    void two() {
//        append("2");
//    }
//
//    @OnClick(R2.id.three)
//    void three() {
//        append("3");
//    }
//
//    @OnClick(R2.id.four)
//    void four() {
//        append("4");
//    }
//
//    @OnClick(R2.id.five)
//    void five() {
//        append("5");
//    }
//
//    @OnClick(R2.id.six)
//    void six() {
//        append("6");
//    }
//
//    @OnClick(R2.id.seven)
//    void seven() {
//        append("7");
//    }
//
//    @OnClick(R2.id.eight)
//    void eight() {
//        append("8");
//    }
//
//    @OnClick(R2.id.nine)
//    void nine() {
//        append("9");
//    }
//
//    @OnClick(R2.id.zero)
//    void zero() {
//        append("0");
//    }
//
//    @OnClick(R2.id.backspace)
//    void backspace() {
//        if (mPasscode.length() > 0) {
//            String text = mPasscode.getText().toString();
//
//            mPasscode.setText(text.substring(0, text.length() - 1));
//        }
//    }
//
//    @OnClick(R2.id.next)
//    void next() {
//        if (mPasscode.length() < MAX_LENGTH)
//            Toast.makeText(getContext(), R.string.alert_not_enough_digits, Toast.LENGTH_SHORT).show();
//        else if (getAction() == Action.CREATE_PASSWORD) {
//            if (mPassword == null) {
//                mPassword = mPasscode.getText().toString();
//
//                mPasscode.setText("");
//                notifyStepCreatePassword(StepCreatePassword.CONFIRM_PASSWORD);
//            } else {
//                if (mPassword.equals(mPasscode.getText().toString())) {
//                    mSetting.putPassword(mPassword);
//                    notifyStepCreatePassword(StepCreatePassword.CREATE_PASSWORD_SUCCESS);
//                    mSetting.setTypeLock(TypeLock.PASSCODE);
//                }
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.one) {
            append("1");
        } else if (v.getId() == R.id.two) {
            append("2");
        } else if (v.getId() == R.id.three) {
            append("3");
        } else if (v.getId() == R.id.four) {
            append("4");
        } else if (v.getId() == R.id.five) {
            append("5");
        } else if (v.getId() == R.id.six) {
            append("6");
        } else if (v.getId() == R.id.seven) {
            append("7");
        } else if (v.getId() == R.id.eight) {
            append("8");
        } else if (v.getId() == R.id.nine) {
            append("9");
        } else if (v.getId() == R.id.zero) {
            append("0");
        } else if (v.getId() == R.id.backspace) {
            if (mPasscode.length() > 0) {
                String text = mPasscode.getText().toString();
                mPasscode.setText(text.substring(0, text.length() - 1));
            }
        } else if (v.getId() == R.id.next) {
            if (mPasscode.length() < MAX_LENGTH)
                Toast.makeText(getContext(), R.string.alert_not_enough_digits, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState, View view) {
        mCurrentStep = view.findViewById(R.id.current_step);
        mPasscode = view.findViewById(R.id.passcode);
        mNext = view.findViewById(R.id.next);

        view.findViewById(R.id.one).setOnClickListener(this);
        view.findViewById(R.id.two).setOnClickListener(this);
        view.findViewById(R.id.three).setOnClickListener(this);
        view.findViewById(R.id.four).setOnClickListener(this);
        view.findViewById(R.id.five).setOnClickListener(this);
        view.findViewById(R.id.six).setOnClickListener(this);
        view.findViewById(R.id.seven).setOnClickListener(this);
        view.findViewById(R.id.eight).setOnClickListener(this);
        view.findViewById(R.id.nine).setOnClickListener(this);
        view.findViewById(R.id.zero).setOnClickListener(this);
        view.findViewById(R.id.backspace).setOnClickListener(this);
        view.findViewById(R.id.next).setOnClickListener(this);

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

    @Override
    protected void notifyStepCreatePassword(StepCreatePassword step) {
        super.notifyStepCreatePassword(step);

        if (step == StepCreatePassword.SET_PASSWORD) mCurrentStep.setText(R.string.set_password);
        else if (step == StepCreatePassword.CONFIRM_PASSWORD)
            mCurrentStep.setText(R.string.confirm_password);
    }
}
