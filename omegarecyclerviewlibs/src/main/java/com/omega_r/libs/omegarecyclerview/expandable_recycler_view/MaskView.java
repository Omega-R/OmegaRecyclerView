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

public class MaskView extends FrameLayout {

    @Nullable
    private Path mMaskPath;

    private float mMaskHeightAbove;
    private float mMaskHeightBelow;

    public MaskView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public void setupMask(@Nullable List<View> includedViewsAbove, @Nullable List<View> includedViewsBelow) {
        mMaskPath = new Path();
        mMaskHeightAbove = 0;
        mMaskHeightBelow = 0;

        if (includedViewsAbove != null) {
            for (View view : includedViewsAbove) {
                mMaskHeightAbove += view.getHeight();
            }
        }

        if (includedViewsBelow != null) {
            for (View view : includedViewsBelow) {
                mMaskHeightBelow += view.getHeight();
            }
        }

        updateMask();
        setClipping(false);
    }

    public void animateMaskAboveDecreasing(float decreaseTarget, long duration, long delay) {
        if (mMaskPath == null) return;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mMaskHeightAbove, mMaskHeightAbove - decreaseTarget)
                .setDuration(duration);
        valueAnimator.setStartDelay(delay);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMaskHeightAbove = (float) animation.getAnimatedValue();
                updateMask();
                requestLayout();
            }
        });
        valueAnimator.start();
    }

    public void invalidateMask() {
        mMaskPath = null;
        mMaskHeightAbove = 0;
        mMaskHeightBelow = 0;
        setClipping(true);
    }

    protected void updateMask() {
        if (mMaskPath == null) return;
        mMaskPath.reset();
        mMaskPath.addRect(new RectF(0, -mMaskHeightAbove, getWidth(), getHeight() + mMaskHeightBelow), Path.Direction.CW);
    }

    protected void setClipping(boolean isClipEnabled) {
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
        if (mMaskPath != null) {
            canvas.clipPath(mMaskPath);
        }
        super.onDraw(canvas);
    }
}
