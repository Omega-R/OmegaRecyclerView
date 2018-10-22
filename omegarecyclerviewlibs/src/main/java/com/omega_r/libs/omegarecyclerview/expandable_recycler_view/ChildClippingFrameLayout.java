package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.List;

public class ChildClippingFrameLayout extends FrameLayout {

    @Nullable
    private Path mClipPath;

    private float mClipHeightAbove;
    private float mClipHeightBelow;

    public ChildClippingFrameLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public void setupClipping(@Nullable List<View> includedViewsAbove, @Nullable List<View> includedViewsBelow) {
        mClipPath = new Path();
        mClipHeightAbove = 0;
        mClipHeightBelow = 0;

        if (includedViewsAbove != null) {
            for (View view : includedViewsAbove) {
                mClipHeightAbove += view.getHeight();
            }
        }

        if (includedViewsBelow != null) {
            for (View view : includedViewsBelow) {
                mClipHeightBelow += view.getHeight();
            }
        }

        updateClip();
        setClipEnabled(false);
    }

    public void animateClipAboveDecreasing(float decreaseTarget, long duration, long delay) {
        if (mClipPath == null) return;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mClipHeightAbove, mClipHeightAbove - decreaseTarget)
                .setDuration(duration);
        valueAnimator.setStartDelay(delay);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mClipHeightAbove = (float) animation.getAnimatedValue();
                updateClip();
                requestLayout();
            }
        });
        valueAnimator.start();
    }

    public void invalidateClipping() {
        mClipPath = null;
        mClipHeightAbove = 0;
        mClipHeightBelow = 0;
        setClipEnabled(true);
    }

    protected void updateClip() {
        if (mClipPath == null) return;
        mClipPath.reset();
        mClipPath.addRect(new RectF(0, -mClipHeightAbove, getWidth(), getHeight() + mClipHeightBelow), Path.Direction.CW);
    }

    protected void setClipEnabled(boolean isClipEnabled) {
        ViewParent vp = getChildAt(0).getParent();
        while (vp != null) {
            if (vp instanceof ViewGroup) {
                ((ViewGroup) vp).setClipChildren(isClipEnabled);
                ((ViewGroup) vp).setClipToPadding(isClipEnabled);
            }
            if (vp instanceof RecyclerView) {
                vp = null;
            } else {
                vp = vp.getParent();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mClipPath != null) {
            canvas.clipPath(mClipPath);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            View child = getChildAt(0);
            child.measure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(child.getMeasuredWidth(), child.getMeasuredHeight());
        }

    }
}
