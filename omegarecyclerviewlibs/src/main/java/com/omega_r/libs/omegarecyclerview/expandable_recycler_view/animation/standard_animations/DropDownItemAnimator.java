package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations;

import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;

public class DropDownItemAnimator extends ExpandableItemAnimator {

    @Override
    protected void onRemoveStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(0);
    }

    @Override
    protected void setupRemoveAnimation(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation
                .setDuration(getRemoveDuration())
                .translationY(-holder.itemView.getHeight());
    }

    @Override
    protected void onRemoveCancel(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(-holder.itemView.getHeight());
    }

    @Override
    protected void onAddStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(-holder.itemView.getHeight());
    }

    @Override
    protected void setupAddAnimation(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation
                .setDuration(getRemoveDuration())
                .translationY(0);
    }

    @Override
    protected void onAddCancel(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(0);
    }
}
