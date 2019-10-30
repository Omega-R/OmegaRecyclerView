package com.omega_r.libs.omegarecyclerview.sticky_decoration;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

public interface StickyAdapter<T extends RecyclerView.ViewHolder> {

    long getStickyId(int position);

    T onCreateStickyViewHolder(ViewGroup parent);

    void onBindStickyViewHolder(T viewHolder, int position);


    interface Mode {

        int HEADER = 0;
        int MIDDLE = 1;

    }

}