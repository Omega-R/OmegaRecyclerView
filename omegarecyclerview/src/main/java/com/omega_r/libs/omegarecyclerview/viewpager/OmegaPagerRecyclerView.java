package com.omega_r.libs.omegarecyclerview.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.R;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.AccordionTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.BackgroundToForegroundTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.CubeInTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.CubeOutTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.DepthPageTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.FadeTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.FlipTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.ForegroundToBackgroundTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.RotateDownTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.RotateUpTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.StackTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.TabletTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.ZoomInTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.default_transformers.ZoomOutSlideTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;
import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformerWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@SuppressWarnings("unchecked")
public class OmegaPagerRecyclerView extends OmegaRecyclerView implements ViewPagerLayoutManager.ScrollStateListener, Runnable {

    private static final int DEFAULT_AUTO_SCROLL_INTERVAL = 2000;
    private final Set<ScrollStateChangeListener> mScrollStateChangeListenerSet = new HashSet<>();
    private final Set<OnItemChangedListener> mOnItemChangedListenerSet = new HashSet<>();
    private final Set<ViewPager.OnPageChangeListener> mViewPagerOnPageChangeListeners = new CopyOnWriteArraySet<>();
    private boolean mIsOverScrollEnabled;
    private boolean mFirstLayout = true;
    private boolean mIsAutoScrollEnabled = false;
    private int mAutoScrollIntervalInMilliseconds = DEFAULT_AUTO_SCROLL_INTERVAL;
    private final AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            updateAutoScroll();
        }
    };

    private Handler mAutoScrollHandler;

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
        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OmegaPagerRecyclerView);
            initDefaultAdapter(typedArray);
            initAutoScroll(typedArray);
            initTransformation(typedArray);
            typedArray.recycle();
        }
        mIsOverScrollEnabled = getOverScrollMode() != OVER_SCROLL_NEVER;
        getAutoScrollHandler().removeCallbacksAndMessages(null);

    }

    private void initDefaultAdapter(TypedArray typedArray) {
        boolean useDefaultAdapter = typedArray.getBoolean(R.styleable.OmegaPagerRecyclerView_useDefaultAdapter, false);
        if (useDefaultAdapter) setAdapter(new DefaultPagerAdapter());
    }

    private void initAutoScroll(TypedArray typedArray) {
        mAutoScrollIntervalInMilliseconds = typedArray.getInt(R.styleable.OmegaPagerRecyclerView_autoScrollInterval, DEFAULT_AUTO_SCROLL_INTERVAL);
        mIsAutoScrollEnabled = typedArray.getBoolean(R.styleable.OmegaPagerRecyclerView_autoScrollEnabled, false);
        getAutoScrollHandler().removeCallbacksAndMessages(null);
    }

    private void initTransformation(TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.OmegaPagerRecyclerView_transformer)) {
            int section = typedArray.getInt(R.styleable.OmegaPagerRecyclerView_transformer, Transformers.NONE);
            switch (section) {
                case Transformers.ACCORDION_TRANSFORMER:
                    setItemTransformer(new AccordionTransformer());
                    break;
                case Transformers.BACKGROUND_TO_FOREGROUND_TRANSFORMER:
                    setItemTransformer(new BackgroundToForegroundTransformer());
                    break;
                case Transformers.CUBE_IN_TRANSFORMER:
                    setItemTransformer(new CubeInTransformer());
                    break;
                case Transformers.CUBE_OUT_TRANSFORMER:
                    setItemTransformer(new CubeOutTransformer());
                    break;
                case Transformers.DEPTH_PAGE_TRANSFORMER:
                    setItemTransformer(new DepthPageTransformer());
                    break;
                case Transformers.FADE_TRANSFORMER:
                    setItemTransformer(new FadeTransformer());
                    break;
                case Transformers.FLIP_TRANSFORMER:
                    setItemTransformer(new FlipTransformer());
                    break;
                case Transformers.FOREGROUND_TO_BACKGROUND:
                    setItemTransformer(new ForegroundToBackgroundTransformer());
                    break;
                case Transformers.ROTATE_DOWN_TRANSFORMER:
                    setItemTransformer(new RotateDownTransformer());
                    break;
                case Transformers.ROTATE_UP_TRANSFORMER:
                    setItemTransformer(new RotateUpTransformer());
                    break;
                case Transformers.STACK_TRANSFORMER:
                    setItemTransformer(new StackTransformer());
                    break;
                case Transformers.TABLET_TRANSFORMER:
                    setItemTransformer(new TabletTransformer());
                    break;
                case Transformers.ZOOM_IN_TRANSFORMER:
                    setItemTransformer(new ZoomInTransformer());
                    break;
                case Transformers.ZOOM_OUT_SLIDE_TRANSFORMER:
                    setItemTransformer(new ZoomOutSlideTransformer());
                    break;
            }
        }
    }

    @Override
    protected void initDefaultLayoutManager(@Nullable AttributeSet attrs, int defStyleAttr) {
        if (getLayoutManager() == null) {
            setLayoutManager(new ViewPagerLayoutManager(getContext(), attrs, defStyleAttr, this));
        }
    }

    @Nullable
    @Override
    public ViewPagerLayoutManager getLayoutManager() {
        return (ViewPagerLayoutManager) super.getLayoutManager();
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layoutManager) {
        if (layoutManager != null && !(layoutManager instanceof ViewPagerLayoutManager)) {
            throw new IllegalStateException("LayoutManager " + layoutManager.toString() + " should be ViewPagerLayoutManager");
        }
        super.setLayoutManager(layoutManager);
        updateAutoScroll();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(dataObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            mFirstLayout = true;
            adapter.registerAdapterDataObserver(dataObserver);
        }
    }

    public final void setAutoScrollEnabled(boolean autoScrollEnabled) {
        if (mIsAutoScrollEnabled != autoScrollEnabled) {
            mIsAutoScrollEnabled = autoScrollEnabled;
            updateAutoScroll();
        }
    }

    public final boolean isAutoScrollEnabled() {
        return mIsAutoScrollEnabled;
    }

    public final void setAutoScrollInterval(int intervalInMillis) {
        if (mAutoScrollIntervalInMilliseconds != intervalInMillis) {
            mAutoScrollIntervalInMilliseconds = intervalInMillis;
            updateAutoScroll();
        }
    }

    public final int getAutoScrollInterval() {
        return mAutoScrollIntervalInMilliseconds;
    }

    private void updateAutoScroll() {
        Handler handler = getAutoScrollHandler();
        handler.removeCallbacksAndMessages(null);
        if (mIsAutoScrollEnabled && mAutoScrollIntervalInMilliseconds > 0
                && getItemCount() > 1 && getLayoutManager() != null) {
            handler.postDelayed(this, mAutoScrollIntervalInMilliseconds);
        }
    }

    private Handler getAutoScrollHandler() {
        if (mAutoScrollHandler == null) {
            mAutoScrollHandler = new Handler();
        }
        return mAutoScrollHandler;
    }

    @Override
    public void run() {
        Handler handler = getAutoScrollHandler();

        ViewPagerLayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            handler.removeCallbacksAndMessages(null);
            return;
        }
        layoutManager.smoothScrollToNextPosition();
        handler.postDelayed(this, mAutoScrollIntervalInMilliseconds);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
        updateAutoScroll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getAutoScrollHandler().removeCallbacksAndMessages(null);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            updateAutoScroll();
        } else {
            getAutoScrollHandler().removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mFirstLayout = false;
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
    public RecyclerView.ViewHolder getViewHolder(int position) {
        View view = null;
        if (getLayoutManager() != null) {
            view = getLayoutManager().findViewByPosition(position);
        }
        return view != null ? getChildViewHolder(view) : null;
    }

    /**
     * @return adapter position of the current item or -1 if nothing is selected
     */
    public int getCurrentItem() {
        return getLayoutManager() == null ? NO_POSITION : getLayoutManager().getCurrentPosition();
    }

    /**
     * Set the currently selected page. If the OmegaPagerRecyclerView has already been through its first
     * layout with its current adapter there will be a smooth animated transition between
     * the current item and the specified item.
     *
     * @param item Item index to select
     */
    public void setCurrentItem(int item) {
        setCurrentItem(item, !mFirstLayout);
    }

    /**
     * Set the currently selected page.
     *
     * @param item         Item index to select
     * @param smoothScroll True to smoothly scroll to the new item, false to transition immediately
     */
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (item < 0 || item >= getItemCount()) {
            return;
        }
        if (smoothScroll) {
            smoothScrollToPosition(item);
        } else {
            scrollToPosition(item);
            notifyCurrentItemChanged();
        }
    }

    public int getItemCount() {
        RecyclerView.Adapter adapter = getAdapter();
        return adapter == null ? 0 : adapter.getItemCount();
    }

    /**
     * Set a listener that will be invoked whenever the page changes or is incrementally
     * scrolled. See {@link ViewPager.OnPageChangeListener}.
     *
     * @param listener Listener to set
     * @deprecated Use {@link #addOnPageChangeListener(ViewPager.OnPageChangeListener)}
     * and {@link #removeOnPageChangeListener(ViewPager.OnPageChangeListener)} instead.
     */
    @Deprecated
    public void setOnPageChangeListener(@Nullable ViewPager.OnPageChangeListener listener) {
        if (listener != null) {
            addOnPageChangeListener(listener);
        } else {
            mViewPagerOnPageChangeListeners.clear();
        }
    }

    /**
     * Add a listener that will be invoked whenever the page changes or is incrementally
     * scrolled. See {@link ViewPager.OnPageChangeListener}.
     * <p>
     * <p>Components that add a listener should take care to remove it when finished.
     * Other components that take ownership of a view may call {@link #clearOnPageChangeListeners()}
     * to remove all attached listeners.</p>
     *
     * @param listener listener to add
     */
    public void addOnPageChangeListener(@Nullable ViewPager.OnPageChangeListener listener) {
        mViewPagerOnPageChangeListeners.add(listener);
    }

    /**
     * Remove a listener that was previously added via
     * {@link #addOnPageChangeListener(ViewPager.OnPageChangeListener)}.
     *
     * @param listener listener to remove
     */
    public void removeOnPageChangeListener(@Nullable ViewPager.OnPageChangeListener listener) {
        mViewPagerOnPageChangeListeners.remove(listener);
    }

    /**
     * Remove all listeners that are notified of any changes in scroll state or position.
     */
    public void clearOnPageChangeListeners() {
        mViewPagerOnPageChangeListeners.clear();
    }


    public void setItemTransformer(@Nullable ItemTransformer transformer) {
        ViewPagerLayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            RecyclerView.Adapter adapter = getAdapter();
            if (adapter != null) adapter.notifyDataSetChanged();
            getRecycledViewPool().clear();
            layoutManager.setItemTransformer(transformer);
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

    private void notifyScrollStart(RecyclerView.ViewHolder holder, int current) {
        for (ScrollStateChangeListener listener : mScrollStateChangeListenerSet) {
            listener.onScrollStart(holder, current);
        }
    }

    private void notifyScrollEnd(RecyclerView.ViewHolder holder, int current) {
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
        RecyclerView.ViewHolder holder = getViewHolder(current);
        if (holder != null) {
            notifyScrollStart(holder, current);
        }
    }

    @Override
    public void onScrollEnd() {
        ViewPagerLayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return;

        int current = getLayoutManager().getCurrentPosition();
        RecyclerView.ViewHolder holder = getViewHolder(current);
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

            if (!mViewPagerOnPageChangeListeners.isEmpty()) {
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

                for (ViewPager.OnPageChangeListener listener : mViewPagerOnPageChangeListeners) {
                    listener.onPageScrolled(currentIndex, positionOffset, 0);
                }
            }
        }
    }

    private void notifyScroll(float position, int currentIndex, int newIndex,
                              RecyclerView.ViewHolder currentHolder, RecyclerView.ViewHolder newHolder) {
        for (ScrollStateChangeListener listener : mScrollStateChangeListenerSet) {
            listener.onScroll(position, currentIndex, newIndex, currentHolder, newHolder);
        }
    }

    private void notifyCurrentItemChanged(RecyclerView.ViewHolder holder, int current) {
        for (OnItemChangedListener listener : mOnItemChangedListenerSet) {
            listener.onCurrentItemChanged(holder, current);
        }

        for (ViewPager.OnPageChangeListener listener : mViewPagerOnPageChangeListeners) {
            listener.onPageSelected(current);
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
        RecyclerView.ViewHolder currentHolder = getViewHolder(current);
        notifyCurrentItemChanged(currentHolder, current);
    }

    public interface ScrollStateChangeListener<T extends RecyclerView.ViewHolder> {

        void onScrollStart(@NonNull T currentItemHolder, int adapterPosition);

        void onScrollEnd(@NonNull T currentItemHolder, int adapterPosition);

        void onScroll(float scrollPosition, int currentPosition, int newPosition,
                      @Nullable T currentHolder, @Nullable T newCurrent);
    }

    public interface OnItemChangedListener<T extends RecyclerView.ViewHolder> {
        /*
         * This method will be also triggered when view appears on the screen for the first time.
         * If data set is empty, viewHolder will be null and adapterPosition will be NO_POSITION
         */
        void onCurrentItemChanged(@Nullable T viewHolder, int adapterPosition);
    }

    public interface Transformers {

        int NONE = 0;
        int ACCORDION_TRANSFORMER = 1;
        int BACKGROUND_TO_FOREGROUND_TRANSFORMER = 2;
        int CUBE_IN_TRANSFORMER = 3;
        int CUBE_OUT_TRANSFORMER = 4;
        int DEPTH_PAGE_TRANSFORMER = 5;
        int FADE_TRANSFORMER = 6;
        int FLIP_TRANSFORMER = 7;
        int FOREGROUND_TO_BACKGROUND = 8;
        int ROTATE_DOWN_TRANSFORMER = 9;
        int ROTATE_UP_TRANSFORMER = 10;
        int STACK_TRANSFORMER = 11;
        int TABLET_TRANSFORMER = 12;
        int ZOOM_IN_TRANSFORMER = 13;
        int ZOOM_OUT_SLIDE_TRANSFORMER = 14;

    }

}
