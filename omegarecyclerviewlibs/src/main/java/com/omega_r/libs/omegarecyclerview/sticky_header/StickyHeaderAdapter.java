package com.omega_r.libs.omegarecyclerview.sticky_header;

import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

public interface StickyHeaderAdapter<T extends OmegaRecyclerView.ViewHolder> {

    long getHeaderId(int position);

    T onCreateHeaderViewHolder(ViewGroup parent);

    void onBindHeaderViewHolder(T viewHolder, int position);
}
