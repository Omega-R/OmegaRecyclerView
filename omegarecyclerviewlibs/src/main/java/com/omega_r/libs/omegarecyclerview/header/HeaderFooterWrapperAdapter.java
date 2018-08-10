package com.omega_r.libs.omegarecyclerview.header;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.R;
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
        super.setHasStableIds(mRealAdapter.hasStableIds());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isHeader(viewType)) {
            return removeParentView(new SectionViewHolder(mHeaderArray.get(viewType)), viewType);
        }
        if (isFooter(viewType)) {
            return removeParentView(new SectionViewHolder(mFooterArray.get(viewType)), viewType);
        }
        return mRealAdapter.onCreateViewHolder(parent, viewType);
    }

    private SectionViewHolder removeParentView(@NonNull SectionViewHolder sectionViewHolder, int viewType) {
        View itemView = sectionViewHolder.itemView;
        if (itemView.getParent() == null) {
            return sectionViewHolder;
        } else {
            RecyclerView recyclerView = (RecyclerView) itemView.getParent();
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            int headerIndex = mHeaderArray.indexOfKey(viewType);
            int footerIndex = mFooterArray.indexOfKey(viewType);
            if (headerIndex != -1 || footerIndex != -1) {
                layoutManager.removeView(itemView);
            }

            return sectionViewHolder;
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        mRealAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!(isHeaderPosition(position) || isFooterPosition(position))) {
            mRealAdapter.onBindViewHolder(holder, position - mHeaderArray.size());
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderArray.size() + mRealAdapter.getItemCount() + mFooterArray.size();
    }

    @Override
    public boolean isShowDivided(int position) {
        SparseArray<View> sectionArray = null;
        if (isHeaderPosition(position)) sectionArray = mHeaderArray;
        if (isFooterPosition(position)) sectionArray = mFooterArray;

        if (sectionArray != null) {
            Object tag = sectionArray.get(getItemViewType(position)).getTag(R.id.section_show_divider);
            if (tag instanceof Boolean) {
                return (boolean) tag;
            }
            return super.isShowDivided(position);
        }

        if (mRealAdapter instanceof OmegaRecyclerView.Adapter) {
            return ((OmegaRecyclerView.Adapter) mRealAdapter).isShowDivided(position - mHeaderArray.size());
        }

        return super.isShowDivided(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || mRealAdapter.getItemCount() <= position || mRealAdapter.getItemCount() == 0) {
            return getItemViewType(position);
        }

        return mRealAdapter.getItemId(position);
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
        if (position < 0 || mRealAdapter.getItemCount() <= position) {
            return StickyHeaderDecoration.NO_HEADER_ID;
        }
        StickyHeaderAdapter stickyHeaderAdapter = getStickyHeaderAdapter();
        if (stickyHeaderAdapter == null) {
            return StickyHeaderDecoration.NO_HEADER_ID;
        }

        int realAdapterItemCount = mRealAdapter.getItemCount();
        if (realAdapterItemCount == 0) {
            return StickyHeaderDecoration.NO_HEADER_ID;
        }
        if (isHeaderPosition(position)) {
            return stickyHeaderAdapter.getHeaderId(0);
        }
        if (isFooterPosition(position)) {
            return stickyHeaderAdapter.getHeaderId(realAdapterItemCount - 1);
        }

        return stickyHeaderAdapter.getHeaderId(position);
    }

    public int applyRealPositionToChildPosition(int realPosition) {
        return realPosition - mHeaderArray.size();
    }

    public int applyChildPositionToRealPosition(int childPosition) {
        return childPosition + mHeaderArray.size();
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
        stickyHeaderAdapter.onBindHeaderViewHolder(viewHolder, position);
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

    public class SectionViewHolder extends RecyclerView.ViewHolder {

        public SectionViewHolder(View itemView) {
            super(itemView);
        }
    }
}
