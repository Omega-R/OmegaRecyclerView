package com.omega_r.libs.omegarecyclerview.item_decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.item_decoration.decoration_helpers.DividerDecorationHelper;

public class DividerItemDecoration extends BaseItemDecoration {

    private final Drawable mDivider;
    private final int mOffset;
    private final float mDividerAlpha;
    private int mDividerSize;
    private int mPaddingStart;
    private int mPaddingEnd;
    private Rect mViewRect = new Rect();
    private Rect mItemRect = new Rect();

    public DividerItemDecoration(Drawable divider, int dividerSize, int showDivider, int offset, float dividerAlpha) {
        super(showDivider);
        mDivider = divider;
        mDividerSize = dividerSize;
        mOffset = offset;
        mDividerAlpha = dividerAlpha;
        updateSize();
    }

    @Override
    void onOrientationUpdated(int orientation) {
        updateSize();
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
            switch (getOrientation()) {
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
    void getItemOffset(@NonNull Rect outRect, @NonNull RecyclerView parent, @NonNull DividerDecorationHelper helper, int position, int itemCount) {
        if (position == 0 && isShowBeginDivider()) {
            helper.setStart(outRect, mDividerSize);
        }
        if (position != 0 && isShowMiddleDivider()) {
            helper.setStart(outRect, mDividerSize);
        }
        if (position == itemCount - 1 && isShowEndDivider()) {
            helper.setEnd(outRect, mDividerSize);
        }
    }


    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mDivider == null || !(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            super.onDrawOver(c, parent, state);
            return;
        }

        final int childCount = parent.getChildCount();

        if (childCount > 0) {
            DividerDecorationHelper helper = DividerDecorationHelper.getHelper(getOrientation(), parent);
            
            mItemRect.set(parent.getPaddingLeft() + mPaddingStart,
                    parent.getPaddingTop() + mPaddingStart,
                    parent.getWidth() - parent.getPaddingRight() - mPaddingEnd,
                    parent.getHeight() - parent.getPaddingBottom() - mPaddingEnd);

            View child;

            // show beginning divider
            if (isShowBeginDivider()) {
                child = parent.getChildAt(0);
                if (isShowDivider(parent, getAdapterPosition(parent, child))) {
                    updateViewRect(child);
                    helper.setStart(mItemRect, helper.getStart(mViewRect) - helper.getOffset(mOffset));
                    helper.setEnd(mItemRect, helper.getStart(mItemRect) - helper.getOffset(mDividerSize));
                    drawDivider(c, child, mItemRect);
                }
            }

            // show middle dividers
            if (isShowMiddleDivider()) {
                for (int i = 1; i < childCount; i++) {
                    child = parent.getChildAt(i);
                    if (isShowDivider(parent, getAdapterPosition(parent, child))) {
                        updateViewRect(child);
                        helper.setStart(mItemRect, helper.getStart(mViewRect) - helper.getOffset(mOffset));
                        helper.setEnd(mItemRect, helper.getStart(mItemRect) - helper.getOffset(mDividerSize));
                        drawDivider(c, child, mItemRect);
                    }
                }
            }

            // show end divider
            if (isShowEndDivider()) {
                child = parent.getChildAt(childCount - 1);
                if (isShowDivider(parent, childCount - 1)) {
                    updateViewRect(child);
                    helper.setStart(mItemRect, helper.getEnd(mViewRect) + helper.getOffset(mOffset));
                    helper.setEnd(mItemRect, helper.getStart(mItemRect) + helper.getOffset(mDividerSize));
                    drawDivider(c, child, mItemRect);
                }
            }
        }
    }

    private void updateViewRect(View view) {
        view.getHitRect(mViewRect);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        mViewRect.left -= params.leftMargin;
        mViewRect.top -= params.topMargin;
        mViewRect.right += params.rightMargin;
        mViewRect.bottom += params.bottomMargin;
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

}