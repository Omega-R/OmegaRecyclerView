package com.omega_r.libs.omegarecyclerview_fastscroll;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public interface FastScrollAdapter {

    long getFastScrollGroupId(int position);

    interface Text extends FastScrollAdapter {

        String getText(int position);

    }

    interface ViewHolder<T extends RecyclerView.ViewHolder> extends FastScrollAdapter {

        T onCreateFastScrollViewHolder(ViewGroup parent);

        void onBindFastScrollViewHolder(T viewHolder, int position);

    }

}