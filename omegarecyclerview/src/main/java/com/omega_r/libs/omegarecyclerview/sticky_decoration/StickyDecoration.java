package com.omega_r.libs.omegarecyclerview.sticky_decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;
import static com.omega_r.libs.omegarecyclerview.utils.ViewUtils.isReverseLayout;
import static java.lang.Math.abs;

@SuppressWarnings("rawtypes")
abstract class StickyDecoration extends BaseStickyDecoration {

    private static final float CLICK_MOVE_BIAS = 150;

    private long mClickedPosition = NO_POSITION;
    private float mActionDownX;
    private float mActionDownY;

    private final Map<Long, Rect> mMap = new HashMap<>();

    public StickyDecoration(@Nullable StickyAdapter adapter) {
        super(adapter);
    }

    @Override
    public final boolean onTouchEvent(@NonNull RecyclerView parent, @NonNull MotionEvent ev, boolean defaultResult) {
        if (mStickyAdapter == null) return defaultResult;

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null || adapter.getItemCount() == 0) return defaultResult;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionDownX = ev.getX();
                mActionDownY = ev.getY();
                mClickedPosition = NO_POSITION;
                return handleActionDown(parent, ev, defaultResult);
            case MotionEvent.ACTION_UP:
                if (mStickyAdapter != null && mClickedPosition != NO_POSITION) {
                    float eventX = ev.getX();
                    float eventY = ev.getY();
                    if (abs(mActionDownX - eventX) <= CLICK_MOVE_BIAS && abs(mActionDownY - eventY) <= CLICK_MOVE_BIAS) {
                        mStickyAdapter.onClickStickyViewHolder(mClickedPosition);
                    }
                }
                break;
        }
        return super.onTouchEvent(parent, ev, defaultResult);
    }

    protected boolean handleActionDown(@NonNull RecyclerView parent, @NonNull final MotionEvent ev, boolean defaultResult) {
        int eventX = (int) ev.getX();
        int eventY = (int) ev.getY();
        for (Long id : mMap.keySet()) {
            Rect rect = mMap.get(id);
            if (rect != null && !rect.isEmpty() && rect.contains(eventX, eventY)) {
                mClickedPosition = id;
                return true;
            }
        }
        return defaultResult;
    }

    @Override
    public final void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        int count = parent.getChildCount();
        long previousStickerId = -1;

        mMap.forEach(new BiConsumer<Long, Rect>() {
            @Override
            public void accept(Long aLong, Rect rect) {
                if (rect != null) rect.setEmpty();
            }
        });

        if (isReverseLayout(parent)) {
            for (int layoutPos = count - 1; layoutPos >= 0; layoutPos--) {
                previousStickerId = calculateStickerIdAndDrawIt(canvas, parent, true, layoutPos, previousStickerId);
            }
        } else {
            for (int layoutPos = 0; layoutPos < count; layoutPos++) {
                previousStickerId = calculateStickerIdAndDrawIt(canvas, parent, false, layoutPos, previousStickerId);
            }
        }
    }

    private long calculateStickerIdAndDrawIt(Canvas canvas, RecyclerView parent, boolean isReverseLayout,
                                             int layoutPos, long previousStickerId) {
        long previousId = previousStickerId;
        View childView = parent.getChildAt(layoutPos);
        int adapterPosition = parent.getChildAdapterPosition(childView);

        if (mStickyAdapter != null && adapterPosition != RecyclerView.NO_POSITION && hasSticker(adapterPosition)) {
            long currentStickerId = mStickyAdapter.getStickyId(adapterPosition);

            if (currentStickerId != previousId) {
                RecyclerView.ViewHolder stickyHolder = getStickyHolder(parent, adapterPosition);
                previousId = currentStickerId;

                if (stickyHolder != null) {
                    View stickerView = stickyHolder.itemView;
                    int top = getStickerTop(isReverseLayout, childView, stickerView, layoutPos + 1);

                    long previousStickyId = adapterPosition == 0 ? currentStickerId : mStickyAdapter.getStickyId(adapterPosition - 1);

                    if (layoutPos == 0 && top > 0 && adapterPosition > 0
                            && hasSticker(adapterPosition - 1)
                            && previousStickyId != currentStickerId) {

                        RecyclerView.ViewHolder previousStickyViewHolder = getStickyHolder(parent, adapterPosition - 1);

                        if (previousStickyViewHolder != null) {
                            int previousTop = top - previousStickyViewHolder.itemView.getHeight();
                            if (previousTop > 0) previousTop = 0;
                            drawSticky(canvas, childView, previousStickyViewHolder, previousTop);
                            drawSticky(canvas, childView, stickyHolder, top);

                            Rect previousStickyViewHolderBounds = previousStickyViewHolder.itemView.getClipBounds();
                            Rect bounds = stickyHolder.itemView.getClipBounds();
                        }
                    } else {
                        top = getStickerTop(parent, isReverseLayout, childView, stickerView, adapterPosition, layoutPos);
                        drawSticky(canvas, childView, stickyHolder, top);

                        Rect rect = mMap.get(previousId);
                        if (rect == null) {
                            rect = new Rect();
                            mMap.put(previousId, rect);
                        }
                        rect.set(stickerView.getLeft(), top, stickerView.getRight(), top + stickerView.getHeight());
                    }
                }
            }
        }
        return previousId;
    }

    private void drawSticky(Canvas canvas, View childView, RecyclerView.ViewHolder stickyHolder, int top) {
        if (stickyHolder == null) return;

        View stickerView = stickyHolder.itemView;
        canvas.save();
        int left = childView.getLeft();
        canvas.translate(left, top);

        stickerView.setTranslationX(left);
        stickerView.setTranslationY(top);
        stickerView.draw(canvas);
        canvas.restore();
    }

    private int getStickerTop(RecyclerView parent, boolean isReverseLayout, View child, View sticker, int adapterPos, int layoutPos) {
        int childCount = parent.getChildCount();
        int stickerHeight = sticker.getHeight();
        if (mStickyAdapter != null) {
            long currentStickerId = mStickyAdapter.getStickyId(adapterPos);

            if (isReverseLayout && layoutPos == childCount - 1) {
                for (int i = childCount - 1; i >= 1; i--) {
                    int offset = calculateOffset(parent, stickerHeight, currentStickerId, i);
                    if (offset < 0) {
                        return offset;
                    }
                }
            } else if (!isReverseLayout && layoutPos == 0) {
                for (int i = 1; i < childCount; i++) {
                    int offset = calculateOffset(parent, stickerHeight, currentStickerId, i);
                    if (offset < 0) {
                        return offset;
                    }
                }
            }
        }
        return getStickerTop(isReverseLayout, child, sticker, layoutPos);
    }

    abstract int getStickerTop(boolean isReverseLayout, View child, View sticker, int layoutPos);

    private int calculateOffset(@NonNull RecyclerView parent, int stickerHeight, long currentStickyId, int nextPosition) {
        int adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(nextPosition));
        if (mStickyAdapter != null && adapterPosHere != RecyclerView.NO_POSITION) {
            long nextId = mStickyAdapter.getStickyId(adapterPosHere);
            if (nextId != currentStickyId && nextId != NO_STICKY_ID) {
                View next = parent.getChildAt(nextPosition);
                RecyclerView.ViewHolder stickyHolder = getStickyHolder(parent, adapterPosHere);
                return getOffset(stickyHolder, stickerHeight, next);
            }
        }
        return 0;
    }

    abstract int getOffset(@Nullable RecyclerView.ViewHolder stickyHolder, int stickerHeight, View next);

    @Nullable
    RecyclerView.ViewHolder getStickyHolder(RecyclerView parent, int position) {
        if (mStickyAdapter == null) return null;
        RecyclerView.ViewHolder holder = mStickyAdapter.onCreateStickyViewHolder(parent);
        View header = holder.itemView;

        //noinspection unchecked
        mStickyAdapter.onBindStickyViewHolder(holder, position);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), getMeasureStickerWidthMode());
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), getMeasureStickerHeightMode());

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);

        header.measure(childWidth, childHeight);
        header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());

        return holder;
    }

    protected int getMeasureStickerWidthMode() {
        return View.MeasureSpec.EXACTLY;
    }

    protected int getMeasureStickerHeightMode() {
        return View.MeasureSpec.UNSPECIFIED;
    }

}
