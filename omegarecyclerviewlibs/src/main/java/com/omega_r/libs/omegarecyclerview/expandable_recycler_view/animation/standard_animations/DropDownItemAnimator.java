package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations;

import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;

public class DropDownItemAnimator extends ExpandableItemAnimator {

    @Override
    protected void onRemoveStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(0f);
    }

    @Override
    protected void setupRemoveAnimation(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation
                .translationY(-holder.itemView.getHeight() - holder.animationHelper.previousChangedHoldersHeight);
    }

    @Override
    protected void onRemoveCancel(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void onRemoveEnd(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(0f);
    }

    @Override
    protected void onAddStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(-holder.itemView.getHeight() - holder.animationHelper.previousChangedHoldersHeight);
    }

    @Override
    protected void setupAddAnimation(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation
                .translationY(0f);
    }

    @Override
    protected void onAddCancel(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void onAddEnd(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setAlpha(1f);
        holder.itemView.setTranslationY(0f);
    }
}
