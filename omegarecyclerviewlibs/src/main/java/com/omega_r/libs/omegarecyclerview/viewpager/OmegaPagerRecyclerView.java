package com.omega_r.libs.omegarecyclerview.viewpager;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;


import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformerWrapper;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class OmegaPagerRecyclerView extends OmegaRecyclerView implements ViewPagerLayoutManager.ScrollStateListener {

    private final Set<ScrollStateChangeListener> mScrollStateChangeListenerSet = new HashSet<>();
    private final Set<OnItemChangedListener> mOnItemChangedListenerSet = new HashSet<>();
    @Nullable
    private ViewPager.OnPageChangeListener mViewPagerOnPageChangeListener;
    private boolean mIsOverScrollEnabled;

    public OmegaPagerRecyclerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OmegaPagerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OmegaPagerRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        if (layoutManager != null && !(layoutManager instanceof ViewPagerLayoutManager)) {
            throw new IllegalStateException("LayoutManager " + layoutManager.toString() + " should be ViewPagerLayoutManager");
        }
        super.setLayoutManager(layoutManager);
    }

    @Nullable
    @Override
    public ViewPagerLayoutManager getLayoutManager() {
        return (ViewPagerLayoutManager) super.getLayoutManager();
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        boolean isFling = super.fling(velocityX, velocityY);
        ViewPagerLayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && isFling) {
            layoutManager.onFling(velocityX, velocityY);
        } else if (layoutManager != null) {
            layoutManager.returnToCurrentPosition();
        }
        return isFling;
    }

    @Nullable
    public ViewHolder getViewHolder(int position) {
        View view = null;
        if (getLayoutManager() != null) {
            view = getLayoutManager().findViewByPosition(position);
        }
        return view != null ? (ViewHolder) getChildViewHolder(view) : null;
    }

    /**
     * @return adapter position of the current item or -1 if nothing is selected
     */
    public int getCurrentItem() {
        return getLayoutManager() == null ? NO_POSITION : getLayoutManager().getCurrentPosition();
    }

    public void setViewPagerOnPageChangeListener(@Nullable ViewPager.OnPageChangeListener viewPagerOnPageChangeListener) {
        mViewPagerOnPageChangeListener = viewPagerOnPageChangeListener;
    }

    public void setItemTransformer(ItemTransformer transformer) {
        if (getLayoutManager() != null) {
            getLayoutManager().setItemTransformer(transformer);
        }
    }

    public void setItemTransformer(ViewPager.PageTransformer transformer) {
        setItemTransformer(new ItemTransformerWrapper(transformer));
    }

    public void setItemTransitionTimeMillis(@IntRange(from = 10) int millis) {
        if (getLayoutManager() != null) {
            getLayoutManager().setItemTransitionTimeMillis(millis);
        }
    }

    public void setSlideOnFling(boolean slideOnFling) {
        if (getLayoutManager() != null) {
            getLayoutManager().setShouldSlideOnFling(slideOnFling);
        }
    }

    public void setSlideOnFlingThreshold(int threshold) {
        if (getLayoutManager() != null) {
            getLayoutManager().setSlideOnFlingThreshold(threshold);
        }
    }

    public void setOrientation(int orientation) {
        if (getLayoutManager() != null) {
            getLayoutManager().setOrientation(orientation);
        }
    }

    public void setOffscreenItems(int items) {
        if (getLayoutManager() != null) {
            getLayoutManager().setOffscreenItems(items);
        }
    }

    public void setClampTransformProgressAfter(@IntRange(from = 1) int itemCount) {
        if (itemCount <= 1) {
            throw new IllegalArgumentException("must be >= 1");
        }
        if (getLayoutManager() != null) {
            getLayoutManager().setTransformClampItemCount(itemCount);
        }
    }

    public void setOverScrollEnabled(boolean overScrollEnabled) {
        mIsOverScrollEnabled = overScrollEnabled;
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setPageSize(@FloatRange(from = 0f, to = 1f) float itemsSize) {
        if (getLayoutManager() != null) {
            getLayoutManager().setPageSize(itemsSize);
        }
    }

    public void setInfinite(boolean infinite) {
        if (getLayoutManager() != null) {
            getLayoutManager().setInfinite(infinite);
        }
    }

    public void setInterpolator(@Nullable Interpolator interpolator) {
        if (getLayoutManager() != null) {
            getLayoutManager().setInterpolator(interpolator);
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

    @Override
    public void onIsBoundReachedFlagChange(boolean isBoundReached) {
        if (mIsOverScrollEnabled) {
            setOverScrollMode(isBoundReached ? OVER_SCROLL_ALWAYS : OVER_SCROLL_NEVER);
        }
    }

    public void scrollToPosition(int position) {
        if (getLayoutManager() != null) {
            getLayoutManager().scrollToPosition(position);
        }
    }

    @Override
    public void onScrollStart() {
        ViewPagerLayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return;

        int current = layoutManager.getCurrentPosition();
        ViewHolder holder = getViewHolder(current);
        if (holder != null) {
            notifyScrollStart(holder, current);
        }
    }

    @Override
    public void onScrollEnd() {
        ViewPagerLayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return;

        int current = getLayoutManager().getCurrentPosition();
        ViewHolder holder = getViewHolder(current);
        if (holder != null) {
            notifyScrollEnd(holder, current);
            notifyCurrentItemChanged(holder, current);
        }
    }

    @Override
    public void onScroll(float currentViewPosition) {
        ViewPagerLayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return;

        int currentIndex = getCurrentItem();
        int newIndex = layoutManager.getNextPosition();
        if (currentIndex != newIndex) {
            notifyScroll(currentViewPosition, currentIndex, newIndex,
                    getViewHolder(currentIndex), getViewHolder(newIndex));

            if (mViewPagerOnPageChangeListener != null) {
                float positionOffset;

                if (newIndex > currentIndex) {
                    positionOffset = -currentViewPosition;
                    if (positionOffset == 1) {
                        positionOffset = 0;
                        currentIndex += 1;
                    }
                } else {
                    positionOffset = 1 - currentViewPosition;
                    currentIndex -= 1;
                }

                mViewPagerOnPageChangeListener.onPageScrolled(currentIndex, positionOffset, 0);
            }
        }
    }

    private void notifyScroll(float position, int currentIndex, int newIndex,
                              ViewHolder currentHolder, ViewHolder newHolder) {
        for (ScrollStateChangeListener listener : mScrollStateChangeListenerSet) {
            listener.onScroll(position, currentIndex, newIndex, currentHolder, newHolder);
        }
    }

    private void notifyCurrentItemChanged(ViewHolder holder, int current) {
        for (OnItemChangedListener listener : mOnItemChangedListenerSet) {
            listener.onCurrentItemChanged(holder, current);
        }
        if (mViewPagerOnPageChangeListener != null) {
            mViewPagerOnPageChangeListener.onPageSelected(current);
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

    private void notifyCurrentItemChanged() {
        ViewPagerLayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return;

        int current = layoutManager.getCurrentPosition();
        ViewHolder currentHolder = getViewHolder(current);
        notifyCurrentItemChanged(currentHolder, current);
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
