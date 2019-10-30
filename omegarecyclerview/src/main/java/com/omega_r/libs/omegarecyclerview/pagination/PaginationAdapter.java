package com.omega_r.libs.omegarecyclerview.pagination;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

import java.util.List;


public class PaginationAdapter extends WrapperAdapter {

    private static final int VIEW_TYPE_PAGINATION_PROGRESS = -1;
    private static final int VIEW_TYPE_PAGINATION_ERROR = -2;

    @LayoutRes
    private int mPaginationLayout;
    @LayoutRes
    private int mPaginationErrorLayout;

    private PaginationViewState mShowPagination;

    public PaginationAdapter(RecyclerView.Adapter adapter,
                             @LayoutRes int paginationLayout,
                             @LayoutRes int paginationErrorLayout) {
        super(adapter);
        mPaginationLayout = paginationLayout;
        mPaginationErrorLayout = paginationErrorLayout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_PAGINATION_PROGRESS:
                return createPaginationViewHolder(parent, inflater);
            case VIEW_TYPE_PAGINATION_ERROR:
                return createPaginationErrorViewHolder(parent, inflater);
            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }

    private OmegaRecyclerView.ViewHolder createPaginationViewHolder(ViewGroup parent, LayoutInflater inflater) {
        View view = null;
        if (mChildAdapter instanceof PaginationViewCreator) {
            view = ((PaginationViewCreator) mChildAdapter).createPaginationView(parent, inflater);
        }
        if (view == null) {
            view = inflater.inflate(mPaginationLayout, parent, false);
        }
        return new OmegaRecyclerView.ViewHolder(view);
    }

    private OmegaRecyclerView.ViewHolder createPaginationErrorViewHolder(ViewGroup parent, LayoutInflater inflater) {
        View view = null;
        if (mChildAdapter instanceof PaginationViewCreator) {
            view = ((PaginationViewCreator) mChildAdapter).createPaginationErrorView(parent, inflater);
        }
        if (view == null) {
            view = inflater.inflate(mPaginationErrorLayout, parent, false);
        }
        return new OmegaRecyclerView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < super.getItemCount()) {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (position < super.getItemCount()) {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return mShowPagination != null ? super.getItemCount() + 1 : super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        if (position == super.getItemCount()) {
            return RecyclerView.NO_ID;
        }
        return super.getItemId(position);
    }

    @Override
    public boolean isDividerAllowedAbove(int position) {
        if (position == super.getItemCount()) {
            return position != 0;
        }
        return super.isDividerAllowedAbove(position);
    }

    @Override
    public boolean isDividerAllowedBelow(int position) {
        if (position == super.getItemCount()) {
            return false;
        }
        return super.isDividerAllowedBelow(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowPagination != null && position == super.getItemCount()) {
            switch (mShowPagination) {
                case ERROR:
                    return VIEW_TYPE_PAGINATION_ERROR;
                case PROGRESS:
                    return VIEW_TYPE_PAGINATION_PROGRESS;
            }
        }
        return super.getItemViewType(position);
    }

    public void showProgressPagination() {
        if (mShowPagination != PaginationViewState.PROGRESS) {
            mShowPagination = PaginationViewState.PROGRESS;
            mChildAdapter.notifyDataSetChanged();
        }
    }

    public void showErrorPagination() {
        if (mShowPagination != PaginationViewState.ERROR) {
            mShowPagination = PaginationViewState.ERROR;
            mChildAdapter.notifyDataSetChanged();
        }
    }

    public void hidePagination() {
        if (mShowPagination != null) {
            mShowPagination = null;
            mChildAdapter.notifyDataSetChanged();
        }
    }

    enum PaginationViewState {
        PROGRESS,
        ERROR
    }

}
