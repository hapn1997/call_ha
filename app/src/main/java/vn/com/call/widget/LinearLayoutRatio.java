package vn.com.call.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;


import vn.com.call.R;

/**
 * Created by ngson on 11/07/2017.
 */

public class LinearLayoutRatio extends LinearLayout {
    private static final int WIDTH = 0;
    private static final int HEIGHT = 1;

    private int datum;
    private float ratioWidth;
    private float ratioHeight;

    public LinearLayoutRatio(Context context) {
        super(context);
    }

    public LinearLayoutRatio(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        getAttrs(attrs);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ImageViewRatio);

        try {
            ratioWidth = typedArray.getFloat(R.styleable.ImageViewRatio_ratioWidth, -1);
            ratioHeight = typedArray.getFloat(R.styleable.ImageViewRatio_ratioHeight, -1);
            datum = typedArray.getInt(R.styleable.ImageViewRatio_datum, 0);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratioWidth > 0 && ratioHeight > 0) {
            int w;
            int h;

            if (datum == WIDTH) {
                w = MeasureSpec.getSize(widthMeasureSpec);
                h = (int) (((float)w / ratioWidth) * ratioHeight);
            } else {
                h = MeasureSpec.getSize(heightMeasureSpec);
                w = (int) (((float)h / ratioHeight) * ratioWidth);
            }

            widthMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
