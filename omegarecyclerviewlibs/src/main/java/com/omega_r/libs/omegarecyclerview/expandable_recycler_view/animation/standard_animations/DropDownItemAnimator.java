package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.ChildClippingFrameLayout;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.layout_manager.ExpandableLayoutManager;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class DropDownItemAnimator extends ExpandableItemAnimator {
    private static final String TAG = DropDownItemAnimator.class.getName();

    private static final long COLLAPSE_DELAY = 110L;
    private static final long COLLAPSE_DURATION_LONG = 320L;
    private static final long EXPAND_DURATION_LONG = 350L;
    private static final long COLLAPSE_DURATION_SHORT = 300L;
    private static final long EXPAND_DURATION_SHORT = 160L;

    public DropDownItemAnimator() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw new RuntimeException("DropDownItemAnimator supported only since Lollipop");
        }
    }

    @Override
    protected void onRemoveStart(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.contentView.setTranslationY(0f);
        resolveRemovingZ(holder);
        setupMask(holder);
    }

    @Override
    protected void setupRemoveAnimation(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animator.setStartDelay(COLLAPSE_DELAY);
        long duration = holder.animationHelper.havePendingAdditions() ? COLLAPSE_DURATION_LONG : COLLAPSE_DURATION_SHORT;
        animator.setDuration(duration);

        if (holder.animationHelper.lowerViewHolder == null) {
            animator.translationY(getHiddenOffset(holder));
        } else {
            animator.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    holder.contentView.setTranslationY(holder.animationHelper.lowerViewHolder.contentView.getTranslationY());
                }
            });
        }

        if (holder.animationHelper.havePendingAdditions()) {
            float deltaHeight = 0f;
            for (RecyclerView.ViewHolder viewHolder : holder.animationHelper.getPendingChanges().additions) {
                if (viewHolder.getAdapterPosition() < holder.animationHelper.visibleAdapterPosition) {
                    deltaHeight += viewHolder.itemView.getHeight();
                }
            }
            if (deltaHeight > 0) {
                ((ChildClippingFrameLayout) holder.itemView).animateClipAboveDecreasing(deltaHeight, duration, COLLAPSE_DELAY);
            }
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
        holder.itemView.setAlpha(1f);
        holder.contentView.setTranslationY(getHiddenOffsetReversed(holder));
        resolveAddingZ(holder);
        setupMask(holder);
    }

    @Override
    protected void setupAddAnimation(ViewPropertyAnimator animator, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animator.setDuration(holder.animationHelper.havePendingRemovals() ? EXPAND_DURATION_LONG : EXPAND_DURATION_SHORT);
        animator.alpha(1f);
        if (holder.animationHelper.upperViewHolder == null) {
            animator.translationY(0f);
        } else {
            animator.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    holder.contentView.setTranslationY(holder.animationHelper.upperViewHolder.contentView.getTranslationY());
                }
            });
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
        animator.setUpdateListener(null);
        animator.setStartDelay(0);

        holder.itemView.setTranslationZ(ExpandableLayoutManager.DEFAULT_CHILD_Z);
        holder.contentView.setTranslationY(0f);

        ((ChildClippingFrameLayout) holder.itemView).invalidateClipping();
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
        OmegaExpandableRecyclerView.Adapter.ChildViewHolder vh = holder.animationHelper.upperViewHolder;
        while (vh != null) {
            offset -= vh.contentView.getHeight();
            vh = vh.animationHelper.upperViewHolder;
        }
        return offset;
    }

    private int getHiddenOffsetReversed(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        int offset = -holder.contentView.getHeight();
        OmegaExpandableRecyclerView.Adapter.ChildViewHolder vh = holder.animationHelper.lowerViewHolder;
        while (vh != null) {
            offset -= vh.contentView.getHeight();
            vh = vh.animationHelper.lowerViewHolder;
        }
        return offset;
    }

    private void resolveAddingZ(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        for (RecyclerView.ViewHolder removal : holder.animationHelper.getPendingChanges().removals) {
            if (removal instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) removal;
                if (cvh.animationHelper.visibleAdapterPosition < holder.getAdapterPosition()) {
                    cvh.itemView.setTranslationZ(holder.itemView.getTranslationZ() + 1);
                }
            }
        }
    }

    private void resolveRemovingZ(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        for (RecyclerView.ViewHolder addition : holder.animationHelper.getPendingChanges().additions) {
            if (addition instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) addition;
                if (cvh.getAdapterPosition() < holder.animationHelper.visibleAdapterPosition) {
                    cvh.itemView.setTranslationZ(holder.itemView.getTranslationZ() + 1);
                }
            }
        }
    }

    private void setupMask(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        List<View> viewsAbove = new ArrayList<>();
        List<View> viewsBelow = new ArrayList<>();

        OmegaExpandableRecyclerView.Adapter.ChildViewHolder tmpCvh = holder.animationHelper.upperViewHolder;
        while (tmpCvh != null) {
            viewsAbove.add(tmpCvh.contentView);
            tmpCvh = tmpCvh.animationHelper.upperViewHolder;
        }
        tmpCvh = holder.animationHelper.lowerViewHolder;
        while (tmpCvh != null) {
            viewsBelow.add(tmpCvh.contentView);
            tmpCvh = tmpCvh.animationHelper.lowerViewHolder;
        }

        ((ChildClippingFrameLayout) holder.itemView).setupClipping(viewsAbove, viewsBelow);
    }
}
