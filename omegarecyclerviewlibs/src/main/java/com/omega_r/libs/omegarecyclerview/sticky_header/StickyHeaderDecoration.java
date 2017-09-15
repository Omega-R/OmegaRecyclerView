package com.omega_r.libs.omegarecyclerview.sticky_header;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    public static final long NO_HEADER_ID = -1L;

    private StickyHeaderAdapter mAdapter;
    private boolean mRenderInline;

    private int mItemSpace;

    public StickyHeaderDecoration(StickyHeaderAdapter adapter) {
        this(adapter, false);
    }

    public StickyHeaderDecoration(StickyHeaderAdapter adapter, boolean renderInline) {
        mAdapter = adapter;
        mRenderInline = renderInline;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int headerHeight = 0;

        if (position != RecyclerView.NO_POSITION
                && hasHeader(position)
                && showHeaderAboveItem(position)) {

            View header = getHeader(parent, position).itemView;
            headerHeight = getHeaderHeightForLayout(header) - mItemSpace;
        }

        outRect.set(0, headerHeight, 0, 0);
    }

    private boolean showHeaderAboveItem(int itemAdapterPosition) {
        return itemAdapterPosition == 0
                || mAdapter.getHeaderId(itemAdapterPosition - 1)
                != mAdapter.getHeaderId(itemAdapterPosition);
    }

    private boolean hasHeader(int position) {
        return mAdapter.getHeaderId(position) != NO_HEADER_ID;
    }

    private RecyclerView.ViewHolder getHeader(RecyclerView parent, int position) {
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
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int count = parent.getChildCount();
        long previousHeaderId = -1;

        for (int layoutPos = 0; layoutPos < count; layoutPos++) {
            View child = parent.getChildAt(layoutPos);
            int adapterPos = parent.getChildAdapterPosition(child);

            if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
                long headerId = mAdapter.getHeaderId(adapterPos);

                if (headerId != previousHeaderId) {
                    previousHeaderId = headerId;
                    View header = getHeader(parent, adapterPos).itemView;
                    canvas.save();

                    int left = child.getLeft();
                    int top = getHeaderTop(parent, child, header, adapterPos, layoutPos);
                    canvas.translate(left, top);

                    header.setTranslationX(left);
                    header.setTranslationY(top);
                    header.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private int getHeaderTop(RecyclerView parent, View child, View header, int adapterPos, int layoutPos) {
        int headerHeight = getHeaderHeightForLayout(header);
        int top = ((int) child.getY()) - headerHeight;
        if (layoutPos == 0) {
            int count = parent.getChildCount();
            long currentId = mAdapter.getHeaderId(adapterPos);

            for (int i = 1; i < count; i++) {
                int adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(i));
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    long nextId = mAdapter.getHeaderId(adapterPosHere);
                    if (nextId != currentId) {
                        View next = parent.getChildAt(i);
                        int offset = ((int) next.getY()) - (headerHeight + getHeader(parent, adapterPosHere).itemView.getHeight());
                        if (offset < 0) {
                            return offset;
                        } else {
                            break;
                        }
                    }
                }
            }
            top = Math.max(0, top);
        }
        return top;
    }

    public void setItemSpace(int itemSpace) {
        mItemSpace = itemSpace;
    }

    private int getHeaderHeightForLayout(View header) {
        return mRenderInline ? 0 : header.getHeight();
    }
}
