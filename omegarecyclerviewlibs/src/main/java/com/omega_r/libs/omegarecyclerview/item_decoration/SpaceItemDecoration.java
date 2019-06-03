package com.omega_r.libs.omegarecyclerview.item_decoration;

import android.graphics.Rect;

import com.omega_r.libs.omegarecyclerview.item_decoration.decoration_helpers.DividerDecorationHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends BaseItemDecoration {

    private final int space;

    public SpaceItemDecoration(int showDivider, int space) {
        super(showDivider);
        this.space = space;
    }

    @Override
    void getItemOffset(@NonNull Rect outRect, @NonNull RecyclerView parent,
                       @NonNull DividerDecorationHelper helper, int position, int itemCount) {
        int countBeginEndPositions = getCountBeginEndPositions(parent);
        if (isShowBeginDivider() || countBeginEndPositions <= position) helper.setStart(outRect, space);
        if (isShowEndDivider() && position == itemCount - countBeginEndPositions) helper.setEnd(outRect, space);
    }

    private int getCountBeginEndPositions(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else return 1;
    }

}

