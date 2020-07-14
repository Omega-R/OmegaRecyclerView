package com.omega_r.libs.omegarecyclerview.sticky_decoration;

import android.view.MotionEvent;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
public abstract class BaseStickyDecoration extends RecyclerView.ItemDecoration {

    public static final long NO_STICKY_ID = -1L;

    @Nullable
    protected StickyAdapter mStickyAdapter;
    protected int mItemSpace;

    public BaseStickyDecoration(@Nullable StickyAdapter adapter) {
        mStickyAdapter = adapter;
    }

    public boolean onTouchEvent(@NonNull RecyclerView parent, @NonNull MotionEvent ev, boolean defaultResult) {
        return defaultResult;
    }

    @CallSuper
    public final void setStickyAdapter(@Nullable StickyAdapter adapter) {
        mStickyAdapter = adapter;
    }

    public final void setItemSpace(int itemSpace) {
        mItemSpace = itemSpace;
    }

    protected final boolean hasSticker(int position) {
        return mStickyAdapter != null && mStickyAdapter.getStickyId(position) != NO_STICKY_ID;
    }

}
