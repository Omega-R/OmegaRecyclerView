package com.omega_r.libs.omegarecyclerview.header;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

import java.util.List;

public class HeaderFooterWrapperAdapter<T extends RecyclerView.Adapter> extends OmegaRecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Defines available view type integers for headers and footers.
    private static final int BASE_HEADER_VIEW_TYPE = -1 << 10;
    private static final int BASE_FOOTER_VIEW_TYPE = -1 << 11;

    private final T mRealAdapter;
    private SparseArray<View> mHeaderArray = new SparseArray<>();
    private SparseArray<View> mFooterArray = new SparseArray<>();

    public HeaderFooterWrapperAdapter(T adapter) {
        mRealAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeader(viewType)) {
            return new RecyclerView.ViewHolder(mHeaderArray.get(viewType)) {
                // nothing
            };
        }
        if (isFooter(viewType)) {
            return new RecyclerView.ViewHolder(mFooterArray.get(viewType)) {
                // nothing
            };
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
        for (View view : list) {
            mHeaderArray.append(BASE_HEADER_VIEW_TYPE + list.indexOf(view), view);
        }
        notifyDataSetChanged();
    }

    public void addHeader(@NonNull View view) {
        mHeaderArray.append(BASE_HEADER_VIEW_TYPE + mHeaderArray.size(), view);
        notifyDataSetChanged();
    }

    public void setFooters(@NonNull List<View> list) {
        mFooterArray = new SparseArray<>();
        for (View view : list) {
            mFooterArray.append(BASE_FOOTER_VIEW_TYPE + list.indexOf(view), view);
        }
        notifyDataSetChanged();
    }

    public void addFooter(@NonNull View view) {
        mFooterArray.append(BASE_FOOTER_VIEW_TYPE + mFooterArray.size(), view);
        notifyDataSetChanged();
    }

    public void setHeadersVisible(boolean visible) {
        for (int i = 0; i < mHeaderArray.size(); i++) {
            mHeaderArray.valueAt(i).setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setFootersVisible(boolean visible) {
        for (int i = 0; i < mHeaderArray.size(); i++) {
            mFooterArray.valueAt(i).setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

}
