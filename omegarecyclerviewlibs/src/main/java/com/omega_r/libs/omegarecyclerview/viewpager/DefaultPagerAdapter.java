package com.omega_r.libs.omegarecyclerview.viewpager;

import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

public class DefaultPagerAdapter extends OmegaRecyclerView.Adapter<OmegaRecyclerView.ViewHolder> {

    @Override
    public OmegaRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(OmegaRecyclerView.ViewHolder holder, int position) {
        // nothing
    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
