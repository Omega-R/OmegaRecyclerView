package com.omega_r.libs.omegarecyclerview.viewpager;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class ViewPagerOmegaRecyclerView extends OmegaRecyclerView implements ViewPagerLayoutManager.ScrollStateListener {

    private final Set<ScrollStateChangeListener> mScrollStateChangeListenerSet = new HashSet<>();
    private final Set<OnItemChangedListener> mOnItemChangedListenerSet = new HashSet<>();
    @Nullable
    private ViewPagerLayoutManager mLayoutManager;
    private boolean mIsOverScrollEnabled;

    public ViewPagerOmegaRecyclerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ViewPagerOmegaRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ViewPagerOmegaRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mIsOverScrollEnabled = getOverScrollMode() != OVER_SCROLL_NEVER;
    }

    @Override
    protected void initDefaultLayoutManager(@Nullable AttributeSet attrs, int defStyleAttr) {
        if (getLayoutManager() == null) {
            setLayoutManager(new ViewPagerLayoutManager(getContext(), attrs, defStyleAttr, this));
        }
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layoutManager) {
        if (layoutManager == null) {
            mLayoutManager = null;
            super.setLayoutManager(mLayoutManager);
            return;
        }
        if (layoutManager instanceof ViewPagerLayoutManager) {
            mLayoutManager = (ViewPagerLayoutManager) layoutManager;
            super.setLayoutManager(mLayoutManager);
        } else {
            throw new IllegalStateException("LayoutManager " + layoutManager.toString() + " should be ViewPagerLayoutManager");
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        boolean isFling = super.fling(velocityX, velocityY);
        if (mLayoutManager != null && isFling) {
            mLayoutManager.onFling(velocityX, velocityY);
        } else if (mLayoutManager != null) {
            mLayoutManager.returnToCurrentPosition();
        }
        return isFling;
    }

    @Nullable
    public ViewHolder getViewHolder(int position) {
        View view = null;
        if (mLayoutManager != null) {
            view = mLayoutManager.findViewByPosition(position);
        }
        return view != null ? (ViewHolder) getChildViewHolder(view) : null;
    }

    /**
     * @return adapter position of the current item or -1 if nothing is selected
     */
    public int getCurrentItem() {
        return mLayoutManager == null ? NO_POSITION : mLayoutManager.getCurrentPosition();
    }

    public void setItemTransformer(ItemTransformer transformer) {
        if (mLayoutManager != null) {
            mLayoutManager.setItemTransformer(transformer);
        }
    }

    public void setItemTransitionTimeMillis(@IntRange(from = 10) int millis) {
        if (mLayoutManager != null) {
            mLayoutManager.setItemTransitionTimeMillis(millis);
        }
    }

    public void setSlideOnFling(boolean slideOnFling) {
        if (mLayoutManager != null) {
            mLayoutManager.setShouldSlideOnFling(slideOnFling);
        }
    }

    public void setSlideOnFlingThreshold(int threshold) {
        if (mLayoutManager != null) {
            mLayoutManager.setSlideOnFlingThreshold(threshold);
        }
    }

    public void setOrientation(int orientation) {
        if (mLayoutManager != null) {
            mLayoutManager.setOrientation(orientation);
        }
    }

    public void setOffscreenItems(int items) {
        if (mLayoutManager != null) {
            mLayoutManager.setOffscreenItems(items);
        }
    }

    public void setClampTransformProgressAfter(@IntRange(from = 1) int itemCount) {
        if (itemCount <= 1) {
            throw new IllegalArgumentException("must be >= 1");
        }
        if (mLayoutManager != null) {
            mLayoutManager.setTransformClampItemCount(itemCount);
        }
    }

    public void setOverScrollEnabled(boolean overScrollEnabled) {
        mIsOverScrollEnabled = overScrollEnabled;
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setItemsSize(@FloatRange(from = 0f, to = 1f) float itemsSize) {
        if (mLayoutManager != null) {
            mLayoutManager.setItemsSize(itemsSize);
        }
    }

    public void addScrollStateChangeListener(@NonNull ScrollStateChangeListener<?> scrollStateChangeListener) {
        mScrollStateChangeListenerSet.add(scrollStateChangeListener);
    }

    public void addOnItemChangedListener(@NonNull OnItemChangedListener<?> onItemChangedListener) {
        mOnItemChangedListenerSet.add(onItemChangedListener);
    }

    public void removeScrollStateChangeListener(@NonNull ScrollStateChangeListener<?> scrollStateChangeListener) {
        mScrollStateChangeListenerSet.remove(scrollStateChangeListener);
    }

    public void removeItemChangedListener(@NonNull OnItemChangedListener<?> onItemChangedListener) {
        mOnItemChangedListenerSet.remove(onItemChangedListener);
    }

    private void notifyScrollStart(ViewHolder holder, int current) {
        for (ScrollStateChangeListener listener : mScrollStateChangeListenerSet) {
            listener.onScrollStart(holder, current);
        }
    }

    private void notifyScrollEnd(ViewHolder holder, int current) {
        for (ScrollStateChangeListener listener : mScrollStateChangeListenerSet) {
            listener.onScrollEnd(holder, current);
        }
    }

    private void notifyScroll(float position, int currentIndex, int newIndex,
                              ViewHolder currentHolder, ViewHolder newHolder) {
        for (ScrollStateChangeListener listener : mScrollStateChangeListenerSet) {
            listener.onScroll(position, currentIndex, newIndex, currentHolder, newHolder);
        }
    }

    private void notifyCurrentItemChanged() {
        if (mOnItemChangedListenerSet.isEmpty() || mLayoutManager == null) {
            return;
        }
        int current = mLayoutManager.getCurrentPosition();
        ViewHolder currentHolder = getViewHolder(current);
        notifyCurrentItemChanged(currentHolder, current);
    }

    private void notifyCurrentItemChanged(ViewHolder holder, int current) {
        for (OnItemChangedListener listener : mOnItemChangedListenerSet) {
            listener.onCurrentItemChanged(holder, current);
        }
    }

    @Override
    public void onIsBoundReachedFlagChange(boolean isBoundReached) {
        if (mIsOverScrollEnabled) {
            setOverScrollMode(isBoundReached ? OVER_SCROLL_ALWAYS : OVER_SCROLL_NEVER);
        }
    }

    public void scrollToPosition(int position) {
        if (mLayoutManager != null) {
            mLayoutManager.scrollToPosition(position);
        }
    }

    @Override
    public void onScrollStart() {
        if (mScrollStateChangeListenerSet.isEmpty() || mLayoutManager == null) {
            return;
        }
        int current = mLayoutManager.getCurrentPosition();
        ViewHolder holder = getViewHolder(current);
        if (holder != null) {
            notifyScrollStart(holder, current);
        }
    }

    @Override
    public void onScrollEnd() {
        if ((mOnItemChangedListenerSet.isEmpty() && mScrollStateChangeListenerSet.isEmpty()) || mLayoutManager == null) {
            return;
        }
        int current = mLayoutManager.getCurrentPosition();
        ViewHolder holder = getViewHolder(current);
        if (holder != null) {
            notifyScrollEnd(holder, current);
            notifyCurrentItemChanged(holder, current);
        }
    }

    @Override
    public void onScroll(float currentViewPosition) {
        if (mScrollStateChangeListenerSet.isEmpty() || mLayoutManager == null) {
            return;
        }
        int currentIndex = getCurrentItem();
        int newIndex = mLayoutManager.getNextPosition();
        if (currentIndex != newIndex) {
            notifyScroll(currentViewPosition, currentIndex, newIndex,
                    getViewHolder(currentIndex), getViewHolder(newIndex));
        }
    }

    @Override
    public void onCurrentViewFirstLayout() {
        post(new Runnable() {
            @Override
            public void run() {
                notifyCurrentItemChanged();
            }
        });
    }

    @Override
    public void onDataSetChangeChangedPosition() {
        notifyCurrentItemChanged();
    }

    public interface ScrollStateChangeListener<T extends ViewHolder> {

        void onScrollStart(@NonNull T currentItemHolder, int adapterPosition);

        void onScrollEnd(@NonNull T currentItemHolder, int adapterPosition);

        void onScroll(float scrollPosition, int currentPosition, int newPosition,
                      @Nullable T currentHolder, @Nullable T newCurrent);
    }

    public interface OnItemChangedListener<T extends ViewHolder> {
        /*
         * This method will be also triggered when view appears on the screen for the first time.
         * If data set is empty, viewHolder will be null and adapterPosition will be NO_POSITION
         */
        void onCurrentItemChanged(@Nullable T viewHolder, int adapterPosition);
    }
}
