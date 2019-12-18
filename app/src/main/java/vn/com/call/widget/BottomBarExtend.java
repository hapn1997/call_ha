package vn.com.call.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;


import vn.com.call.R;

/**
 * Created by ngson on 03/07/2017.
 */

public class BottomBarExtend extends BottomBar {
    private static final String TAG = BottomBarExtend.class.getSimpleName();

    private boolean hideTitleTab;

    public BottomBarExtend(Context context) {
        super(context);
    }

    public BottomBarExtend(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarExtend(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BottomBarExtend);

        try {
            hideTitleTab = typedArray.getBoolean(R.styleable.BottomBarExtend_hideTitle, false);
        } finally {
            typedArray.recycle();
        }

        if (hideTitleTab)
            hideTitleTab();
        else
            setTextSize();
    }

    private void hideTitleTab() {
        for (int i = 0; i < getTabCount(); i++) {
            BottomBarTab tab = getTabAtPosition(i);
            tab.setGravity(Gravity.CENTER);

            View icon = tab.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_icon);
            // the paddingTop will be modified when select/deselect,
            // so, in order to make the icon always center in tab,
            // we need set the paddingBottom equals paddingTop
            icon.setPadding(0, icon.getPaddingTop(), 0, icon.getPaddingTop());

            View title = tab.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_title);
            title.setVisibility(View.GONE);
        }
    }

    private void setTextSize() {
        int textSizeInSp = (int) getResources().getDimension(R.dimen.text_size_small);
        int topMargin = (int) getResources().getDimension(R.dimen.margin_tinyxxx);
        for (int i = 0; i < getTabCount(); i++) {
            BottomBarTab tab = getTabAtPosition(i);
            TextView title = tab.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_title);
            LinearLayout.LayoutParams lastTxtParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lastTxtParams.setMargins(0, topMargin, 0, 0);
            title.setLayoutParams(lastTxtParams);
            title.setVisibility(View.VISIBLE);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInSp);
        }
    }
}
