package com.omega_r.libs.omegarecyclerview.sticky_decoration;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MiddleStickyDecoration extends StickyDecoration {

    public MiddleStickyDecoration(@NonNull StickyAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getStickerTop(boolean isReverseLayout, View child, View sticker, int layoutPos) {
        int top = (int) child.getY();
        if (!isReverseLayout && layoutPos == 0 && top > 0) {
            return Math.max(0, top - mItemSpace);
        }
        return Math.max(0, top);
    }

    @Override
    int getOffset(@Nullable RecyclerView.ViewHolder stickyHolder, int stickerHeight, View next) {
        return (int) (next.getY() - stickerHeight);
    }

}
