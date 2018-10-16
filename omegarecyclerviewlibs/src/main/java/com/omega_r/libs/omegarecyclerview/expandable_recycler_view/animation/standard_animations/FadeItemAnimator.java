package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations;

import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;

public class FadeItemAnimator extends ExpandableItemAnimator {

    @Override
    protected void onRemoveStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void setupRemoveAnimation(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation
                .alpha(0f);
    }

    @Override
    protected void onRemoveCancel(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void onRemoveEnd(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setAlpha(1f);
    }

    @Override
    protected void onAddStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void setupAddAnimation(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation
                .alpha(1f);
    }

    @Override
    protected void onAddCancel(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void onAddEnd(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setAlpha(1f);
    }
}
