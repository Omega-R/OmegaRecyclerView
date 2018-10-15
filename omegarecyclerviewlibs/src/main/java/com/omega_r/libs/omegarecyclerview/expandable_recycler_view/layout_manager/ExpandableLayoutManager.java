package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.layout_manager;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class ExpandableLayoutManager extends RecyclerView.LayoutManager {

    public static final int INVALID_POSITION = -1;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}
