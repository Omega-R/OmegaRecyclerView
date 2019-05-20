package com.omega_r.libs.omegarecyclerview.header;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.R;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.HeaderStickyDecoration;

import java.util.List;

public class HeaderFooterWrapperAdapter<T extends RecyclerView.Adapter> extends OmegaRecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyAdapter {

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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRealAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isHeader(viewType)) {
            return new SectionViewHolder(mHeaderArray.get(viewType));
        }
        if (isFooter(viewType)) {
            return new SectionViewHolder(mFooterArray.get(viewType));
        }

        return mRealAdapter.onCreateViewHolder(parent, viewType);
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
        } else {
            ((SectionViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderArray.size() + mRealAdapter.getItemCount() + mFooterArray.size();
    }

    @Override
    public boolean isDividerAllowedAbove(int position) {
        SparseArray<View> sectionArray = null;
        if (isHeaderPosition(position)) sectionArray = mHeaderArray;
        if (isFooterPosition(position)) sectionArray = mFooterArray;

        if (sectionArray != null) {
            return true;
        }

        if (mRealAdapter instanceof OmegaRecyclerView.Adapter) {
            return ((OmegaRecyclerView.Adapter) mRealAdapter).isDividerAllowedAbove(position - mHeaderArray.size());
        }

        return super.isDividerAllowedAbove(position);
    }

    @Override
    public boolean isDividerAllowedBelow(int position) {
        SparseArray<View> sectionArray = null;
        if (isHeaderPosition(position)) sectionArray = mHeaderArray;
        if (isFooterPosition(position)) sectionArray = mFooterArray;

        if (sectionArray != null) {
            Object tag = sectionArray.get(getItemViewType(position)).getTag(R.id.section_show_divider);
            if (tag instanceof Boolean) {
                return (boolean) tag;
            }
            return super.isDividerAllowedBelow(position);
        }

        if (mRealAdapter instanceof OmegaRecyclerView.Adapter) {
            return ((OmegaRecyclerView.Adapter) mRealAdapter).isDividerAllowedBelow(position - mHeaderArray.size());
        }

        return super.isDividerAllowedBelow(position);
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
    public long getStickyId(int position) {
        if (position < 0 || mRealAdapter.getItemCount() <= position) {
            return HeaderStickyDecoration.NO_STICKY_ID;
        }
        StickyAdapter stickyAdapter = getStickyAdapter();
        if (stickyAdapter == null) {
            return HeaderStickyDecoration.NO_STICKY_ID;
        }

        int realAdapterItemCount = mRealAdapter.getItemCount();
        if (realAdapterItemCount == 0) {
            return HeaderStickyDecoration.NO_STICKY_ID;
        }
        if (isHeaderPosition(position)) {
            return stickyAdapter.getStickyId(0);
        }
        if (isFooterPosition(position)) {
            return stickyAdapter.getStickyId(realAdapterItemCount - 1);
        }

        return stickyAdapter.getStickyId(position);
    }

    public int applyRealPositionToChildPosition(int realPosition) {
        return realPosition - mHeaderArray.size();
    }

    public int applyChildPositionToRealPosition(int childPosition) {
        return childPosition + mHeaderArray.size();
    }

    @Nullable
    public StickyAdapter getStickyAdapter() {
        if (mRealAdapter instanceof StickyAdapter) {
            return (StickyAdapter) mRealAdapter;
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateStickyViewHolder(ViewGroup parent) {
        StickyAdapter stickyAdapter = getStickyAdapter();
        assert stickyAdapter != null;
        return stickyAdapter.onCreateStickyViewHolder(parent);
    }

    @Override
    public void onBindStickyViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        StickyAdapter stickyAdapter = getStickyAdapter();
        assert stickyAdapter != null;
        //noinspection unchecked
        stickyAdapter.onBindStickyViewHolder(viewHolder, position);
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
    protected void tryNotifyItemRemoved(int position) {
        super.tryNotifyItemRemoved(position + mHeaderArray.size());
    }

    @Override
    protected void tryNotifyItemMoved(int fromPosition, int toPosition) {
        super.tryNotifyItemMoved(fromPosition + mHeaderArray.size(), toPosition + mHeaderArray.size());
    }

    @Override
    protected void tryNotifyItemRangeRemoved(int positionStart, int itemCount) {
        super.tryNotifyItemRangeRemoved(positionStart + mHeaderArray.size(), itemCount);
    }

    private static class SectionViewHolder extends RecyclerView.ViewHolder {

        private View contentView;

        private SectionViewHolder(View itemView) {
            super(new SectionContentFrameLayout(itemView.getContext()));
            contentView = itemView;
        }

        private void bind() {
            if (contentView.getParent() instanceof ViewGroup) {
                ((ViewGroup) contentView.getParent()).removeView(contentView);
            }
            itemView.setLayoutParams(new ViewGroup.LayoutParams(contentView.getLayoutParams()));
            ((ViewGroup) itemView).addView(contentView);
        }

    }
}
