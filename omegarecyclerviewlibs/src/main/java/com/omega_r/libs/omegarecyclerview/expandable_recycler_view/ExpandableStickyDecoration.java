package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.BaseStickyDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import static com.omega_r.libs.omegarecyclerview.utils.ViewUtils.isReverseLayout;

public class ExpandableStickyDecoration extends BaseStickyDecoration {

    private static final int OFFSET_NOT_FOUND = Integer.MIN_VALUE;
    private static final Comparator<Integer> ASCENDING_COMPARATOR = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    };

    private final Rect mViewRect = new Rect();
    @Nullable
    private OmegaExpandableRecyclerView.Adapter mExpandableAdapter;
    private final Map<Long, OmegaExpandableRecyclerView.BaseViewHolder> mGroupHeaderHolders = new HashMap<>();
    private final Map<Long, RecyclerView.ViewHolder> mStickyHeaderHolders = new HashMap<>();

    private final DrawingInfo mDrawingInfo = new DrawingInfo();
    private final SparseArray<Pair<Integer, View>> mRecyclerViewItemsByAdapterPosition = new SparseArray<>();
    private final List<Integer> mAdapterPositions = new ArrayList<>();

    ExpandableStickyDecoration(@Nullable StickyAdapter adapter,
                               @Nullable OmegaExpandableRecyclerView.Adapter expandableAdapter) {
        super(adapter);
        mExpandableAdapter = expandableAdapter;
    }

    void setExpandableAdapter(@Nullable OmegaExpandableRecyclerView.Adapter adapter) {
        mExpandableAdapter = adapter;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        mDrawingInfo.reset();
        mRecyclerViewItemsByAdapterPosition.clear();
        mAdapterPositions.clear();

        for (int layoutPos = 0; layoutPos < parent.getChildCount(); layoutPos++) {
            View child = parent.getChildAt(layoutPos);
            int adapterPosition = parent.getChildAdapterPosition(child);
            mRecyclerViewItemsByAdapterPosition.put(adapterPosition, Pair.create(layoutPos, child));
            mAdapterPositions.add(adapterPosition);
        }

        Collections.sort(mAdapterPositions, ASCENDING_COMPARATOR);

        if (isReverseLayout(parent)) {
            for (int i = mAdapterPositions.size() - 1; i >= 0; i--) {
                Pair<Integer, View> foundedView = mRecyclerViewItemsByAdapterPosition.get(mAdapterPositions.get(i));
                if (foundedView == null || foundedView.first == null || foundedView.second == null) continue;
                drawHeadersWithIdUpdate(canvas, parent, foundedView.second, true, foundedView.first, mAdapterPositions.get(i));
            }
        } else {
            for (int i = 0; i < mAdapterPositions.size(); i++) {
                Pair<Integer, View> foundedView = mRecyclerViewItemsByAdapterPosition.get(mAdapterPositions.get(i));
                if (foundedView == null || foundedView.first == null || foundedView.second == null) continue;
                drawHeadersWithIdUpdate(canvas, parent, foundedView.second, false, foundedView.first, mAdapterPositions.get(i));
            }
        }
    }

    private void drawHeadersWithIdUpdate(Canvas canvas, RecyclerView parent, View child, boolean isReverseLayout, int layoutPos, int adapterPos) {
        if (adapterPos != RecyclerView.NO_POSITION) {
            RecyclerView.ViewHolder stickyHeader = findStickyHeaderWithIdUpdate(parent, adapterPos);
            RecyclerView.ViewHolder groupHeader = findGroupHeaderWithIdUpdate(parent, adapterPos);

            if (stickyHeader != null && !mDrawingInfo.sticky.isFromCache) {
                int left = child.getLeft();
                int top = getStickyHeaderTop(parent, isReverseLayout, child, stickyHeader.itemView, adapterPos, layoutPos);
                mDrawingInfo.sticky.left = left;
                mDrawingInfo.sticky.top = top;

                mDrawingInfo.sticky.bottom = mDrawingInfo.sticky.top + stickyHeader.itemView.getHeight();

                if (!mDrawingInfo.sticky.cache.isSetUp) {
                    mDrawingInfo.sticky.cache.left = left;
                    mDrawingInfo.sticky.cache.top = top;
                    mDrawingInfo.sticky.cache.topHolder = stickyHeader;
                    mDrawingInfo.sticky.cache.isSetUp = true;
                }
            }

            boolean flagNotDrawGroupHeader = true;
            if (groupHeader != null) {
                mDrawingInfo.group.left = child.getLeft();
                mDrawingInfo.group.top = getGroupHeaderTop(parent, isReverseLayout, child, adapterPos, layoutPos);
                flagNotDrawGroupHeader = mDrawingInfo.group.top == OFFSET_NOT_FOUND;
                if (mDrawingInfo.group.top <= mDrawingInfo.sticky.bottom && !flagNotDrawGroupHeader) {
                    if (mDrawingInfo.group.top == mDrawingInfo.sticky.bottom) {
                        int calcOffset = getFixedGroupOffset(parent, isReverseLayout, layoutPos, adapterPos);
                        if (calcOffset < mDrawingInfo.sticky.bottom) {
                            mDrawingInfo.group.top = calcOffset;
                        }
                    }

                    mViewRect.set(
                            mDrawingInfo.group.left,
                            mDrawingInfo.group.top,
                            mDrawingInfo.group.left + groupHeader.itemView.getWidth(),
                            mDrawingInfo.group.top + groupHeader.itemView.getHeight());
                    notifyParentGroupHeaderShown(parent, groupHeader, mViewRect);
                } else {
                    flagNotDrawGroupHeader = true;
                }
            }

            if (groupHeader != null && !flagNotDrawGroupHeader) {
                drawGroupHeader(canvas, groupHeader);
            }
            if (mDrawingInfo.sticky.cache.isSetUp && mDrawingInfo.sticky.cache.topHolder != null) {
                drawHeader(canvas, mDrawingInfo.sticky.cache.topHolder, mDrawingInfo.sticky.cache.left, mDrawingInfo.sticky.cache.top);
            }
            if (stickyHeader != null) {
                drawStickyHeader(canvas, stickyHeader);
            }
        } else {
            if (mDrawingInfo.sticky.cache.topHolder != null) {
                drawStickyHeader(canvas, mDrawingInfo.sticky.cache.topHolder);
            }
        }
    }

    private int getFixedGroupOffset(RecyclerView parent, boolean isReverseLayout, int layoutPos, int adapterPosition) {
        if (mExpandableAdapter == null) return OFFSET_NOT_FOUND;

        long headerId = mExpandableAdapter.getGroupUniqueId(adapterPosition);
        int calcOffset = mDrawingInfo.sticky.bottom;

        int childCount = parent.getChildCount();
        if (isReverseLayout && layoutPos != childCount - 1 && layoutPos - 1 > 0) {
            for (int i = layoutPos - 1; i >= 1; i--) {
                int offset = calculateGroupOffset(parent, headerId, i);
                if (offset < mDrawingInfo.sticky.bottom && offset != OFFSET_NOT_FOUND) {
                    return offset;
                }
            }
        } else if (!isReverseLayout && layoutPos != 0 && layoutPos < childCount) {
            for (int i = layoutPos + 1; i < childCount; i++) {
                int offset = calculateGroupOffset(parent, headerId, i);
                if (offset < mDrawingInfo.sticky.bottom && offset != OFFSET_NOT_FOUND) {
                    return offset;
                }
            }
        }
        return calcOffset;
    }

    private void drawGroupHeader(Canvas canvas, RecyclerView.ViewHolder groupHeader) {
        drawHeader(canvas, groupHeader, mDrawingInfo.group.left, mDrawingInfo.group.top);
    }

    private void drawStickyHeader(Canvas canvas, RecyclerView.ViewHolder stickyHeader) {
        drawHeader(canvas, stickyHeader, mDrawingInfo.sticky.left, mDrawingInfo.sticky.top);
    }

    private void drawHeader(Canvas canvas, RecyclerView.ViewHolder header, int left, int top) {
        canvas.save();
        canvas.translate(left, top);
        header.itemView.setTranslationX(left);
        header.itemView.setTranslationY(top);
        header.itemView.draw(canvas);
        canvas.restore();
    }

    @Nullable
    private RecyclerView.ViewHolder findStickyHeaderWithIdUpdate(RecyclerView recyclerView, int adapterPos) {
        if (mStickyAdapter != null && hasSticker(adapterPos)) {
            long stickyHeaderId = mStickyAdapter.getStickyId(adapterPos);
            if (stickyHeaderId != mDrawingInfo.sticky.id) {
                RecyclerView.ViewHolder holder = getStickyHeader(recyclerView, adapterPos);
                if (holder != null) {
                    mDrawingInfo.sticky.id = stickyHeaderId;
                    mDrawingInfo.sticky.isFromCache = false;
                    return holder;
                }
            } else {
                mDrawingInfo.sticky.isFromCache = true;
                return mDrawingInfo.sticky.cache.previousHolder;
            }
        }
        return null;
    }

    @Nullable
    private RecyclerView.ViewHolder findGroupHeaderWithIdUpdate(RecyclerView recyclerView, int adapterPos) {
        if (mExpandableAdapter != null) {
            long groupHeaderId = mExpandableAdapter.getGroupUniqueId(adapterPos);
            if (groupHeaderId != mDrawingInfo.group.id) {
                RecyclerView.ViewHolder holder = getGroupHeader(recyclerView, adapterPos);
                if (holder != null) {
                    mDrawingInfo.group.id = groupHeaderId;
                    return holder;
                }
            }
        }
        return null;
    }

    private int getStickyHeaderTop(final RecyclerView parent, boolean isReverseLayout, View child, View header, int adapterPos, int layoutPosition) {
        if (mStickyAdapter == null) return 0;
        int childCount = parent.getChildCount();
        final int headerHeight = header.getHeight();
        int top = ((int) child.getY()) - headerHeight;
        final long currentHeaderId = mStickyAdapter.getStickyId(adapterPos);

        return calculateTop(isReverseLayout, layoutPosition, childCount, top, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return calculateStickyOffset(parent, headerHeight, currentHeaderId, integer);
            }
        });
    }

    private int getGroupHeaderTop(final RecyclerView parent, boolean isReverseLayout, View child, int adapterPos, int layoutPosition) {
        if (mExpandableAdapter == null) return 0;

        if (child.getY() > mDrawingInfo.sticky.bottom) {
            return OFFSET_NOT_FOUND;
        }

        int top = mDrawingInfo.sticky.bottom == 0 ? (int) child.getY() : mDrawingInfo.sticky.bottom;
        int childCount = parent.getChildCount();
        final long currentHeaderId = mExpandableAdapter.getGroupUniqueId(adapterPos);

        return calculateTop(isReverseLayout, layoutPosition, childCount, top, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return calculateGroupOffset(parent, currentHeaderId, integer);
            }
        });
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int headerHeight = 0;

        if (position != RecyclerView.NO_POSITION) {

            if (mStickyAdapter != null && hasSticker(position) && showHeaderAboveItem(parent, position)) {
                RecyclerView.ViewHolder header = getStickyHeader(parent, position);
                if (header != null) {
                    headerHeight += header.itemView.getHeight() - mItemSpace;
                }
            } else if (mExpandableAdapter != null && showGroupHeaderAboveItem(parent, position)) {
                RecyclerView.ViewHolder header = getGroupHeader(parent, position);
                if (header != null) {
                    headerHeight -= mItemSpace;
                }
            }
        }

        outRect.set(0, headerHeight, 0, 0);
    }

    private boolean showHeaderAboveItem(RecyclerView parent, int position) {
        if (mStickyAdapter == null || parent.getLayoutManager() == null) return false;
        if (isReverseLayout(parent)) {
            int itemCount = parent.getLayoutManager().getItemCount();
            return position == (itemCount - 1) || mStickyAdapter.getStickyId(position + 1)
                    != mStickyAdapter.getStickyId(position);
        } else {
            return position == 0 || mStickyAdapter.getStickyId(position - 1)
                    != mStickyAdapter.getStickyId(position);
        }
    }

    private boolean showGroupHeaderAboveItem(RecyclerView parent, int position) {
        if (mExpandableAdapter == null || parent.getLayoutManager() == null) return false;
        if (isReverseLayout(parent)) {
            int itemCount = parent.getLayoutManager().getItemCount();
            return position == (itemCount - 1) || mExpandableAdapter.getGroupUniqueId(position + 1)
                    != mExpandableAdapter.getGroupUniqueId(position);
        } else {
            return position == 0 || mExpandableAdapter.getGroupUniqueId(position - 1)
                    != mExpandableAdapter.getGroupUniqueId(position);
        }
    }

    private void notifyParentGroupHeaderShown(RecyclerView parent, RecyclerView.ViewHolder headerHolder, Rect viewRect) {
        if (parent instanceof OmegaExpandableRecyclerView &&
                headerHolder instanceof OmegaExpandableRecyclerView.Adapter.GroupViewHolder) {
            OmegaExpandableRecyclerView recyclerView = (OmegaExpandableRecyclerView) parent;
            recyclerView.notifyHeaderPosition((OmegaExpandableRecyclerView.Adapter.GroupViewHolder) headerHolder, viewRect);
        }
    }

    private int calculateTop(boolean isReverseLayout, int layoutPos, int childCount, int simpleTop, Function<Integer, Integer> calculateFunc) {
        if (isReverseLayout && layoutPos == childCount - 1) {
            for (int i = childCount - 1; i >= 1; i--) {
                int offset = calculateFunc.apply(i);
                if (offset < mDrawingInfo.sticky.bottom && offset != OFFSET_NOT_FOUND) {
                    return offset;
                }
            }
        } else if (!isReverseLayout && layoutPos == 0) {
            for (int i = 1; i < childCount; i++) {
                int offset = calculateFunc.apply(i);
                if (offset < mDrawingInfo.sticky.bottom && offset != OFFSET_NOT_FOUND) {
                    return offset;
                }
            }
        }
        return Math.max(0, simpleTop);
    }

    private int calculateStickyOffset(RecyclerView parent, int headerHeight, long currentHeaderId, int nextPosition) {
        if (mStickyAdapter == null) return 0;
        int adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(nextPosition));
        if (adapterPosHere != RecyclerView.NO_POSITION) {
            long nextId = mStickyAdapter.getStickyId(adapterPosHere);
            if (nextId != currentHeaderId && nextId != NO_STICKY_ID) {
                View next = parent.getChildAt(nextPosition);
                RecyclerView.ViewHolder viewHolder = getStickyHeader(parent, adapterPosHere);
                if (viewHolder == null) return 0;
                return ((int) next.getY()) - (headerHeight + viewHolder.itemView.getHeight());
            }
        }
        return 0;
    }

    private int calculateGroupOffset(RecyclerView parent, long currentHeaderId, int nextPosition) {
        if (mExpandableAdapter == null) return 0;
        int adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(nextPosition));
        if (adapterPosHere != RecyclerView.NO_POSITION) {
            long nextId = mExpandableAdapter.getGroupUniqueId(adapterPosHere);
            if (nextId != currentHeaderId && nextId != NO_STICKY_ID) {
                View next = parent.getChildAt(nextPosition);
                RecyclerView.ViewHolder viewHolder = getGroupHeader(parent, adapterPosHere);
                if (viewHolder == null) return OFFSET_NOT_FOUND;
                return (int) next.getY() - viewHolder.itemView.getHeight();
            }
        }
        return OFFSET_NOT_FOUND;
    }

    @Nullable
    private RecyclerView.ViewHolder getGroupHeader(RecyclerView parent, int position) {
        if (mExpandableAdapter == null) return null;

        long id = mExpandableAdapter.getGroupUniqueId(position);

        OmegaExpandableRecyclerView.BaseViewHolder holder = mGroupHeaderHolders.get(id);
        boolean isHolderExists = holder != null;

        if (!isHolderExists) {
            holder = mExpandableAdapter.onCreateViewHolder(parent, OmegaExpandableRecyclerView.Adapter.VH_TYPE_GROUP);
            mGroupHeaderHolders.put(id, holder);
        }
        //noinspection unchecked
        mExpandableAdapter.bindGroupViewHolder(holder, position);
        measureAndLayoutHolder(parent, holder);
        return holder;
    }

    @Nullable
    private RecyclerView.ViewHolder getStickyHeader(RecyclerView parent, int position) {
        if (mStickyAdapter == null) return null;

        long id = mStickyAdapter.getStickyId(position);

        RecyclerView.ViewHolder holder = mStickyHeaderHolders.get(id);
        boolean isHolderExists = holder != null;

        if (!isHolderExists) {
            holder = mStickyAdapter.onCreateStickyViewHolder(parent);
            mStickyHeaderHolders.put(id, holder);
        }
        //noinspection unchecked
        mStickyAdapter.onBindStickyViewHolder(holder, position);
        measureAndLayoutHolder(parent, holder);
        return holder;
    }

    private void measureAndLayoutHolder(RecyclerView parent, RecyclerView.ViewHolder viewHolder) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(), viewHolder.itemView.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(), viewHolder.itemView.getLayoutParams().height);

        viewHolder.itemView.measure(childWidth, childHeight);
        viewHolder.itemView.layout(0, 0, viewHolder.itemView.getMeasuredWidth(), viewHolder.itemView.getMeasuredHeight());
    }

    private class DrawingInfo {
        StickyHeaderDrawingInfo sticky = new StickyHeaderDrawingInfo();
        GroupHeaderDrawingInfo group = new GroupHeaderDrawingInfo();

        void reset() {
            sticky.reset();
            group.reset();
        }
    }

    private class StickyHeaderDrawingInfo {
        long id = -1L;
        int left;
        int top;
        int bottom;
        boolean isFromCache;
        StickyHeaderDrawingInfoCache cache = new StickyHeaderDrawingInfoCache();

        void reset() {
            id = -1L;
            left = 0;
            top = 0;
            bottom = 0;
            isFromCache = false;
            cache.reset();
        }
    }

    private class StickyHeaderDrawingInfoCache {
        @Nullable
        RecyclerView.ViewHolder topHolder;

        @Nullable
        RecyclerView.ViewHolder previousHolder;

        int left;
        int top;
        boolean isSetUp;

        void reset() {
            topHolder = null;
            previousHolder = null;
            left = 0;
            top = 0;
            isSetUp = false;
        }
    }

    private class GroupHeaderDrawingInfo {
        long id = -1L;
        int left;
        int top;

        void reset() {
            id = -1L;
            left = 0;
            top = 0;
        }
    }

    private interface Function<PARAM, RESULT> {
        RESULT apply(PARAM param);
    }
}
