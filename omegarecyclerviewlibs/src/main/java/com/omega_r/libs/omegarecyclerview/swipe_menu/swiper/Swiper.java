package com.omega_r.libs.omegarecyclerview.swipe_menu.swiper;


import android.view.View;
import android.widget.OverScroller;

public abstract class Swiper {

    static final int BEGIN_DIRECTION = 1;
    static final int END_DIRECTION = -1;

    private int mDirection;
    private View mMenuView;
    Checker mChecker;

    Swiper(int direction, View menuView) {
        mDirection = direction;
        mMenuView = menuView;
        mChecker = new Checker();
    }

    int getDirection() {
        return mDirection;
    }

    public View getMenuView() {
        return mMenuView;
    }

    public int getMenuWidth(){
        return getMenuView().getWidth();
    }

    public abstract boolean isMenuOpen(final int scrollDis);

    public abstract boolean isMenuOpenNotEqual(final int scrollDis);

    public abstract void autoOpenMenu(OverScroller scroller, int scrollDis, int duration);

    public abstract void autoCloseMenu(OverScroller scroller, int scrollDis, int duration);

    public abstract Checker checkXY(int x, int y);

    public abstract boolean isClickOnContentView(View contentView, float clickPoint);

    public static final class Checker {

        public int x;

        public int y;

        public boolean shouldResetSwiper;

    }
}
