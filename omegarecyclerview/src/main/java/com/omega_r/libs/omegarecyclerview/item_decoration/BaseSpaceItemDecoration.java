package com.omega_r.libs.omegarecyclerview.item_decoration;

import android.graphics.Rect;

import com.omega_r.libs.omegarecyclerview.item_decoration.decoration_helpers.DividerDecorationHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BaseSpaceItemDecoration extends BaseItemDecoration {

    private final int mItemSpace;

    public BaseSpaceItemDecoration(int showDivider, int space) {
        super(showDivider);
        mItemSpace = space;
    }

    @Override
    void getItemOffset(@NonNull Rect outRect, @NonNull RecyclerView parent,
                       @NonNull DividerDecorationHelper helper, int position, int itemCount) {
        int countBeginEndPositions = getCountBeginEndPositions(parent);
        int itemSpace = getItemSpace(parent, position, itemCount);

        if (isShowBeginDivider() || countBeginEndPositions <= position) helper.setStart(outRect, itemSpace);
        if (isShowEndDivider() && position == itemCount - countBeginEndPositions) helper.setEnd(outRect, itemSpace);

        if (countBeginEndPositions > 1) {
            if (position % countBeginEndPositions != 0 || isShowBeginDivider()) helper.setOtherStart(outRect, itemSpace);
            if (position / (countBeginEndPositions - 1) > 0 && isShowEndDivider()) helper.setOtherEnd(outRect, itemSpace);
        }
    }

    protected int getItemSpace(@NonNull RecyclerView parent, int position, int itemCount) {
        return mItemSpace;
    }

    private int getCountBeginEndPositions(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else return 1;
    }

}

