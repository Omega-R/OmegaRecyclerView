package com.omega_r.libs.omegarecyclerview.swipe_menu;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.swipe_menu.listener.SwipeFractionListener;
import com.omega_r.libs.omegarecyclerview.swipe_menu.listener.SwipeSwitchListener;

public class SwipeViewHolder extends OmegaRecyclerView.ViewHolder {

    public static final int NO_ID = 0;

    public final View contentView;

    private SwipeHorizontalMenuLayout mSwipeMenuLayout;

    public SwipeViewHolder(ViewGroup parent, @LayoutRes int contentRes,
                           @LayoutRes int swipeLeftMenuRes, @LayoutRes int swipeRightMenuRes) {

        this(parent, LayoutInflater.from(parent.getContext()),
                contentRes, swipeLeftMenuRes, swipeRightMenuRes);
    }

    public SwipeViewHolder(ViewGroup parent, LayoutInflater layoutInflater, @LayoutRes int contentRes,
                           @LayoutRes int swipeLeftMenuRes, @LayoutRes int swipeRightMenuRes) {

        this(inflateView(parent, layoutInflater, contentRes),
                inflateView(parent, layoutInflater, swipeLeftMenuRes),
                inflateView(parent, layoutInflater, swipeRightMenuRes));
    }

    public SwipeViewHolder(ViewGroup parent, @LayoutRes int contentRes, @LayoutRes int swipeMenuRes) {
        this(parent, LayoutInflater.from(parent.getContext()), contentRes, swipeMenuRes);
    }

    public SwipeViewHolder(ViewGroup parent, int contentRes) {
        this(inflateView(parent, LayoutInflater.from(parent.getContext()), contentRes));
    }

    public SwipeViewHolder(ViewGroup parent, LayoutInflater inflater, int contentRes, int swipeMenuRes) {
        this(inflateView(parent, inflater, contentRes), inflateView(parent, inflater, swipeMenuRes));
    }

    public SwipeViewHolder(ViewGroup parent, LayoutInflater inflater, int contentRes) {
        this(inflateView(parent, inflater, contentRes));
    }

    public SwipeViewHolder(View contentView) {
        this(contentView, null);
        setSwipeEnable(false);
    }

    public SwipeViewHolder(View contentView, @Nullable View swipeMenuView) {
        this(new SwipeHorizontalMenuLayout(contentView.getContext()), contentView, swipeMenuView, swipeMenuView);
    }

    public SwipeViewHolder(View contentView, @Nullable View swipeLeftMenuView, @Nullable View swipeRightMenuView) {
        this(new SwipeHorizontalMenuLayout(contentView.getContext()), contentView, swipeLeftMenuView, swipeRightMenuView);
    }

    private SwipeViewHolder(SwipeHorizontalMenuLayout swipeMenuLayout,
                            View contentView, @Nullable View swipeLeftMenuView, @Nullable View swipeRightMenuView) {
        super(swipeMenuLayout);
        this.contentView = contentView;
        mSwipeMenuLayout = swipeMenuLayout;
        mSwipeMenuLayout.setClickable(true);
        mSwipeMenuLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mSwipeMenuLayout.setContentView(contentView);
        if (swipeLeftMenuView != null) mSwipeMenuLayout.setLeftMenu(swipeLeftMenuView);
        if (swipeRightMenuView != null) mSwipeMenuLayout.setRightMenu(swipeRightMenuView);
    }

    private static View inflateView(ViewGroup parent, LayoutInflater layoutInflater, @LayoutRes int resId) {
        return resId == NO_ID ? null : layoutInflater.inflate(resId, parent, false);
    }

    public void setSwipeFractionListener(@Nullable SwipeFractionListener listener) {
        mSwipeMenuLayout.setSwipeFractionListener(listener);
    }

    public void setSwipeListener(@Nullable SwipeSwitchListener listener) {
        mSwipeMenuLayout.setSwipeListener(listener);
    }

    public void smoothCloseMenu(int duration) {
        mSwipeMenuLayout.smoothCloseMenu(duration);
    }

    public void smoothCloseMenu() {
        mSwipeMenuLayout.smoothCloseMenu();
    }

    public void smoothOpenMenu(SwipeDirection direction) {
        mSwipeMenuLayout.smoothOpenMenu(direction);
    }

    public void smoothOpenLeftMenu() {
        mSwipeMenuLayout.smoothOpenMenu(SwipeDirection.LEFT);
    }

    public void smoothOpenRightMenu() {
        mSwipeMenuLayout.smoothOpenMenu(SwipeDirection.RIGHT);
    }

    public void setSwipeEnable(boolean enable) {
        mSwipeMenuLayout.setSwipeEnable(enable);
    }

    public void setLeftSwipeEnable(boolean enable) {
        mSwipeMenuLayout.setSwipeEnable(SwipeDirection.LEFT, enable);
    }

    public void setRightSwipeEnable(boolean enable) {
        mSwipeMenuLayout.setSwipeEnable(SwipeDirection.RIGHT, enable);
    }

    public boolean isSwipeEnable() {
        return mSwipeMenuLayout.isSwipeEnable();
    }

    public boolean isLeftSwipeEnable() {
        return mSwipeMenuLayout.isSwipeEnable(SwipeDirection.LEFT);
    }

    public boolean isRightSwipeEnable() {
        return mSwipeMenuLayout.isSwipeEnable(SwipeDirection.RIGHT);
    }


}
