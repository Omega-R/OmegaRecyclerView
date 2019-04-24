package com.omega_r.libs.omegarecyclerview.pagination;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

import java.util.List;

@SuppressWarnings("unchecked")
public class WrapperAdapter<T extends RecyclerView.ViewHolder> extends OmegaRecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected RecyclerView.Adapter mChildAdapter;

    public WrapperAdapter(RecyclerView.Adapter adapter) {
        super.setHasStableIds(adapter.hasStableIds());
        this.mChildAdapter = adapter;
        adapter.registerAdapterDataObserver(new ForwardingDataSetObserver());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mChildAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mChildAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mChildAdapter.getItemCount();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        mChildAdapter.onBindViewHolder(holder, position, payloads);
    }

    public RecyclerView.Adapter getWrappedAdapter() {
        return mChildAdapter;
    }

    public RecyclerView.Adapter getLastWrappedAdapter() {
        RecyclerView.Adapter childAdapter = mChildAdapter;
        while (childAdapter instanceof WrapperAdapter) {
            childAdapter = ((WrapperAdapter) childAdapter).mChildAdapter;
        }
        return childAdapter;
    }

    @Override
    public int getItemViewType(int position) {
        return mChildAdapter.getItemViewType(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        mChildAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return mChildAdapter.getItemId(position);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        mChildAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return mChildAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mChildAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        mChildAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mChildAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mChildAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mChildAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public boolean isDividerAllowedAbove(int position) {
        if (mChildAdapter instanceof OmegaRecyclerView.Adapter) {
            return ((OmegaRecyclerView.Adapter) mChildAdapter).isDividerAllowedAbove(position);
        }
        return super.isDividerAllowedBelow(position);
    }

    @Override
    public boolean isDividerAllowedBelow(int position) {
        if (mChildAdapter instanceof OmegaRecyclerView.Adapter) {
            return ((OmegaRecyclerView.Adapter) mChildAdapter).isDividerAllowedBelow(position);
        }
        return super.isDividerAllowedBelow(position);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mChildAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    private class ForwardingDataSetObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            tryNotifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            tryNotifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            tryNotifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int position, int itemCount) {
            tryNotifyItemRangeRemoved(position, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            tryNotifyDataSetChanged();
        }
    }



}
