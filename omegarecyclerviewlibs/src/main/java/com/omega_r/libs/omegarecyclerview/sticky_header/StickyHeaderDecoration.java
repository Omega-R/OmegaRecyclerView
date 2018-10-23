package com.omega_r.libs.omegarecyclerview.sticky_header;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    public static final long NO_HEADER_ID = -1L;

    StickyHeaderAdapter mAdapter;
    private boolean mRenderInline;

    private int mItemSpace;

    public StickyHeaderDecoration(StickyHeaderAdapter adapter) {
        this(adapter, false);
    }

    public StickyHeaderDecoration(StickyHeaderAdapter adapter, boolean renderInline) {
        mAdapter = adapter;
        mRenderInline = renderInline;
    }

    public void setAdapter(StickyHeaderAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int headerHeight = 0;

        if (position != RecyclerView.NO_POSITION
                && hasHeader(position)
                && showHeaderAboveItem(parent, position)) {

            View header = getHeader(parent, position).itemView;
            headerHeight = getHeaderHeightForLayout(header) - mItemSpace;
        }

        outRect.set(0, headerHeight, 0, 0);
    }

    private boolean showHeaderAboveItem(RecyclerView parent, int position) {
        if (isReverseLayout(parent)) {
            int itemCount = parent.getLayoutManager().getItemCount();
            return position == (itemCount - 1) || mAdapter.getHeaderId(position + 1)
                    != mAdapter.getHeaderId(position);
        } else {
            return position == 0 || mAdapter.getHeaderId(position - 1)
                    != mAdapter.getHeaderId(position);

        }
    }

    boolean hasHeader(int position) {
        return mAdapter.getHeaderId(position) != NO_HEADER_ID;
    }

    RecyclerView.ViewHolder getHeader(RecyclerView parent, int position) {
        RecyclerView.ViewHolder holder = mAdapter.onCreateHeaderViewHolder(parent);
        View header = holder.itemView;

        //noinspection unchecked
        mAdapter.onBindHeaderViewHolder(holder, position);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);

        header.measure(childWidth, childHeight);
        header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());

        return holder;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int count = parent.getChildCount();
        long previousHeaderId = -1;

        if (isReverseLayout(parent)) {
            for (int layoutPos = count - 1; layoutPos >= 0; layoutPos--) {
                previousHeaderId = calculateHeaderIdAndDrawHeader(canvas, parent, true, layoutPos, previousHeaderId);
            }
        } else {
            for (int layoutPos = 0; layoutPos < count; layoutPos++) {
                previousHeaderId = calculateHeaderIdAndDrawHeader(canvas, parent, false, layoutPos, previousHeaderId);
            }
        }
    }

    long calculateHeaderIdAndDrawHeader(Canvas canvas, RecyclerView parent,
                                        boolean isReverseLayout,
                                        int layoutPos, long previousHeaderId) {
        View child = parent.getChildAt(layoutPos);
        int adapterPos = parent.getChildAdapterPosition(child);

        if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
            long headerId = mAdapter.getHeaderId(adapterPos);

            if (headerId != previousHeaderId) {
                previousHeaderId = headerId;
                View header = getHeader(parent, adapterPos).itemView;
                canvas.save();

                int left = child.getLeft();
                int top = getHeaderTop(parent, isReverseLayout, child, header, adapterPos, layoutPos);
                canvas.translate(left, top);

                header.setTranslationX(left);
                header.setTranslationY(top);
                header.draw(canvas);
                canvas.restore();
            }
        }

        return previousHeaderId;
    }

    int getHeaderTop(RecyclerView parent, boolean isReverseLayout, View child, View header, int adapterPos, int layoutPos) {
        int childCount = parent.getChildCount();
        int headerHeight = getHeaderHeightForLayout(header);
        int top = ((int) child.getY()) - headerHeight;
        long currentHeaderId = mAdapter.getHeaderId(adapterPos);

        if (isReverseLayout && layoutPos == childCount - 1) {
            for (int i = childCount - 1; i >= 1; i--) {
                int offset = calculateOffset(parent, headerHeight, currentHeaderId, i);
                if (offset < 0) {
                    return offset;
                }
            }
        } else if (!isReverseLayout && layoutPos == 0) {
            for (int i = 1; i < childCount; i++) {
                int offset = calculateOffset(parent, headerHeight, currentHeaderId, i);
                if (offset < 0) {
                    return offset;
                }
            }
        }
        return Math.max(0, top);
    }

    int calculateOffset(RecyclerView parent, int headerHeight, long currentHeaderId, int nextPosition) {
        int adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(nextPosition));
        if (adapterPosHere != RecyclerView.NO_POSITION) {
            long nextId = mAdapter.getHeaderId(adapterPosHere);
            if (nextId != currentHeaderId && nextId != NO_HEADER_ID) {
                View next = parent.getChildAt(nextPosition);
                return ((int) next.getY()) - (headerHeight + getHeader(parent, adapterPosHere).itemView.getHeight());
            }
        }
        return 0;
    }

    public void setItemSpace(int itemSpace) {
        mItemSpace = itemSpace;
    }

    int getHeaderHeightForLayout(View header) {
        return mRenderInline ? 0 : header.getHeight();
    }

    private boolean isReverseLayout(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).getReverseLayout();
        }
        return false;
    }
}
