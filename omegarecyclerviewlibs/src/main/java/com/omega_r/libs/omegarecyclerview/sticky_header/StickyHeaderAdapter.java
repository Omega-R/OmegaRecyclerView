package com.omega_r.libs.omegarecyclerview.sticky_header;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface StickyHeaderAdapter<T extends RecyclerView.ViewHolder> {

    long getHeaderId(int position);

    T onCreateHeaderViewHolder(ViewGroup parent);

    void onBindHeaderViewHolder(T viewHolder, int position);
}
