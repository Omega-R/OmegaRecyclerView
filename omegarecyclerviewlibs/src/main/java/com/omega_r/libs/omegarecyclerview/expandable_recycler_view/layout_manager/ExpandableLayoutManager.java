package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.layout_manager;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.Range;

import androidx.recyclerview.widget.ExpandedRecyclerView;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandableLayoutManager extends LinearLayoutManager {

    public static final float DEFAULT_CHILD_Z = -50f;

    private Range mAddedRange = Range.empty();

    @Nullable
    private ExpandedRecyclerView mRecyclerView;

    public ExpandableLayoutManager(Context context) {
        this(context, 1, false);
    }

    public ExpandableLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ExpandableLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRecyclerView = (ExpandedRecyclerView) view;
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        mRecyclerView = null;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (mRecyclerView != null) {
            if (mRecyclerView.getLayoutStep() == ExpandedRecyclerView.STEP_LAYOUT) mAddedRange.clear();
        }
    }

    @Override
    public void addView(View child) {
        child.setAlpha(1f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            child.setTranslationZ(0f);
        }

        ExpandedRecyclerView.ViewHolder holder = ExpandedRecyclerView.getChildViewHolderInt(child);
        if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                child.setTranslationZ(DEFAULT_CHILD_Z);
            }
            int adapterPosition = holder.getAdapterPosition();
            if (mAddedRange.contains(adapterPosition)) {
                ((OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder).contentView.setAlpha(0f);
            }
        }
        super.addView(child);
    }

    public void setAddedRange(Range positions) {
        mAddedRange = positions;
    }
}
