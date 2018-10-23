package com.omega_r.libs.omegarecyclerview.sticky_header;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;

public class StickyHeaderOnlyTopDecoration extends StickyHeaderDecoration {

    private Rect mViewRect = new Rect();

    public StickyHeaderOnlyTopDecoration(StickyHeaderAdapter adapter) {
        super(adapter, true);
    }

    @Override
    long calculateHeaderIdAndDrawHeader(Canvas canvas, RecyclerView parent, boolean isReverseLayout, int layoutPos, long previousHeaderId) {
        View child = parent.getChildAt(layoutPos);
        int adapterPos = parent.getChildAdapterPosition(child);

        if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
            long headerId = mAdapter.getHeaderId(adapterPos);

            if (headerId != previousHeaderId) {
                previousHeaderId = headerId;
                RecyclerView.ViewHolder headerHolder = getHeader(parent, adapterPos);
                View header = headerHolder.itemView;

                int left = child.getLeft();
                int top = getHeaderTop(parent, isReverseLayout, child, header, adapterPos, layoutPos);
                if (top <= 0) {
                    if (top == 0) {
                        int calcOffset = getFixedCalcOffset(parent, header, headerId, isReverseLayout, layoutPos);
                        if (calcOffset < 0) {
                            top = calcOffset;
                        }
                    }
                    canvas.save();
                    canvas.translate(left, top);
                    header.setTranslationX(left);
                    header.setTranslationY(top);
                    header.draw(canvas);
                    canvas.restore();

                    mViewRect.set(left, top, left + header.getWidth(), top + header.getHeight());
                    notifyParentHeaderShown(parent, headerHolder, mViewRect);
                }
            }

        }

        return previousHeaderId;
    }

    private int getFixedCalcOffset(RecyclerView parent, View header, long headerId, boolean isReverseLayout, int layoutPos) {
        int calcOffset = 0;
        int headerHeightForLayout = getHeaderHeightForLayout(header);
        if (isReverseLayout && layoutPos != parent.getChildCount() - 1 && layoutPos - 1 > 0) {
            calcOffset = calculateOffset(parent, headerHeightForLayout, headerId, layoutPos - 1);
        } else if (!isReverseLayout && layoutPos != 0 && layoutPos < parent.getChildCount()) {
            calcOffset = calculateOffset(parent, headerHeightForLayout, headerId, layoutPos + 1);
        }
        return calcOffset;
    }

    private void notifyParentHeaderShown(RecyclerView parent, RecyclerView.ViewHolder headerHolder, Rect viewRect) {
        if (parent instanceof OmegaExpandableRecyclerView &&
                headerHolder instanceof OmegaExpandableRecyclerView.Adapter.GroupViewHolder) {
            OmegaExpandableRecyclerView recyclerView = (OmegaExpandableRecyclerView) parent;
            recyclerView.notifyHeaderPosition((OmegaExpandableRecyclerView.Adapter.GroupViewHolder) headerHolder, viewRect);
        }
    }
}
