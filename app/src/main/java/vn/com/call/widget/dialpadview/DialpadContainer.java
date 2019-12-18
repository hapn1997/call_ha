package vn.com.call.widget.dialpadview;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
/**
 * Created by ngson on 03/10/2017.
 */

public class DialpadContainer extends LinearLayout {
    private DialpadView mDialpadView;

    public DialpadContainer(Context context) {
        super(context, null);
    }

    public DialpadContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mDialpadView = new DialpadView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mDialpadView, params);

        fitViewToParent();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        fitViewToParent();
    }

    private void fitViewToParent() {
        mDialpadView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int parentHeight = getHeight();
                mDialpadView.fitParent(parentHeight);
                mDialpadView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
