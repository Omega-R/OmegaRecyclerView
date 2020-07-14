package com.omega_r.libs.omegarecyclerview.sticky_decoration;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;
import static com.omega_r.libs.omegarecyclerview.utils.ViewUtils.isReverseLayout;

@SuppressWarnings("rawtypes")
public class HeaderStickyDecoration extends StickyDecoration {

    public HeaderStickyDecoration(StickyAdapter adapter) {
        super(adapter);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) return;

        int position = parent.getChildAdapterPosition(view);
        int topOffset = 0;
        if (position != RecyclerView.NO_POSITION && hasSticker(position)
                && showHeaderAboveItem(parent, position)) {

            RecyclerView.ViewHolder stickyHolder = getStickyHolder(parent, position);
            if(stickyHolder != null) {
                View header = stickyHolder.itemView;
                topOffset = header.getHeight();
                if (isReverseLayout(parent)) {
                    if (position != adapter.getItemCount() - 1) topOffset -= mItemSpace;
                } else {
                    if (position != 0) {
                        if (mItemSpace > topOffset) {
                            topOffset = 0;
                        } else {
                            topOffset -= mItemSpace;
                        }
                    }
                }
            }
        }
        outRect.set(0, topOffset, 0, 0);
    }

    private int mClickedPosition = NO_POSITION;

//    @Override
//    public boolean onTouchEvent(@NonNull RecyclerView parent, @NonNull MotionEvent ev, boolean defaultResult) {
//        RecyclerView.Adapter adapter = parent.getAdapter();
//        if (adapter == null || adapter.getItemCount() == 0) {
//            mClickedPosition = NO_POSITION;
//            return defaultResult;
//        }
//
//        float eventY = ev.getY();
//        float eventX = ev.getX();
//        Log.d("StickyDecoration", "EventY " + eventY);
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                View view = parent.findChildViewUnder(eventX, eventY);
//
//                if (view == null) {
//                    int position = getNearestViewPosition(parent, eventY);
//                    if (position != NO_POSITION && hasSticker(position)) {
//                        mClickedPosition = position;
//                        return true;
//                    } else {
//                        mClickedPosition = NO_POSITION;
//                    }
//                } else {
//                    float viewY = view.getY();
//                    if (viewY < 0 || parent.getChildAt(0).equals(view) || parent.getChildAt(parent.getChildCount() - 1).equals(view)) {
//                        int position = parent.getChildAdapterPosition(view);
//                        if (position != NO_POSITION && hasSticker(position)) {
//                            RecyclerView.ViewHolder viewHolder = getStickyHolder(parent, position);
//                            if (viewHolder != null) {
//                                View itemView = viewHolder.itemView;
//                                if (itemView.getLeft() <= eventX && eventX <= itemView.getRight()
//                                        && itemView.getTop() <= eventY && eventY <= itemView.getBottom()) {
//                                    mClickedPosition = position;
//                                    return true;
//                                } else {
//                                    mClickedPosition = NO_POSITION;
//                                }
//                            } else {
//                                mClickedPosition = NO_POSITION;
//                            }
//                        } else {
//                            mClickedPosition = NO_POSITION;
//                        }
//                    } else {
//                        mClickedPosition = NO_POSITION;
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                if (mClickedPosition != NO_POSITION) {
//                    ((StickyAdapter) adapter).onClickStickyViewHolder(mClickedPosition);
//                    mClickedPosition = NO_POSITION;
//                    return true;
//                }
//                break;
//        }
//        return super.onTouchEvent(parent, ev, defaultResult);
//    }

    private int getNearestViewPosition(@NonNull RecyclerView parent, float eventY) {
        int itemCount = parent.getAdapter().getItemCount();

        boolean isReversed = isReverseLayout(parent);

        if (isReversed) {
            for (int i = itemCount - 1; i >= 0; i--) {
                RecyclerView.ViewHolder viewHolder = parent.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    View view = viewHolder.itemView;
                    if (view.getY() >= eventY) return i;
                }
            }
        } else {
            for (int i = 0; i < itemCount; i++) {
                RecyclerView.ViewHolder viewHolder = parent.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    View view = viewHolder.itemView;
                    if (view.getY() >= eventY) return i;
                }
            }
        }
        return NO_POSITION;
    }

    private boolean showHeaderAboveItem(@NonNull RecyclerView parent, int position) {
        if (mStickyAdapter == null) return false;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null) return false;

        if (isReverseLayout(parent)) {
            int itemCount = layoutManager.getItemCount();
            return position == (itemCount - 1) || mStickyAdapter.getStickyId(position + 1)
                    != mStickyAdapter.getStickyId(position);
        } else {
            return position == 0 || mStickyAdapter.getStickyId(position - 1)
                    != mStickyAdapter.getStickyId(position);
        }
    }

    @Override
    int getOffset(@Nullable RecyclerView.ViewHolder stickyHolder, int stickerHeight, View next) {
        if (stickyHolder == null) return 0;
        return ((int) next.getY()) - (stickerHeight + stickyHolder.itemView.getHeight());
    }

    @Override
    protected int getStickerTop(boolean isReverseLayout, View child, View sticker, int layoutPos) {
        int stickerHeight = sticker.getHeight();

        int topMargin = 0;
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            topMargin = ((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
        }

        if (layoutPos == 0) stickerHeight = Math.max(stickerHeight, mItemSpace);
        return (int) Math.max(0, child.getY() - stickerHeight  - topMargin);
    }

}