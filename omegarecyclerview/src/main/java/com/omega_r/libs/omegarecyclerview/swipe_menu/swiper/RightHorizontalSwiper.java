package com.omega_r.libs.omegarecyclerview.swipe_menu.swiper;


import android.view.View;
import android.widget.OverScroller;

public class RightHorizontalSwiper extends Swiper {

    public RightHorizontalSwiper(View menuView) {
        super(END_DIRECTION, menuView);
    }

    @Override
    public boolean isMenuOpen(int scrollX) {
        return scrollX >= -getMenuView().getWidth() * getDirection();
    }

    @Override
    public boolean isMenuOpenNotEqual(int scrollX) {
        return scrollX > -getMenuView().getWidth() * getDirection();
    }

    @Override
    public void autoOpenMenu(OverScroller scroller, int scrollX, int duration) {
        scroller.startScroll(Math.abs(scrollX), 0, getMenuView().getWidth()-Math.abs(scrollX), 0, duration);
    }

    @Override
    public void autoCloseMenu(OverScroller scroller, int scrollX, int duration) {
        scroller.startScroll(-Math.abs(scrollX), 0, Math.abs(scrollX), 0, duration);
    }

    @Override
    public Checker checkXY(int x, int y) {
        mChecker.x = x;
        mChecker.y = y;

        mChecker.shouldResetSwiper = mChecker.x == 0;

        if (mChecker.x < 0) mChecker.x = 0;

        if (mChecker.x > getMenuView().getWidth()) mChecker.x = getMenuView().getWidth();

        return mChecker;
    }

    @Override
    public boolean isClickOnContentView(View contentView, float x) {
        return x < (contentView.getWidth() - getMenuView().getWidth());
    }
}
