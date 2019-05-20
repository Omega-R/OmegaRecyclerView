package com.omega_r.libs.omegarecyclerview.item_decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.item_decoration.decoration_helpers.DividerDecorationHelper;

public class DividerItemDecoration extends BaseItemDecoration {

    private final Rect mViewRect = new Rect();
    private final Rect mItemRect = new Rect();
    private final int mOriginalDividerSize;
    private final int mOffset;
    private float mDividerAlpha;
    private Drawable mDivider;
    private int mDividerSize;
    private int mPaddingStart;
    private int mPaddingEnd;


    public DividerItemDecoration(Drawable divider, int dividerSize, int showDivider, int offset, float dividerAlpha) {
        super(showDivider);
        mOriginalDividerSize = dividerSize;
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

    public void setDividerAlpha(float dividerAlpha) {
        this.mDividerAlpha = dividerAlpha;
    }

    public void setDividerDrawable(@NonNull Drawable dividerDrawable) {
        mDivider = dividerDrawable;
        if (mOriginalDividerSize < 0) {
            mDividerSize = mOriginalDividerSize;
            updateSize();
        }
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
    public void getItemOffset(@NonNull Rect outRect, @NonNull RecyclerView parent, @NonNull DividerDecorationHelper helper, int position, int itemCount) {
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
            c.save();
            DividerDecorationHelper helper = DividerDecorationHelper.getHelper(getOrientation(), parent);

            if (parent.getClipToPadding()) {
                mViewRect.set(parent.getPaddingLeft(),
                        parent.getPaddingTop(),
                        parent.getWidth() - parent.getPaddingRight(),
                        parent.getHeight() - parent.getPaddingBottom());

                c.clipRect(mViewRect);

                mItemRect.set(parent.getPaddingLeft() + mPaddingStart,
                        parent.getPaddingTop() + mPaddingStart,
                        parent.getWidth() - parent.getPaddingRight() - mPaddingEnd,
                        parent.getHeight() - parent.getPaddingBottom() - mPaddingEnd);

                helper.setStart(mItemRect, helper.getStart(mItemRect) - mPaddingStart);
                helper.setEnd(mItemRect, helper.getEnd(mItemRect) + mPaddingEnd);
            } else {
                mItemRect.set(mPaddingStart, mPaddingStart,
                        parent.getWidth() - mPaddingEnd, parent.getHeight() - mPaddingEnd);
            }
            View child;

            int offsetIndex = 0;

            // show beginning divider
            if (isShowBeginDivider()) {
                child = parent.getChildAt(0);
                int adapterPosition = getAdapterPosition(parent, child);
                if (adapterPosition == 0 && isShowDividerAbove(parent, 0)) {
                    offsetIndex = 1;
                    updateViewRect(parent, child);
                    helper.setStart(mItemRect, helper.getStart(mViewRect) - helper.getOffset(mOffset));
                    helper.setEnd(mItemRect, helper.getStart(mItemRect) - helper.getOffset(mDividerSize));
                    drawDivider(c, child, mItemRect);
                }
            }

            // show middle dividers
            if (isShowMiddleDivider()) {
                for (int i = offsetIndex; i < childCount; i++) {
                    child = parent.getChildAt(i);
                    int adapterPosition = getAdapterPosition(parent, child);
                    if (adapterPosition > 0 && isShowDividerAbove(parent, adapterPosition)) {
                        updateViewRect(parent, child);
                        helper.setStart(mItemRect, helper.getStart(mViewRect) - helper.getOffset(mOffset));
                        helper.setEnd(mItemRect, helper.getStart(mItemRect) - helper.getOffset(mDividerSize));
                        drawDivider(c, child, mItemRect);
                    }
                }
            }

            // show end divider
            if (isShowEndDivider()) {
                child = parent.getChildAt(childCount - 1);
                int adapterPosition = getAdapterPosition(parent, child);
                int adapterCount = parent.getAdapter().getItemCount();
                if (adapterPosition == adapterCount - 1 && isShowDividerBelow(parent, adapterPosition, adapterCount)) {
                    updateViewRect(parent, child);
                    helper.setStart(mItemRect, helper.getEnd(mViewRect) + helper.getOffset(mOffset));
                    helper.setEnd(mItemRect, helper.getStart(mItemRect) + helper.getOffset(mDividerSize));
                    drawDivider(c, child, mItemRect);
                }
            }

            c.restore();
        }
    }

    private void updateViewRect(RecyclerView parent, View view) {
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

    private boolean isShowDividerBelow(RecyclerView parent, int adapterPosition, int count) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter instanceof OmegaRecyclerView.Adapter) {
            boolean allowedBelow = ((OmegaRecyclerView.Adapter) adapter).isDividerAllowedBelow(adapterPosition);
            if (allowedBelow) {
                return (adapterPosition + 1 == count)
                        || ((OmegaRecyclerView.Adapter) adapter).isDividerAllowedAbove(adapterPosition + 1);

            } else {
                return false;
            }
        }
        return true;
    }

    private boolean isShowDividerAbove(RecyclerView parent, int adapterPosition) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter instanceof OmegaRecyclerView.Adapter) {
            OmegaRecyclerView.Adapter omegaAdapter = (OmegaRecyclerView.Adapter) adapter;
            boolean allowedBelow = adapterPosition == 0 || omegaAdapter.isDividerAllowedBelow(adapterPosition - 1);
            if (allowedBelow) {
                return omegaAdapter.isDividerAllowedAbove(adapterPosition);
            } else {
                return false;
            }
        }
        return true;
    }

}