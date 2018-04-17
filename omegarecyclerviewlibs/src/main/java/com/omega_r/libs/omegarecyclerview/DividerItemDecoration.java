package com.omega_r.libs.omegarecyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by mac on 28.03.17.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;
    private final int mOffset;
    private final float mDividerAlpha;
    private int mDividerSize;
    private int mShowDivider;
    private int mOrientation;
    private int mPadding;

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
        mPadding = padding;
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
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mDivider == null || parent.getLayoutManager() == null) {
            return;
        }

        updateOrientation(parent);

        if (mOrientation == Orientation.VERTICAL) {
            outRect.top = mDividerSize;
        } else {
            outRect.left = mDividerSize;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
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
                left = parent.getPaddingLeft() + mPadding;
                right = parent.getWidth() - parent.getPaddingRight() - mPadding;
            } else { //horizontal
                top = parent.getPaddingTop() + mPadding;
                bottom = parent.getHeight() - parent.getPaddingBottom() - mPadding;
            }

            // show beginning divider
            View child;
            RecyclerView.LayoutParams params;
            int startIndex = 0;
            if ((mShowDivider & ShowDivider.BEGINNING) == ShowDivider.BEGINNING) {
                for (int i = 0; i < childCount; i++) {
                    child = parent.getChildAt(i);

                    if (isShowDivider(parent, parent.getChildAdapterPosition(child))) {

                        params = (RecyclerView.LayoutParams) child.getLayoutParams();

                        if (mOrientation == Orientation.VERTICAL) {
                            top = child.getTop() + params.topMargin;
                            bottom = top + size;
                        } else { // horizontal
                            left = child.getRight() + params.rightMargin;
                            right = left + size;
                        }
                        divider.setAlpha((int) (child.getAlpha() * 255f * mDividerAlpha));
                        divider.setBounds(left, top, right, bottom);
                        divider.draw(c);
                        startIndex = i;
                        break;
                    }
                }
            }

            if ((mShowDivider & ShowDivider.MIDDLE) == ShowDivider.MIDDLE) {
                for (int i = startIndex + 1; i < childCount; i++) {
                    child = parent.getChildAt(i);

                    if (isShowDivider(parent, parent.getChildAdapterPosition(child))) {
                        params = (RecyclerView.LayoutParams) child.getLayoutParams();

                        if (mOrientation == Orientation.VERTICAL) {
                            top = child.getTop() - params.topMargin - mDividerSize - mOffset;
                            bottom = top + size;
                        } else { //horizontal
                            left = child.getLeft() - params.leftMargin - mOffset;
                            right = left + size;
                        }
                        divider.setAlpha((int) (child.getAlpha() * 255f * mDividerAlpha));
                        divider.setBounds(left, top, right, bottom);
                        divider.draw(c);
                    }
                }
            }

            // show end divider
            if ((mShowDivider & ShowDivider.END) == ShowDivider.END) {
                for (int i = childCount - 1; i >= 0; i--) {
                    child = parent.getChildAt(i);
                    int childLayoutPosition = parent.getChildLayoutPosition(child);
                    int childAdapterPosition = parent.getChildAdapterPosition(child);

                    if (childLayoutPosition == i && isShowDivider(parent, childAdapterPosition)) {
                        params = (RecyclerView.LayoutParams) child.getLayoutParams();
                        if (mOrientation == LinearLayoutManager.VERTICAL) {
                            top = child.getBottom() + params.bottomMargin;
                            bottom = top + size;
                        } else { // horizontal
                            left = child.getRight() + params.rightMargin;
                            right = left + size;
                        }

                        divider.setAlpha((int) (child.getAlpha() * 255f * mDividerAlpha));
                        divider.setBounds(left, top, right, bottom);
                        divider.draw(c);
                        break;
                    }
                }
            }

        }
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
