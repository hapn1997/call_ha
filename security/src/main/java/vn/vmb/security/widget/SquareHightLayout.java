package vn.vmb.security.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by ngson on 07/11/2017.
 */

public class SquareHightLayout extends LinearLayout {
    public SquareHightLayout(Context context) {
        super(context);
    }

    public SquareHightLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
