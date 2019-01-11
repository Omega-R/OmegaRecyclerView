package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations;

import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;

public final class FadeItemAnimator extends ExpandableItemAnimator {
    private static final int FADE_DURATION = 100;

    @Override
    protected void onRemoveStart(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void setupRemoveAnimation(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation
                .alpha(0f)
                .setDuration(FADE_DURATION);
    }

    @Override
    protected void onRemoveCancel(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void onRemoveEnd(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.contentView.setAlpha(1f);
    }

    @Override
    protected void onAddStart(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void setupAddAnimation(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation
                .alpha(1f)
                .setDuration(FADE_DURATION);
    }

    @Override
    protected void onAddCancel(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        // nothing
    }

    @Override
    protected void onAddEnd(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.contentView.setAlpha(1f);
    }

    @Override
    protected boolean shouldReverseAddOrder() {
        return false;
    }

    @Override
    protected boolean shouldReverseRemoveOrder() {
        return false;
    }

    @Override
    protected boolean isNeedAddingDelay() {
        return true;
    }
}
