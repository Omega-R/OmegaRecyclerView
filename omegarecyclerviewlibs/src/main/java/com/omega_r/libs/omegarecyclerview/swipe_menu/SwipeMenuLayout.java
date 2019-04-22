package com.omega_r.libs.omegarecyclerview.swipe_menu;

import android.content.Context;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.omega_r.libs.omegarecyclerview.swipe_menu.listener.SwipeFractionListener;
import com.omega_r.libs.omegarecyclerview.swipe_menu.listener.SwipeSwitchListener;
import com.omega_r.libs.omegarecyclerview.swipe_menu.swiper.Swiper;

public abstract class SwipeMenuLayout extends FrameLayout {

    public static final int DEFAULT_SCROLLER_DURATION = 250;
    public static final float DEFAULT_AUTO_OPEN_PERCENT = 0.5f;
    protected float mAutoOpenPercent = DEFAULT_AUTO_OPEN_PERCENT;
    protected int mScrollerDuration = DEFAULT_SCROLLER_DURATION;

    protected int mScaledTouchSlop;
    protected int mLastX;
    protected int mLastY;
    protected int mDownX;
    protected int mDownY;
    @Nullable
    protected View mContentView;
    @Nullable
    protected Swiper mBeginSwiper;
    @Nullable
    protected Swiper mEndSwiper;
    @Nullable
    protected Swiper mCurrentSwiper;
    protected boolean shouldResetSwiper;
    protected boolean mDragging;
    protected boolean swipeEnable = true;
    protected OverScroller mScroller;
    protected Interpolator mInterpolator;
    protected VelocityTracker mVelocityTracker;
    protected int mScaledMinimumFlingVelocity;
    protected int mScaledMaximumFlingVelocity;
    protected SwipeSwitchListener mSwipeSwitchListener;
    protected SwipeFractionListener mSwipeFractionListener;
    protected NumberFormat mDecimalFormat = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.US));

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Not in the place, the swipe menu is swiping
     * @return int the place or not
     */
    public abstract boolean isNotInPlace();

    public void init() {
        ViewConfiguration mViewConfig = ViewConfiguration.get(getContext());
        mScaledTouchSlop = mViewConfig.getScaledTouchSlop();
        mScroller = new OverScroller(getContext(), mInterpolator);
        mScaledMinimumFlingVelocity = mViewConfig.getScaledMinimumFlingVelocity();
        mScaledMaximumFlingVelocity = mViewConfig.getScaledMaximumFlingVelocity();
    }

    public void smoothOpenMenu(SwipeDirection direction) {
        switch (direction) {
            case LEFT:
                mCurrentSwiper = mBeginSwiper;
                break;
            case RIGHT:
                mCurrentSwiper = mEndSwiper;
                break;
        }
        if (mCurrentSwiper == null) throw new IllegalArgumentException("No menu!");

        smoothOpenMenu();
    }

    public void smoothCloseBeginMenu(SwipeDirection direction) {
        switch (direction) {
            case LEFT:
                mCurrentSwiper = mBeginSwiper;
                break;
            case RIGHT:
                mCurrentSwiper = mEndSwiper;
                break;
        }
        if (mCurrentSwiper == null) throw new IllegalArgumentException("No menu!");
        smoothCloseMenu();
    }

    public abstract void smoothOpenMenu(int duration);

    public void smoothOpenMenu() {
        smoothOpenMenu(mScrollerDuration);
    }

    public abstract void smoothCloseMenu(int duration);

    public void smoothCloseMenu() {
        smoothCloseMenu(mScrollerDuration);
    }

    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    public abstract void setSwipeEnable(SwipeDirection direction, boolean swipeEnable);

    public abstract boolean isSwipeEnable(SwipeDirection direction);

    public void setSwipeListener(SwipeSwitchListener swipeSwitchListener) {
        mSwipeSwitchListener = swipeSwitchListener;
    }

    public void setSwipeFractionListener(SwipeFractionListener swipeFractionListener) {
        mSwipeFractionListener = swipeFractionListener;
    }

    abstract int getMoveLen(MotionEvent event);

    abstract int getLen();

    /**
     * compute finish duration
     *
     * @param ev       up event
     * @param velocity velocity
     * @return finish duration
     */
    int getSwipeDuration(MotionEvent ev, int velocity) {
        int moveLen = getMoveLen(ev);
        final int len = getLen();
        final int halfLen = len / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(moveLen) / len);
        final float distance = halfLen + halfLen *
                distanceInfluenceForSnapDuration(distanceRatio);
        int duration;
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(moveLen) / len;
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, mScrollerDuration);
        return duration;
    }

    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isNotInPlace()) {
            smoothCloseMenu(0);
        }
    }
}
