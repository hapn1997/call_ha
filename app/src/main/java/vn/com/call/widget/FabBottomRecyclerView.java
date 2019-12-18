package vn.com.call.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by ngson on 30/06/2017.
 */

public class FabBottomRecyclerView extends RecyclerView {
    private final String TAG = FabBottomRecyclerView.class.getSimpleName();

    private enum DirectionScroll {
        TO_BOTTOM, TO_TOP, NONE
    }

    private int bottomMargin;
    private int bottomSize;
    private DirectionScroll mDirectionScroll = DirectionScroll.NONE;
    private float lastPercentScroll = 0;

    private View bottom;

    public FabBottomRecyclerView(Context context) {
        super(context);
    }

    public FabBottomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setupWithView(final View bottom) {
        this.bottom = bottom;
        bottomMargin = ((FrameLayout.LayoutParams) bottom.getLayoutParams()).bottomMargin;

        bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomSize = bottom.getHeight();
                bottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        if (Build.VERSION.SDK_INT >= 23) setOnScrollApi23();
        else setOnScroll();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setOnScrollApi23() {
        setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                translationBottom();
            }
        });
    }

    private void setOnScroll() {
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                translationBottom();
            }
        });
    }

    private void translationBottom() {
        float currentPercentScroll = ((float) computeVerticalScrollOffset()/computeVerticalScrollRange());
        if (bottomSize == 0) {
            bottomSize = bottom.getHeight();
        }
        if (currentPercentScroll > lastPercentScroll && mDirectionScroll != DirectionScroll.TO_TOP) {
            mDirectionScroll = DirectionScroll.TO_TOP;

            bottom.animate().translationY(bottomMargin + bottomSize);
        } else if (currentPercentScroll < lastPercentScroll && mDirectionScroll != DirectionScroll.TO_BOTTOM) {
            mDirectionScroll = DirectionScroll.TO_BOTTOM;

            bottom.animate().translationY(0);
        }

        lastPercentScroll = currentPercentScroll;
    }
}
