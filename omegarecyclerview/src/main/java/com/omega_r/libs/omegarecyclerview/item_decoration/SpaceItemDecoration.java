package com.omega_r.libs.omegarecyclerview.item_decoration;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omega_r.libs.omegarecyclerview.item_decoration.decoration_helpers.DividerDecorationHelper;

public class SpaceItemDecoration extends BaseItemDecoration {

    private final int space;

    public SpaceItemDecoration(int showDivider, int space) {
        super(showDivider);
        this.space = space;
    }

    @Override
    void getItemOffset(@NonNull Rect outRect, @NonNull RecyclerView parent,
                       @NonNull DividerDecorationHelper helper, int position, int itemCount) {
        if (isShowBeginDivider() && position < 1 || position >= 1) helper.setStart(outRect, space);
        if (isShowEndDivider() && position == itemCount - 1) helper.setEnd(outRect, space);
    }

}

