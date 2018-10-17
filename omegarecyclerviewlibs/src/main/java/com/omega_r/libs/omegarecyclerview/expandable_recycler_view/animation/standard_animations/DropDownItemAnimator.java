package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations;

import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.MaskView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;

public class DropDownItemAnimator extends ExpandableItemAnimator {

    protected static final long COLLAPSE_DELAY = 110L;
    protected static final long COLLAPSE_DURATION_LONG = 320L;
    protected static final long EXPAND_DURATION_LONG = 350L;
    protected static final long COLLAPSE_DURATION_SHORT = 300L;
    protected static final long EXPAND_DURATION_SHORT = 160L;

    @Override
    protected void onRemoveStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        ((MaskView) holder.itemView).setupMask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.contentView.setTranslationZ(-1f);
        }
        holder.contentView.setTranslationY(0f);
    }

    @Override
    protected void setupRemoveAnimation(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animator = holder.contentView.animate(); // tricky

        animator.setStartDelay(COLLAPSE_DELAY);
        animator.setDuration(holder.animationHelper.havePendingAdding ? COLLAPSE_DURATION_LONG : COLLAPSE_DURATION_SHORT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (holder.animationHelper.lowerViewHolder == null) {
                animator.translationY(getHiddenOffset(holder));
            } else {
                animator.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder.animationHelper.lowerViewHolder;
                        if (cvh != null) {
                            holder.contentView.setTranslationY(cvh.contentView.getTranslationY());
                        }
                    }
                });
            }
        } else {
            animator.translationY(getHiddenOffset(holder));
        }

    }

    @Override
    protected void onRemoveCancel(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        resetAnimator(animator);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.contentView.setTranslationZ(-2f);
        }
        ((MaskView) holder.itemView).invalidateMask();
    }

    @Override
    protected void onRemoveEnd(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.contentView.setTranslationY(0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.contentView.setTranslationZ(-2f);
        }
        resetAnimator(animator);
        ((MaskView) holder.itemView).invalidateMask();
    }

    @Override
    protected void onAddStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        ((MaskView) holder.itemView).setupMask();
        holder.contentView.setTranslationY(getHiddenOffsetReversed(holder));
    }

    @Override
    protected void setupAddAnimation(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animator.setDuration(holder.animationHelper.havePendingRemovals ? EXPAND_DURATION_LONG : EXPAND_DURATION_SHORT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (holder.animationHelper.upperViewHolder == null) {
                animator.translationY(0f);
            } else {
                animator.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        holder.contentView.setTranslationY(
                                ((OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder.animationHelper.upperViewHolder)
                                        .contentView.getTranslationY());
                    }
                });
            }
        } else {
            animator.translationY(0f);
        }
    }

    @Override
    protected void onAddCancel(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        resetAnimator(animator);
        ((MaskView) holder.itemView).invalidateMask();
    }

    @Override
    protected void onAddEnd(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.contentView.setAlpha(1f);
        holder.contentView.setTranslationY(0f);
        ((MaskView) holder.itemView).invalidateMask();
        resetAnimator(animator);
    }

    private void resetAnimator(ViewPropertyAnimator animator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animator.setUpdateListener(null);
        }
        animator.setInterpolator(new ValueAnimator().getInterpolator()); // Default interpolator
        animator.setStartDelay(0);
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
        return false;
    }

    private int getHiddenOffset(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        int offset = -holder.contentView.getHeight();
        RecyclerView.ViewHolder vh = holder.animationHelper.upperViewHolder;
        while (vh != null) {
            if (vh instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) vh;
                offset -= cvh.contentView.getHeight();
                vh = cvh.animationHelper.upperViewHolder;
            } else {
                vh = null;
            }
        }
        return offset;
    }

    private int getHiddenOffsetReversed(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        int offset = -holder.contentView.getHeight();
        RecyclerView.ViewHolder vh = holder.animationHelper.lowerViewHolder;
        while (vh != null) {
            if (vh instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) vh;
                offset -= cvh.contentView.getHeight();
                vh = cvh.animationHelper.lowerViewHolder;
            } else {
                vh = null;
            }
        }
        return offset;
    }
}
