package com.omega_r.libs.omegarecyclerview;

import androidx.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mac on 10.05.17.
 */

public abstract class ClickViewHolder extends OmegaRecyclerView.ViewHolder implements View.OnClickListener {

    public ClickViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(parent, res);
        initClickViewHolder();
    }

    public ClickViewHolder(ViewGroup parent, LayoutInflater layoutInflater, @LayoutRes int res) {
        super(parent, layoutInflater, res);
        initClickViewHolder();
    }

    public ClickViewHolder(View itemView) {
        super(itemView);
        initClickViewHolder();
    }

    private void initClickViewHolder() {
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int adapterPosition = getAdapterPosition();
        if (adapterPosition >= 0) {
            onClick(v, adapterPosition);
        }
    }

    protected abstract void onClick(View view, int position);

}