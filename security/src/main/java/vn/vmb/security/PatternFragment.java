package vn.vmb.security;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import me.zhanghai.android.patternlock.PatternView;

/**
 * Created by ngson on 20/09/2017.
 */

public class PatternFragment extends BaseSecurityFragment implements PatternView.OnPatternListener {
    private final String TAG = getClass().getSimpleName();

    private Setting mSetting;
    private String mPattern;

    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            mPatternView.clearPattern();
        }
    };

    public static PatternFragment newInstance(String sessionId, Action action) {
        Bundle args = new Bundle();
        args.putString(AppLock.EXTRA_SESSION_ID, sessionId);
        args.putInt(AppLock.EXTRA_ACTION, action.ordinal());

        PatternFragment fragment = new PatternFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSetting = getSettingForSessionById();
    }

    //    @BindView(R2.id.current_step)
    private TextView mCurrentStep;
    //    @BindView(R2.id.pattern)
    private PatternView mPatternView;

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState, View view) {
        mCurrentStep = view.findViewById(R.id.current_step);
        mPatternView = view.findViewById(R.id.pattern);

        if (getAction() == Action.CREATE_PASSWORD) {
            notifyStepCreatePassword(StepCreatePassword.SET_PASSWORD);
            mCurrentStep.setVisibility(View.VISIBLE);
        } else mCurrentStep.setVisibility(View.GONE);

        mPatternView.setOnPatternListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pattern;
    }

    @Override
    protected void notifyStepCreatePassword(StepCreatePassword step) {
        super.notifyStepCreatePassword(step);

        if (step == StepCreatePassword.SET_PASSWORD) mCurrentStep.setText(R.string.draw_pattern);
        else if (step == StepCreatePassword.CONFIRM_PASSWORD)
            mCurrentStep.setText(R.string.confirm_pattern);
    }

    private void removeClearPatternRunnable() {
        mPatternView.removeCallbacks(clearPatternRunnable);
    }

    @Override
    public void onPatternStart() {
        removeClearPatternRunnable();

        // Set display mode to correct to ensure that pattern can be in stealth mode.
        mPatternView.setDisplayMode(PatternView.DisplayMode.Correct);
    }

    protected void postClearPatternRunnable() {
        removeClearPatternRunnable();
        mPatternView.postDelayed(clearPatternRunnable, 1000);
    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<PatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<PatternView.Cell> pattern) {
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
                    mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                }
            }
        } else {
            String patternString = patternToString(pattern);

            if (patternString.equals(mSetting.getPassword())) {
                notifyStepUnlock(true);
            } else {
                notifyStepUnlock(false);
                mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
            }
        }
    }

    private boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        String patternConfirm = patternToString(pattern);

        return mPattern.equals(patternConfirm);
    }

    private String patternToString(List<PatternView.Cell> pattern) {
        StringBuilder builder = new StringBuilder();

        for (PatternView.Cell cell : pattern) {
            int row = cell.getRow();
            int column = cell.getColumn();

            builder.append(row + "" + column + "-");
        }

        return builder.toString();
    }
}
