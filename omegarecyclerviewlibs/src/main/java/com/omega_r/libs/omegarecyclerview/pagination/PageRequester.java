package com.omega_r.libs.omegarecyclerview.pagination;

import android.graphics.Rect;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class PageRequester extends RecyclerView.ItemDecoration {

    @Nullable
    private OnPageRequestListener mCallback;
    private int mLastItemCount = -1;
    private boolean mEnabled;

    private int mCurrentPage;

    public void attach(RecyclerView omegaRecyclerView) {
        omegaRecyclerView.addItemDecoration(this);
    }

    public void setPaginationCallback(@Nullable OnPageRequestListener callback) {
        mCallback = callback;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        if (mCallback == null || !mEnabled) return;

        int adapterPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        int itemCount = recyclerView.getAdapter().getItemCount();
        // RecyclerView.Adapter getItemCount gives count of values + 1 (PaginationViewHolder)
        int preventionPosition = itemCount - mCallback.getPagePreventionForEnd() - 1;
        if (adapterPosition >= preventionPosition && itemCount > mLastItemCount) {
            mCurrentPage++;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onPageRequest(mCurrentPage);
                }
            });
            mLastItemCount = itemCount;
        }
    }

    public void reset() {
        mCurrentPage = 0;
        mLastItemCount = -1;
    }

    @Nullable
    public OnPageRequestListener getCallback() {
        return mCallback;
    }

    public void setEnabled(boolean enabled) {
        if (mEnabled != enabled) {
            mEnabled = enabled;
            if (enabled) {
                restart();
            }
        }
    }

    private void restart() {
        if (mCallback != null) {
            mCallback.onPageRequest(mCurrentPage);
        }
    }

}
