package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations;

import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;

public class DropDownItemAnimator extends ExpandableItemAnimator {

    @Override
    protected void onRemoveStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(0f);
    }

    @Override
    protected void setupRemoveAnimation(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation.setStartDelay(100);

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (holder.animationHelper.lowerViewHolder == null) {
                animation.translationY(getHiddenOffset(holder));
            } else {
                animation.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        holder.itemView.setTranslationY(holder.animationHelper.lowerViewHolder.itemView.getTranslationY());
                    }
                });
            }
        } else {
            animation.translationY(getHiddenOffset(holder));
        }

    }

    @Override
    protected void onRemoveCancel(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation.setStartDelay(0);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            animation.setUpdateListener(null);
        }
    }

    @Override
    protected void onRemoveEnd(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        animation.setStartDelay(0);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            animation.setUpdateListener(null);
        }
        holder.itemView.setTranslationY(0f);
    }

    @Override
    protected void onAddStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setTranslationY(getHiddenOffsetReversed(holder));
    }

    @Override
    protected void setupAddAnimation(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (holder.animationHelper.upperViewHolder == null) {
                animation.translationY(0f);
            } else {
                animation.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        holder.itemView.setTranslationY(holder.animationHelper.upperViewHolder.itemView.getTranslationY());
                    }
                });
            }
        } else {
            animation.translationY(0f);
        }
    }

    @Override
    protected void onAddCancel(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            animation.setUpdateListener(null);
        }
    }

    @Override
    protected void onAddEnd(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        holder.itemView.setAlpha(1f);
        holder.itemView.setTranslationY(0f);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            animation.setUpdateListener(null);
        }
    }

    @Override
    protected boolean shouldReverseAddOrder() {
        return false;
    }

    @Override
    protected boolean shouldReverseRemoveOrder() {
        return false;
    }

    private int getHiddenOffset(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        int offset = -holder.itemView.getHeight();
        RecyclerView.ViewHolder vh = holder.animationHelper.upperViewHolder;
        while (vh != null) {
            offset -= vh.itemView.getHeight();
            if (vh instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                vh = ((OmegaExpandableRecyclerView.Adapter.ChildViewHolder) vh).animationHelper.upperViewHolder;
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
            offset -= vh.itemView.getHeight();
            if (vh instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                vh = ((OmegaExpandableRecyclerView.Adapter.ChildViewHolder) vh).animationHelper.lowerViewHolder;
            } else {
                vh = null;
            }
        }
        return offset;
    }
}
