package com.omega_r.libs.omegarecyclerview.swipe_menu;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.omega_r.libs.omegarecyclerview.R;
import com.omega_r.libs.omegarecyclerview.swipe_menu.listener.SwipeFractionListener;
import com.omega_r.libs.omegarecyclerview.swipe_menu.listener.SwipeSwitchListener;
import com.omega_r.libs.omegarecyclerview.swipe_menu.swiper.Swiper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public abstract class SwipeMenuLayout extends FrameLayout {

    public static final int SCROLLER_DURATION = 250;
    public static final float AUTO_OPEN_PERCENT = 0.5f;

    protected int mScaledTouchSlop;
    protected int mLastX;
    protected int mLastY;
    protected int mDownX;
    protected int mDownY;
    protected int mScaledMinimumFlingVelocity;
    protected int mScaledMaximumFlingVelocity;

    protected View mContentView;

    protected Swiper mBeginSwiper;
    protected Swiper mEndSwiper;
    protected Swiper mCurrentSwiper;

    protected boolean shouldResetSwiper;
    protected boolean mDragging;
    protected boolean swipeEnable = true;

    protected OverScroller mScroller;

    protected VelocityTracker mVelocityTracker;

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

    public void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());

        mScaledTouchSlop = viewConfiguration.getScaledTouchSlop();
        mScroller = new OverScroller(getContext());
        mScaledMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mScaledMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }

    public abstract void smoothOpenMenu(int duration);

    public void smoothOpenMenu() {
        smoothOpenMenu(SCROLLER_DURATION);
    }

    public abstract void smoothCloseMenu(int duration);

    public void smoothCloseMenu() {
        smoothCloseMenu(SCROLLER_DURATION);
    }

    public boolean isSwipeEnable() {
        return swipeEnable;
    }
}
