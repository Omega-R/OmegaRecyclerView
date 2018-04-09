package com.omega_r.libs.omegarecyclerview.header;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.pagination.WrapperAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderDecoration;

import java.util.List;

public class HeaderFooterWrapperAdapter<T extends RecyclerView.Adapter> extends OmegaRecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderAdapter {

    // Defines available view type integers for headers and footers.
    private static final int BASE_HEADER_VIEW_TYPE = -1 << 10;
    private static final int BASE_FOOTER_VIEW_TYPE = -1 << 11;

    private final T mRealAdapter;
    private SparseArray<View> mHeaderArray = new SparseArray<>();
    private SparseArray<View> mHeaderArrayCopy = new SparseArray<>();
    private SparseArray<View> mFooterArray = new SparseArray<>();
    private SparseArray<View> mFooterArrayCopy = new SparseArray<>();

    private boolean mHeadersVisibility = true;
    private boolean mFootersVisibility = true;

    public HeaderFooterWrapperAdapter(T adapter) {
        mRealAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeader(viewType)) {
            return new ViewHolder(mHeaderArray.get(viewType));
        }
        if (isFooter(viewType)) {
            return new ViewHolder(mFooterArray.get(viewType));
        }
        return mRealAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(isHeaderPosition(position) || isFooterPosition(position))) {
            mRealAdapter.onBindViewHolder(holder, position - mHeaderArray.size());
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderArray.size() + mRealAdapter.getItemCount() + mFooterArray.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderArray.keyAt(position);
        }
        if (isFooterPosition(position)) {
            return mFooterArray.keyAt(position - mRealAdapter.getItemCount() - mHeaderArray.size());
        }
        return mRealAdapter.getItemViewType(position - mHeaderArray.size());
    }

    public T getWrappedAdapter() {
        return mRealAdapter;
    }

    private boolean isHeader(int viewType) {
        return viewType >= BASE_HEADER_VIEW_TYPE && viewType < (BASE_HEADER_VIEW_TYPE + mHeaderArray.size());
    }

    private boolean isFooter(int viewType) {
        return viewType >= BASE_FOOTER_VIEW_TYPE && viewType < (BASE_FOOTER_VIEW_TYPE + mFooterArray.size());
    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaderArray.size();
    }

    private boolean isFooterPosition(int position) {
        return position >= mRealAdapter.getItemCount() + mHeaderArray.size();
    }

    public void setHeaders(@NonNull List<View> list) {
        mHeaderArray = new SparseArray<>();
        mHeaderArrayCopy = new SparseArray<>();
        for (View view : list) {
            int key = BASE_HEADER_VIEW_TYPE + list.indexOf(view);
            mHeaderArray.append(key, view);
            mHeaderArrayCopy.append(key, view);
        }
        notifyDataSetChanged();
    }

    public void setFooters(@NonNull List<View> list) {
        mFooterArray = new SparseArray<>();
        mFooterArrayCopy = new SparseArray<>();
        for (View view : list) {
            int key = BASE_FOOTER_VIEW_TYPE + list.indexOf(view);
            mFooterArray.append(key, view);
            mFooterArrayCopy.append(key, view);
        }
        notifyDataSetChanged();
    }

    public void setHeadersVisible(boolean visible) {
        if (mHeadersVisibility != visible) {
            if (visible) {
                copyTo(mHeaderArrayCopy, mHeaderArray);
            } else {
                mHeaderArray.clear();
            }
            mHeadersVisibility = visible;
            notifyDataSetChanged();
        }
    }

    public void setFootersVisible(boolean visible) {
        if (mFootersVisibility != visible) {
            if (visible) {
                copyTo(mFooterArrayCopy, mFooterArray);
            } else {
                mFooterArray.clear();
            }
            mFootersVisibility = visible;
            notifyDataSetChanged();
        }
    }

    private <V> void copyTo(SparseArray<V> fromArray, SparseArray<V> toArray) {
        toArray.clear();

        for (int i = 0; i < fromArray.size(); i++) {
            toArray.append(fromArray.keyAt(i), fromArray.valueAt(i));
        }
    }

    @Override
    public long getHeaderId(int position) {
        StickyHeaderAdapter stickyHeaderAdapter = getStickyHeaderAdapter();
        if (stickyHeaderAdapter == null || isHeaderPosition(position) || isFooterPosition(position)) {
            return StickyHeaderDecoration.NO_HEADER_ID;
        }
        return stickyHeaderAdapter.getHeaderId(position - mHeaderArray.size());
    }

    @Nullable
    public StickyHeaderAdapter getStickyHeaderAdapter() {
        if (mRealAdapter instanceof StickyHeaderAdapter) {
            return (StickyHeaderAdapter) mRealAdapter;
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        StickyHeaderAdapter stickyHeaderAdapter = getStickyHeaderAdapter();
        assert stickyHeaderAdapter != null;
        return stickyHeaderAdapter.onCreateHeaderViewHolder(parent);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StickyHeaderAdapter stickyHeaderAdapter = getStickyHeaderAdapter();
        assert stickyHeaderAdapter != null;
        //noinspection unchecked
        stickyHeaderAdapter.onBindHeaderViewHolder(viewHolder, position - mHeaderArray.size());
    }

    @Override
    protected void tryNotifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
        super.tryNotifyItemRangeChanged(positionStart + mHeaderArray.size(), itemCount, payload);
    }

    @Override
    protected void tryNotifyItemRangeInserted(int positionStart, int itemCount) {
        super.tryNotifyItemRangeInserted(positionStart + mHeaderArray.size(), itemCount);
    }

    @Override
    protected void tryNotifyItemRemoved(int positionStart, int itemCount) {
        super.tryNotifyItemRemoved(positionStart + mHeaderArray.size(), itemCount);
    }

    @Override
    protected void tryNotifyItemMoved(int fromPosition, int toPosition) {
        super.tryNotifyItemMoved(fromPosition + mHeaderArray.size(), toPosition + mHeaderArray.size());
    }

    @Override
    protected void tryNotifyItemRangeRemoved(int positionStart, int itemCount) {
        super.tryNotifyItemRangeRemoved(positionStart + mHeaderArray.size(), itemCount);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
