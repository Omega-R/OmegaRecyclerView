package com.omega_r.libs.omegarecyclerview.swipe_menu;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

public class SwipeViewHolder extends OmegaRecyclerView.ViewHolder {

    public static final int NO_ID = 0;

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

    public SwipeViewHolder(ViewGroup parent, LayoutInflater inflater, int contentRes, int swipeMenuRes) {
        this(inflateView(parent, inflater, contentRes), inflateView(parent, inflater, swipeMenuRes));
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
        swipeMenuLayout.setClickable(true);
        swipeMenuLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        swipeMenuLayout.setContentView(contentView);
        if (swipeLeftMenuView != null) swipeMenuLayout.setLeftMenu(swipeLeftMenuView);
        if (swipeRightMenuView != null) swipeMenuLayout.setRightMenu(swipeRightMenuView);
    }

    private static View inflateView(ViewGroup parent, LayoutInflater layoutInflater, @LayoutRes int resId) {
        return resId == NO_ID ? null : layoutInflater.inflate(resId, parent, false);
    }

}
