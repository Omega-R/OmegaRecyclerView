package com.omega_r.libs.omegarecyclerview.item_decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.item_decoration.decoration_helpers.DividerDecorationHelper;

public class DividerItemDecoration extends OmegaRecyclerView.ItemDecoration {

    private final Drawable mDivider;
    private final int mOffset;
    private final float mDividerAlpha;
    private int mDividerSize;
    private int mShowDivider;
    private int mOrientation;
    private int mPaddingStart;
    private int mPaddingEnd;
    private Rect mViewRect = new Rect();
    private Rect mItemRect = new Rect();

    public DividerItemDecoration(Drawable divider, int dividerSize, int showDivider, int offset, float dividerAlpha) {
        this(divider, Orientation.UNKNOWN, dividerSize, showDivider, offset, dividerAlpha);
    }

    public DividerItemDecoration(Drawable divider, int orientation, int dividerSize, int showDivider, int offset, float dividerAlpha) {
        mOrientation = orientation;
        mDivider = divider;
        mDividerSize = dividerSize;
        mShowDivider = showDivider;
        mOffset = offset;
        mDividerAlpha = dividerAlpha;
        updateSize();
    }

    private void updateOrientation(RecyclerView parent) {
        if (mOrientation == Orientation.UNKNOWN && parent.getLayoutManager() instanceof LinearLayoutManager) {
            mOrientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
            updateSize();
        }
    }

    public void setPadding(int padding) {
        mPaddingStart = padding;
        mPaddingEnd = padding;
    }

    public void setPaddingStart(int padding) {
        mPaddingStart = padding;
    }

    public void setPaddingEnd(int padding) {
        mPaddingEnd = padding;
    }

    private void updateSize() {
        if (mDividerSize < 0) {
            switch (mOrientation) {
                case Orientation.HORIZONTAL:
                    mDividerSize = mDivider.getIntrinsicWidth();
                    break;
                case Orientation.VERTICAL:
                    mDividerSize = mDivider.getIntrinsicHeight();
                    break;
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mDivider == null || !(parent.getLayoutManager() instanceof LinearLayoutManager)) return;

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) return;

        int itemCount = adapter.getItemCount();
        if (itemCount == 0) return;

        int position = getAdapterPosition(parent, view);
        if (position == RecyclerView.NO_POSITION) return;

        updateOrientation(parent);

        if (!isShowDivider(parent, position)) return;

        DividerDecorationHelper helper = DividerDecorationHelper.getHelper(mOrientation, parent);
        if (position == 0 && isShowBeginDivider()) {
            helper.setStart(outRect, mDividerSize);
        }
        if (isShowMiddleDivider()) {
            helper.setStart(outRect, mDividerSize);
        }
        if (position == itemCount - 1 && isShowEndDivider()) {
            helper.setEnd(outRect, mDividerSize);
        }
    }

    private boolean isShowBeginDivider() {
        return (mShowDivider & ShowDivider.BEGINNING) == ShowDivider.BEGINNING;
    }

    private boolean isShowMiddleDivider() {
        return (mShowDivider & ShowDivider.MIDDLE) == ShowDivider.MIDDLE;
    }

    private boolean isShowEndDivider() {
        return (mShowDivider & ShowDivider.END) == ShowDivider.END;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mDivider == null || !(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            super.onDrawOver(c, parent, state);
            return;
        }

        final int childCount = parent.getChildCount();

        if (childCount > 0) {
            DividerDecorationHelper helper = DividerDecorationHelper.getHelper(mOrientation, parent);
            
            mItemRect.set(parent.getPaddingLeft() + mPaddingStart,
                    parent.getPaddingTop() + mPaddingStart,
                    parent.getWidth() - parent.getPaddingRight() - mPaddingEnd,
                    parent.getHeight() - parent.getPaddingBottom() - mPaddingEnd);

            View child;
            RecyclerView.LayoutParams params;

            // show beginning divider
            if (isShowBeginDivider()) {
                child = parent.getChildAt(0);
                child.getHitRect(mViewRect);

                if (isShowDivider(parent, getAdapterPosition(parent, child))) {
                    params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    helper.setStart(mItemRect, helper.getStart(mViewRect) + helper.getStartMargin(params) - helper.getOffset(mOffset));
                    helper.setEnd(mItemRect, helper.getStart(mItemRect) - helper.getOffset(mDividerSize));
                    drawDivider(c, child, mItemRect);
                }
            }

            // show middle dividers
            if (isShowMiddleDivider()) {
                for (int i = 1; i < childCount; i++) {
                    child = parent.getChildAt(i);
                    child.getHitRect(mViewRect);

                    if (isShowDivider(parent, getAdapterPosition(parent, child))) {
                        params = (RecyclerView.LayoutParams) child.getLayoutParams();
                        helper.setStart(mItemRect, helper.getStart(mViewRect) - helper.getStartMargin(params) - helper.getOffset(mOffset));
                        helper.setEnd(mItemRect, helper.getStart(mItemRect) - helper.getOffset(mDividerSize));
                        drawDivider(c, child, mItemRect);
                    }
                }
            }

            // show end divider
            if (isShowEndDivider()) {
                child = parent.getChildAt(childCount - 1);
                child.getHitRect(mViewRect);

                if (isShowDivider(parent, childCount - 1)) {
                    params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    helper.setStart(mItemRect, helper.getEnd(mViewRect) - helper.getEndMargin(params) + helper.getOffset(mOffset));
                    helper.setEnd(mItemRect, helper.getStart(mItemRect) + helper.getOffset(mDividerSize));
                    drawDivider(c, child, mItemRect);
                }
            }
        }
    }

    private void drawDivider(Canvas canvas, View view, Rect rect) {
        Drawable divider = this.mDivider;
        divider.setAlpha((int) (view.getAlpha() * 255f * mDividerAlpha));
        rect.sort();
        divider.setBounds(rect);
        divider.draw(canvas);
    }

    private boolean isShowDivider(RecyclerView parent, int index) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter instanceof OmegaRecyclerView.Adapter) {
            return ((OmegaRecyclerView.Adapter) adapter).isShowDivided(index);
        }
        return true;
    }

    public interface Orientation {
        int UNKNOWN = -1;
        int HORIZONTAL = LinearLayout.HORIZONTAL;
        int VERTICAL = LinearLayout.VERTICAL;
    }

    public interface ShowDivider {
        int NONE = 0;
        int BEGINNING = 1;
        int MIDDLE = 2;
        int END = 4;
    }

}