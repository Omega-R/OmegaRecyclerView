package com.omega_r.libs.omegarecyclerview.item_decoration;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.item_decoration.decoration_helpers.DividerDecorationHelper;

public abstract class BaseItemDecoration extends OmegaRecyclerView.ItemDecoration {

    private final int mShowDivider;
    private int mOrientation = DividerItemDecoration.Orientation.UNKNOWN;

    BaseItemDecoration(int showDivider) {
        mShowDivider = showDivider;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) return;

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) return;

        int itemCount = adapter.getItemCount();
        if (itemCount == 0) return;

        int position = getAdapterPosition(parent, view);
        if (position == RecyclerView.NO_POSITION) return;

        updateOrientation(parent);
        DividerDecorationHelper helper = DividerDecorationHelper.getHelper(mOrientation, parent);
        getItemOffset(outRect, parent, helper, position, itemCount);
    }

    abstract void getItemOffset(@NonNull Rect outRect, @NonNull RecyclerView parent,
                                @NonNull DividerDecorationHelper helper, int position, int itemCount);

    public final int getOrientation() {
        return mOrientation;
    }

    private void updateOrientation(RecyclerView parent) {
        if (mOrientation == DividerItemDecoration.Orientation.UNKNOWN && parent.getLayoutManager() instanceof LinearLayoutManager) {
            mOrientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
            onOrientationUpdated(mOrientation);
        }
    }

    void onOrientationUpdated(int orientation) {
        // nothing
    }

    final boolean isShowBeginDivider() {
        return (mShowDivider & DividerItemDecoration.ShowDivider.BEGINNING) == DividerItemDecoration.ShowDivider.BEGINNING;
    }

    final boolean isShowMiddleDivider() {
        return (mShowDivider & DividerItemDecoration.ShowDivider.MIDDLE) == DividerItemDecoration.ShowDivider.MIDDLE;
    }

    final boolean isShowEndDivider() {
        return (mShowDivider & DividerItemDecoration.ShowDivider.END) == DividerItemDecoration.ShowDivider.END;
    }

    public interface ShowDivider {
        int NONE = 0;
        int BEGINNING = 1;
        int MIDDLE = 2;
        int END = 4;
    }

    public interface Orientation {
        int UNKNOWN = -1;
        int HORIZONTAL = LinearLayout.HORIZONTAL;
        int VERTICAL = LinearLayout.VERTICAL;
    }

}
