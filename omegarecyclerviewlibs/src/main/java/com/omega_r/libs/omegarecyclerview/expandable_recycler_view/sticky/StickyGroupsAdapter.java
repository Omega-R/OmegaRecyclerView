package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.sticky;

import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderAdapter;

public abstract class StickyGroupsAdapter<G, CH> extends OmegaExpandableRecyclerView.Adapter<G, CH> implements
        StickyHeaderAdapter<OmegaExpandableRecyclerView.Adapter.GroupViewHolder> {

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getGroup().hashCode();
    }

    @Override
    public GroupViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return provideGroupViewHolder(parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindHeaderViewHolder(OmegaExpandableRecyclerView.Adapter.GroupViewHolder viewHolder, int position) {
        viewHolder.bind(getItem(position).getGroup());
    }
}