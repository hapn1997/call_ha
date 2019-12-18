package vn.vmb.security.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import vn.vmb.security.Action;
import vn.vmb.security.R;
import vn.vmb.security.Setting;
import vn.vmb.security.StepCreatePassword;
import vn.vmb.security.TypeLock;

/**
 * Created by ngson on 07/11/2017.
 */

public class PatternView extends BaseSecurityView implements me.zhanghai.android.patternlock.PatternView.OnPatternListener {
    private TextView mCurrentStep;
    private me.zhanghai.android.patternlock.PatternView mPatternView;

    private Setting mSetting;
    private String mPattern;

    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            mPatternView.clearPattern();
        }
    };

    public PatternView(Context context, String sessionId, Action action) {
        super(context, sessionId, action);
    }


    @Override
    public void initViews() {
        mSetting = getSettingForSessionById();

        inflate(getContext(), R.layout.fragment_pattern, this);

        mCurrentStep = findViewById(R.id.current_step);
        mPatternView = findViewById(R.id.pattern);

        if (getAction() == Action.CREATE_PASSWORD) {
            notifyStepCreatePassword(StepCreatePassword.SET_PASSWORD);
            mCurrentStep.setVisibility(View.VISIBLE);
        } else mCurrentStep.setVisibility(View.GONE);

        mPatternView.setOnPatternListener(this);
    }

    @Override
    protected void notifyStepCreatePassword(StepCreatePassword step) {
        super.notifyStepCreatePassword(step);

        if (step == StepCreatePassword.SET_PASSWORD) mCurrentStep.setText(R.string.draw_pattern);
        else if (step == StepCreatePassword.CONFIRM_PASSWORD) mCurrentStep.setText(R.string.confirm_pattern);
    }

    private void removeClearPatternRunnable() {
        mPatternView.removeCallbacks(clearPatternRunnable);
    }

    @Override
    public void onPatternStart() {
        removeClearPatternRunnable();

        // Set display mode to correct to ensure that pattern can be in stealth mode.
        mPatternView.setDisplayMode(me.zhanghai.android.patternlock.PatternView.DisplayMode.Correct);
    }

    protected void postClearPatternRunnable() {
        removeClearPatternRunnable();
        mPatternView.postDelayed(clearPatternRunnable, 1000);
    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<me.zhanghai.android.patternlock.PatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<me.zhanghai.android.patternlock.PatternView.Cell> pattern) {
        if (getAction() == Action.CREATE_PASSWORD) {
            if (mPattern == null) {
                mPattern = patternToString(pattern);
                notifyStepCreatePassword(StepCreatePassword.CONFIRM_PASSWORD);
                mPatternView.clearPattern();
            } else {
                if (isPatternCorrect(pattern)) {
                    mSetting.setTypeLock(TypeLock.PATTERN);
                    mSetting.putPassword(mPattern);
                    notifyStepCreatePassword(StepCreatePassword.CREATE_PASSWORD_SUCCESS);
                } else {
                    mPatternView.setDisplayMode(me.zhanghai.android.patternlock.PatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                }
            }
        } else {
            String patternString = patternToString(pattern);

            if (patternString.equals(mSetting.getPassword())) {
                notifyStepUnlock(true);
            } else {
                notifyStepUnlock(false);
                mPatternView.setDisplayMode(me.zhanghai.android.patternlock.PatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
            }
        }
    }

    private boolean isPatternCorrect(List<me.zhanghai.android.patternlock.PatternView.Cell> pattern) {
        String patternConfirm = patternToString(pattern);

        return mPattern.equals(patternConfirm);
    }

    private String patternToString(List<me.zhanghai.android.patternlock.PatternView.Cell> pattern) {
        StringBuilder builder = new StringBuilder();

        for (me.zhanghai.android.patternlock.PatternView.Cell cell : pattern) {
            int row = cell.getRow();
            int column = cell.getColumn();

            builder.append(row + "" + column + "-");
        }

        return builder.toString();
    }
}
