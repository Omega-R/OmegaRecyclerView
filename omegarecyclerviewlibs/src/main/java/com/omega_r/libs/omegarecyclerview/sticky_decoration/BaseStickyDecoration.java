package com.omega_r.libs.omegarecyclerview.sticky_decoration;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseStickyDecoration extends RecyclerView.ItemDecoration {

    public static final long NO_STICKY_ID = -1L;

    @Nullable
    protected StickyAdapter mStickyAdapter;
    protected int mItemSpace;

    public BaseStickyDecoration(@Nullable StickyAdapter adapter) {
        mStickyAdapter = adapter;
    }

    @CallSuper
    public final void setStickyAdapter(@Nullable StickyAdapter adapter) {
        mStickyAdapter = adapter;
    }

    public final void setItemSpace(int itemSpace) {
        mItemSpace = itemSpace;
    }

    protected final boolean hasSticker(int position) {
        if (mStickyAdapter == null) return false;
        return mStickyAdapter.getStickyId(position) != NO_STICKY_ID;
    }

}
