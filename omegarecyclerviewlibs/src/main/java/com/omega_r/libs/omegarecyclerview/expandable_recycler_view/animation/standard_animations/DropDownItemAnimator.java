package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations;

import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.layout_manager.ExpandableLayoutManager;

public final class DropDownItemAnimator extends ExpandableItemAnimator {

    private static final long COLLAPSE_DELAY = 110L;
    private static final long COLLAPSE_DURATION_LONG = 320L;
    private static final long EXPAND_DURATION_LONG = 350L;
    private static final long COLLAPSE_DURATION_SHORT = 300L;
    private static final long EXPAND_DURATION_SHORT = 160L;

    @Override
    protected void onRemoveStart(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(0f);
        resolveRemovingZ(holder);
    }

    @Override
    protected void setupRemoveAnimation(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animator.setStartDelay(COLLAPSE_DELAY);
        animator.setDuration(holder.animationHelper.havePendingAdditions() ? COLLAPSE_DURATION_LONG : COLLAPSE_DURATION_SHORT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (holder.animationHelper.lowerViewHolder == null) {
                animator.translationY(getHiddenOffset(holder));
            } else {
                animator.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        holder.itemView.setTranslationY(holder.animationHelper.lowerViewHolder.itemView.getTranslationY());
                    }
                });
            }
        } else {
            animator.translationY(getHiddenOffset(holder));
        }

    }

    @Override
    protected void onRemoveCancel(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        reset(animator, holder);
    }

    @Override
    protected void onRemoveEnd(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        reset(animator, holder);
    }

    @Override
    protected void onAddStart(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(getHiddenOffsetReversed(holder));
        resolveAddingZ(holder);
    }

    @Override
    protected void setupAddAnimation(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animator.setDuration(holder.animationHelper.havePendingRemovals() ? EXPAND_DURATION_LONG : EXPAND_DURATION_SHORT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (holder.animationHelper.upperViewHolder == null) {
                animator.translationY(0f);
            } else {
                animator.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        holder.itemView.setTranslationY(holder.animationHelper.upperViewHolder.itemView.getTranslationY());
                    }
                });
            }
        } else {
            animator.translationY(0f);
        }
    }

    @Override
    protected void onAddCancel(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        reset(animator, holder);
    }

    @Override
    protected void onAddEnd(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        reset(animator, holder);
    }

    private void reset(ViewPropertyAnimator animator, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animator.setUpdateListener(null);
        }
        animator.setStartDelay(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.setTranslationZ(ExpandableLayoutManager.DEFAULT_CHILD_Z);
        }
        holder.itemView.setAlpha(1f);
        holder.itemView.setTranslationY(0f);
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
        int offset = -holder.itemView.getHeight();
        RecyclerView.ViewHolder vh = holder.animationHelper.upperViewHolder;
        while (vh != null) {
            if (vh instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) vh;
                offset -= cvh.itemView.getHeight();
                vh = cvh.animationHelper.upperViewHolder;
            } else {
                vh = null;
            }
        }
        return offset;
    }

    private int getHiddenOffsetReversed(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        int offset = -holder.itemView.getHeight();
        RecyclerView.ViewHolder vh = holder.animationHelper.lowerViewHolder;
        while (vh != null) {
            if (vh instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) vh;
                offset -= cvh.itemView.getHeight();
                vh = cvh.animationHelper.lowerViewHolder;
            } else {
                vh = null;
            }
        }
        return offset;
    }

    private void resolveAddingZ(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (RecyclerView.ViewHolder removal : holder.animationHelper.getPendingChanges().removals) {
                if (removal instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                    OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) removal;
                    if (cvh.animationHelper.visibleAdapterPosition < holder.getAdapterPosition()) {
                        removal.itemView.setTranslationZ(holder.itemView.getTranslationZ() + 1);
                    }
                }
            }
        }
    }

    private void resolveRemovingZ(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (RecyclerView.ViewHolder addition : holder.animationHelper.getPendingChanges().additions) {
                if (addition instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                    OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) addition;
                    if (cvh.getAdapterPosition() < holder.animationHelper.visibleAdapterPosition) {
                        addition.itemView.setTranslationZ(holder.itemView.getTranslationZ() + 1);
                    }
                }
            }
        }
    }
}
