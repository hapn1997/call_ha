package vn.com.call.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by ngson on 13/06/2017.
 */

public class SquareHeightImageButton extends ImageButton {
    public SquareHeightImageButton(Context context) {
        super(context);
    }

    public SquareHeightImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
