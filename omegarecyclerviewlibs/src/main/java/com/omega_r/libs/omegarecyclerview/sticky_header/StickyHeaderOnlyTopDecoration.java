package com.omega_r.libs.omegarecyclerview.sticky_header;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class StickyHeaderOnlyTopDecoration extends StickyHeaderDecoration {

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
                View header = getHeader(parent, adapterPos).itemView;

                int left = child.getLeft();
                int top = getHeaderTop(parent, isReverseLayout, child, header, adapterPos, layoutPos);
                if (top <= 0) {
                    canvas.save();
                    canvas.translate(left, top);
                    header.setTranslationX(left);
                    header.setTranslationY(top);
                    header.draw(canvas);
                    canvas.restore();
                }
            }
        }

        return previousHeaderId;
    }
}
