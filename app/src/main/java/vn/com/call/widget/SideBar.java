package vn.com.call.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import vn.com.call.R;

public class SideBar extends View {

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    public static String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    private TextView mTextDialog;

    private int choose = -1;

    private Paint paint = new Paint();

    public SideBar(Context context) {
        super(context);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / (letters.length+10);

        for (int i = 0; i < letters.length; i++) {
            int color = ContextCompat.getColor(getContext(), R.color.blue_ios);
            paint.setColor(color);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(spToPx(11));
            if (i == choose) {
                paint.setColor(Color.parseColor("#1181ff"));
                paint.setFakeBoldText(true);
            }

            float xPos = width / 2 - paint.measureText(letters[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(letters[i], xPos, yPos, paint);
            paint.reset();
        }

    }


    @SuppressLint("ResourceType")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;

        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;


        int c = (int) (y / getHeight() * letters.length);

        switch (action) {

            case MotionEvent.ACTION_UP:

                setBackgroundDrawable(new ColorDrawable(0x00000000));

                choose = -1;
                //
                invalidate();

                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }

                break;

            default:


                setBackgroundResource(Color.TRANSPARENT);

                if (oldChoose != c) {
                    if (c >= 0 && c < letters.length) {

                        if (listener != null) {
                            listener.onTouchingLetterChanged(letters[c]);
                        }

                        if (mTextDialog != null) {
                            mTextDialog.setText(letters[c]);
                            mTextDialog.setVisibility(View.VISIBLE);

                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }


    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }



    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    public int spToPx(int px) {
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px, getResources().getDisplayMetrics()));
    }
}
