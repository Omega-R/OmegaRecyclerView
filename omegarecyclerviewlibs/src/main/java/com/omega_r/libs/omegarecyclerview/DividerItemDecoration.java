package com.omega_r.libs.omegarecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

public class DividerItemDecoration extends OmegaRecyclerView.ItemDecoration {

    private final Drawable mDivider;
    private final int mOffset;
    private final float mDividerAlpha;
    private int mDividerSize;
    private int mShowDivider;
    private int mOrientation;
    private int mPaddingStart;
    private int mPaddingEnd;

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
        if (mDivider == null || parent.getLayoutManager() == null || parent.getAdapter() == null) {
            return;
        }

        updateOrientation(parent);

        int position = getAdapterPosition(parent, view);
        int itemCount = parent.getAdapter().getItemCount();

        if (position == RecyclerView.NO_POSITION) return;
        if (position == 0) {
            if (!isShowBeginDivider()) return;
        } else if (position == itemCount - 1) {
            if (!isShowEndDivider()) return;
        } else {
            if (!isShowMiddleDivider()) return;
        }
        if (!isShowDivider(parent, position)) return;

        if (mOrientation == Orientation.VERTICAL) {
            outRect.top = mDividerSize;
            if (position == itemCount - 1 && isShowEndDivider()) outRect.bottom = mDividerSize;
        } else {
            outRect.left = mDividerSize;
            if (position == itemCount - 1 && isShowEndDivider()) outRect.right = mDividerSize;
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
        final Drawable divider = this.mDivider;
        if (divider == null || !(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            super.onDrawOver(c, parent, state);
            return;
        }

        // Initialization needed to avoid compiler warning
        int left = 0, right = 0, top = 0, bottom = 0, size;
        final int childCount = parent.getChildCount();

        if (childCount > 0) {
            size = mDividerSize;
            if (mOrientation == Orientation.VERTICAL) {
                left = parent.getPaddingLeft() + mPaddingStart;
                right = parent.getWidth() - parent.getPaddingRight() - mPaddingEnd;
            } else { //horizontal
                top = parent.getPaddingTop() + mPaddingStart;
                bottom = parent.getHeight() - parent.getPaddingBottom() - mPaddingEnd;
            }

            // show beginning divider
            View child;
            RecyclerView.LayoutParams params;
            int startIndex = 0;
            if (isShowBeginDivider()) {
                child = parent.getChildAt(0);
                if (isShowDivider(parent, getAdapterPosition(parent, child))) {
                    params = (RecyclerView.LayoutParams) child.getLayoutParams();

                    if (mOrientation == Orientation.VERTICAL) {
                        top = child.getTop() + params.topMargin - mDividerSize - mOffset;
                        bottom = top + size;
                    } else { // horizontal
                        left = child.getLeft() + params.rightMargin - mDividerSize - mOffset;
                        right = left + size;
                    }
                    drawDivider(c, child, left, top, right, bottom);
                }
            }

            if (isShowMiddleDivider()) {
                for (int i = startIndex + 1; i < childCount; i++) {
                    child = parent.getChildAt(i);

                    if (isShowDivider(parent, getAdapterPosition(parent, child))) {
                        params = (RecyclerView.LayoutParams) child.getLayoutParams();

                        if (mOrientation == Orientation.VERTICAL) {
                            top = child.getTop() - params.topMargin - mDividerSize - mOffset;
                            bottom = top + size;
                        } else { //horizontal
                            left = child.getLeft() - params.leftMargin - mOffset;
                            right = left + size;
                        }
                        drawDivider(c, child, left, top, right, bottom);
                    }
                }
            }

            // show end divider
            if (isShowEndDivider()) {
                child = parent.getChildAt(childCount - 1);
                if (isShowDivider(parent, childCount - 1)) {
                    params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    if (mOrientation == LinearLayoutManager.VERTICAL) {
                        top =  child.getBottom() + params.bottomMargin + mOffset;
                        bottom = top + size;
                    } else { // horizontal
                        left = child.getRight() + params.rightMargin + mOffset;
                        right = left + size;
                    }
                    drawDivider(c, child, left, top, right, bottom);
                }
            }
        }
    }

    private void drawDivider(Canvas canvas, View view, int left, int top, int right, int bottom) {
        Drawable divider = this.mDivider;
        divider.setAlpha((int) (view.getAlpha() * 255f * mDividerAlpha));
        divider.setBounds(left, top, right, bottom);
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
