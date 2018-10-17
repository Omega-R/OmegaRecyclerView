package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

public class AnimationHelper {
    public int totalChanges;
    public int positionInChanges;

    @Nullable
    public RecyclerView.ViewHolder upperViewHolder;

    @Nullable
    public RecyclerView.ViewHolder lowerViewHolder;

    public void clear() {
        totalChanges = 0;
        positionInChanges = 0;
        upperViewHolder = null;
        lowerViewHolder = null;
    }
}
