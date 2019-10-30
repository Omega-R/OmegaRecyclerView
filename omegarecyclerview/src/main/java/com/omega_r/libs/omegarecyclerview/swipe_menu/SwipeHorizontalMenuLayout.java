package com.omega_r.libs.omegarecyclerview.swipe_menu;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewParent;

import com.omega_r.libs.omegarecyclerview.swipe_menu.swiper.LeftHorizontalSwiper;
import com.omega_r.libs.omegarecyclerview.swipe_menu.swiper.RightHorizontalSwiper;
import com.omega_r.libs.omegarecyclerview.swipe_menu.swiper.Swiper;


public class SwipeHorizontalMenuLayout extends SwipeMenuLayout {

    protected int mPreScrollX;
    protected float mPreLeftMenuFraction = -1;
    protected float mPreRightMenuFraction = -1;
    private boolean mDownMenuOpen;
    private boolean isLeftSwipeEnabled = true;
    private boolean isRightSwipeEnabled = true;

    public SwipeHorizontalMenuLayout(Context context) {
        super(context);
    }

    public SwipeHorizontalMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeHorizontalMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercepted = super.onInterceptTouchEvent(ev);
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownMenuOpen = isMenuOpen();
                mDownX = mLastX = (int) ev.getX();
                mDownY = (int) ev.getY();
                isIntercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) (ev.getX() - mDownX);
                int disY = (int) (ev.getY() - mDownY);
                isIntercepted = Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY);
                break;
            case MotionEvent.ACTION_UP:
                isIntercepted = false;
                // menu view opened and click on content view,
                // we just close the menu view and intercept the up event
                if (mCurrentSwiper != null && mDownMenuOpen && isMenuOpen()
                        && mCurrentSwiper.isClickOnContentView(this, ev.getX())) {
                    smoothCloseMenu();
                    isIntercepted = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isIntercepted = false;
                if (!mScroller.isFinished())
                    mScroller.forceFinished(false);
                break;
        }
        return isIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(ev);
        int dx;
        int dy;
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isSwipeEnable()) break;
                float eventX = ev.getX();
                if (eventX < mLastX && !isRightSwipeEnabled && !isNotInPlace()) break;
                if (eventX > mLastX && !isLeftSwipeEnabled && !isNotInPlace()) break;

                int disX = (int) (mLastX - ev.getX());
                int disY = (int) (mLastY - ev.getY());
                if (!mDragging
                        && Math.abs(disX) > mScaledTouchSlop
                        && Math.abs(disX) > Math.abs(disY)) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mDragging = true;
                }
                if (mDragging) {
                    if (mCurrentSwiper == null || shouldResetSwiper) {
                        if (disX < 0) {
                            if (mBeginSwiper != null)
                                mCurrentSwiper = mBeginSwiper;
                            else
                                mCurrentSwiper = mEndSwiper;
                        } else {
                            if (mEndSwiper != null)
                                mCurrentSwiper = mEndSwiper;
                            else
                                mCurrentSwiper = mBeginSwiper;
                        }
                    }
                    scrollBy(disX, 0);
                    mLastX = (int) eventX;
                    mLastY = (int) ev.getY();
                    shouldResetSwiper = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
                dx = (int) (mDownX - ev.getX());
                dy = (int) (mDownY - ev.getY());
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                int velocity = Math.abs(velocityX);
                if (velocity > mScaledMinimumFlingVelocity) {
                    if (mCurrentSwiper != null) {
                        int duration = getSwipeDuration(ev, velocity);
                        if (mCurrentSwiper instanceof RightHorizontalSwiper) {
                            if (velocityX < 0) { // just open
                                smoothOpenMenu(duration);
                            } else { // just close
                                smoothCloseMenu(duration);
                            }
                        } else {
                            if (velocityX > 0) { // just open
                                smoothOpenMenu(duration);
                            } else { // just close
                                smoothCloseMenu(duration);
                            }
                        }
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                } else {
                    judgeOpenClose(dx, dy);
                }
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                if (Math.abs(dx) > mScaledTouchSlop
                        || Math.abs(dy) > mScaledTouchSlop
                        || isMenuOpen()) { // ignore click listener, cancel this event
                    MotionEvent motionEvent = MotionEvent.obtain(ev);
                    motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onTouchEvent(motionEvent);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(false);
                } else {
                    dx = (int) (mDownX - ev.getX());
                    dy = (int) (mDownY - ev.getY());
                    judgeOpenClose(dx, dy);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void judgeOpenClose(int dx, int dy) {
        if (mCurrentSwiper != null) {
            if (Math.abs(getScrollX()) >= (mCurrentSwiper.getMenuView().getWidth() * mAutoOpenPercent)) { // auto open
                if (Math.abs(dx) > mScaledTouchSlop || Math.abs(dy) > mScaledTouchSlop) { // swipe up
                    if (isMenuOpenNotEqual()) smoothCloseMenu();
                    else smoothOpenMenu();
                } else { // normal up
                    if (isMenuOpen()) smoothCloseMenu();
                    else smoothOpenMenu();
                }
            } else { // auto close
                smoothCloseMenu();
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mCurrentSwiper == null) return;

        Swiper.Checker checker = mCurrentSwiper.checkXY(x, y);
        shouldResetSwiper = checker.shouldResetSwiper;
        if (checker.x != getScrollX()) {
            super.scrollTo(checker.x, checker.y);

            if (mContentView != null && mBeginSwiper != null && mEndSwiper != null) {
                int parentViewWidth = ViewCompat.getMeasuredWidthAndState(this);
                LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
                int tGap = getPaddingTop() + lp.topMargin;
                int menuViewWidth = ViewCompat.getMeasuredWidthAndState(mEndSwiper.getMenuView());
                int menuViewHeight = ViewCompat.getMeasuredHeightAndState(mEndSwiper.getMenuView());
                if (mBeginSwiper.getMenuView() == mEndSwiper.getMenuView()) {
                    if (checker.x >= 0) {
                        mBeginSwiper.getMenuView().layout(parentViewWidth,
                                tGap,
                                parentViewWidth + menuViewWidth,
                                tGap + menuViewHeight);
                    } else {
                        mEndSwiper.getMenuView().layout(-menuViewWidth,
                                tGap,
                                0,
                                tGap + menuViewHeight);
                    }
                }
            }
        }
        if (getScrollX() != mPreScrollX) {
            int absScrollX = Math.abs(getScrollX());
            if (mCurrentSwiper instanceof LeftHorizontalSwiper) {
                SwipeDirection direction = SwipeDirection.LEFT;
                if (mSwipeSwitchListener != null) {
                    if (absScrollX == 0) mSwipeSwitchListener.onSwipeMenuClosed(this, direction);
                    else if (absScrollX == mBeginSwiper.getMenuWidth())
                        mSwipeSwitchListener.onSwipeMenuOpened(this, direction);
                }
                if (mSwipeFractionListener != null) {
                    float fraction = (float) absScrollX / mBeginSwiper.getMenuWidth();
                    fraction = Float.parseFloat(mDecimalFormat.format(fraction));
                    if (fraction != mPreLeftMenuFraction) {
                        mSwipeFractionListener.onSwipeMenuFraction(this, direction, fraction);
                    }
                    mPreLeftMenuFraction = fraction;
                }
            } else {
                SwipeDirection direction = SwipeDirection.RIGHT;

                if (mSwipeSwitchListener != null) {
                    if (absScrollX == 0) mSwipeSwitchListener.onSwipeMenuClosed(this, direction);
                    else if (absScrollX == mEndSwiper.getMenuWidth())
                        mSwipeSwitchListener.onSwipeMenuOpened(this, direction);
                }
                if (mSwipeFractionListener != null) {
                    float fraction = (float) absScrollX / mEndSwiper.getMenuWidth();
                    fraction = Float.parseFloat(mDecimalFormat.format(fraction));
                    if (fraction != mPreRightMenuFraction) {
                        mSwipeFractionListener.onSwipeMenuFraction(this, direction, fraction);
                    }
                    mPreRightMenuFraction = fraction;
                }
            }
        }
        mPreScrollX = getScrollX();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currX = Math.abs(mScroller.getCurrX());
            if (mCurrentSwiper instanceof RightHorizontalSwiper) {
                scrollTo(currX, 0);
                invalidate();
            } else {
                scrollTo(-currX, 0);
                invalidate();
            }
        }
    }

    public void setContentView(View view) {
        if (view.getParent() != this) {
            addView(view);
            mContentView = view;
            mContentView.setClickable(true);
        }
    }

    public void setLeftMenu(@NonNull View view) {
        if (view.getParent() != this) {
            addView(view);
            view.setClickable(true);
        }
        mBeginSwiper = new LeftHorizontalSwiper(view);
    }

    public void setRightMenu(@NonNull View view) {
        if (view.getParent() != this) {
            addView(view);
            view.setClickable(true);
        }
        mEndSwiper = new RightHorizontalSwiper(view);
    }

    public boolean isMenuOpen() {
        return (mBeginSwiper != null && mBeginSwiper.isMenuOpen(getScrollX()))
                || (mEndSwiper != null && mEndSwiper.isMenuOpen(getScrollX()));
    }

    public boolean isMenuOpenNotEqual() {
        return (mBeginSwiper != null && mBeginSwiper.isMenuOpenNotEqual(getScrollX()))
                || (mEndSwiper != null && mEndSwiper.isMenuOpenNotEqual(getScrollX()));
    }

    @Override
    public boolean isNotInPlace() {
        return (mBeginSwiper != null && mBeginSwiper.isNotInPlace(getScrollX()))
                || (mEndSwiper != null && mEndSwiper.isNotInPlace(getScrollX()));
    }

    public void smoothOpenMenu(int duration) {
        if (mCurrentSwiper != null) {
            mCurrentSwiper.autoOpenMenu(mScroller, getScrollX(), duration);
            invalidate();
        }
    }

    public void smoothCloseMenu(int duration) {
        if (mCurrentSwiper != null) {
            mCurrentSwiper.autoCloseMenu(mScroller, getScrollX(), duration);
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mContentView == null) return;

        int parentViewWidth = ViewCompat.getMeasuredWidthAndState(this);
        int contentViewWidth = ViewCompat.getMeasuredWidthAndState(mContentView);
        int contentViewHeight = ViewCompat.getMeasuredHeightAndState(mContentView);
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        int lGap = getPaddingLeft() + lp.leftMargin;
        int tGap = getPaddingTop() + lp.topMargin;

        mContentView.layout(lGap,
                tGap,
                lGap + contentViewWidth,
                tGap + contentViewHeight);

        if (mEndSwiper != null) {
            int menuViewWidth = ViewCompat.getMeasuredWidthAndState(mEndSwiper.getMenuView());
            int menuViewHeight = ViewCompat.getMeasuredHeightAndState(mEndSwiper.getMenuView());
            lp = (LayoutParams) mEndSwiper.getMenuView().getLayoutParams();
            tGap = getPaddingTop() + lp.topMargin;
            mEndSwiper.getMenuView().layout(parentViewWidth,
                    tGap,
                    parentViewWidth + menuViewWidth,
                    tGap + menuViewHeight);
        }

        if (mBeginSwiper != null) {
            int menuViewWidth = ViewCompat.getMeasuredWidthAndState(mBeginSwiper.getMenuView());
            int menuViewHeight = ViewCompat.getMeasuredHeightAndState(mBeginSwiper.getMenuView());
            lp = (LayoutParams) mBeginSwiper.getMenuView().getLayoutParams();
            tGap = getPaddingTop() + lp.topMargin;
            mBeginSwiper.getMenuView().layout(-menuViewWidth,
                    tGap,
                    0,
                    tGap + menuViewHeight);
        }
    }

    protected int getLen() {
        return mCurrentSwiper == null ? 0 : mCurrentSwiper.getMenuWidth();
    }

    protected int getMoveLen(MotionEvent ev) {
        int sx = getScrollX();
        return (int) (ev.getX() - sx);
    }

    @Override
    public boolean isSwipeEnable(SwipeDirection direction) {
        switch (direction) {
            case LEFT:
                return isLeftSwipeEnabled;
            case RIGHT:
                return isRightSwipeEnabled;
            default:
                return false;

        }
    }

    @Override
    public void setSwipeEnable(SwipeDirection direction, boolean swipeEnable) {
        switch (direction) {
            case LEFT:
                isLeftSwipeEnabled = swipeEnable;
                break;
            case RIGHT:
                isRightSwipeEnabled = swipeEnable;
                break;
        }
    }

}