package com.omega_r.libs.omegarecyclerview.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.core.view.accessibility.AccessibilityRecordCompat;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;

import com.omega_r.libs.omegarecyclerview.R;
import com.omega_r.libs.omegarecyclerview.viewpager.orientation.HorizontalHelper;
import com.omega_r.libs.omegarecyclerview.viewpager.orientation.OrientationHelper;
import com.omega_r.libs.omegarecyclerview.viewpager.orientation.VerticalHelper;
import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class ViewPagerLayoutManager extends RecyclerView.LayoutManager {

    private static final String KEY_EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_TIME_FOR_ITEM_SETTLE = 300;
    private static final int DEFAULT_FLING_THRESHOLD = 2100; //Decrease to increase sensitivity.
    private static final int DEFAULT_TRANSFORM_CLAMP_ITEM_COUNT = 1;
    private static final float DEFAULT_PAGE_SIZE = 1f;
    private static final float SCROLL_TO_SNAP_TO_ANOTHER_ITEM = 0.6f;
    private static final int INFINITE_MIDDLE = Integer.MAX_VALUE / 2;

    //This field will take value of all visible view's center points during the fill phase
    private final Point mViewCenterIteratorPoint = new Point();
    private final Point mRecyclerCenterPoint = new Point();
    private final Point mCurrentViewCenterPoint = new Point();
    private int mChildHalfWidth;
    private int mChildHalfHeight;
    private int mExtraLayoutSpace;

    //Max possible distance a view can travel during one scroll phase
    private int mScrollToChangeCurrent;
    private int mCurrentScrollState;

    private int mScrolled;
    private int mPendingScroll;
    protected int mCurrentPosition = NO_POSITION;
    private int mPendingPosition = NO_POSITION;

    private final SparseArray<View> mViewCacheArray = new SparseArray<>();

    private OrientationHelper mOrientationHelper;

    private boolean mIsFirstOrEmptyLayout;

    private final Context mContext;

    private int mTimeForItemSettle = DEFAULT_TIME_FOR_ITEM_SETTLE;
    private int mOffscreenItems;
    private int mTransformClampItemCount = DEFAULT_TRANSFORM_CLAMP_ITEM_COUNT;

    private boolean mDataSetChangeShiftedPosition;

    private int mFlingThreshold = DEFAULT_FLING_THRESHOLD;
    private boolean mShouldSlideOnFling;

    private float mPageSize = DEFAULT_PAGE_SIZE;

    @NonNull
    private final ScrollStateListener mScrollStateListener;
    @Nullable
    private ItemTransformer mItemTransformer;
    @Nullable
    private Interpolator mInterpolator;
    private int mOrientation;
    private boolean mIsInfinite;

    ViewPagerLayoutManager(@NonNull Context context,
                           @Nullable AttributeSet attrs, int defStyleAttr,
                           @NonNull ScrollStateListener scrollStateListener) {
        mContext = context;
        mScrollStateListener = scrollStateListener;
        setOrientation(HORIZONTAL); // init mOrientationHelper
        setAutoMeasureEnabled(true);
        initAttrs(attrs, defStyleAttr);
    }

    private void initAttrs(@Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.OmegaPagerRecyclerView, defStyleAttr, 0);
            setShouldSlideOnFling(typedArray.getBoolean(R.styleable.OmegaPagerRecyclerView_slideOnFling, false));
            setOrientation(typedArray.getInt(R.styleable.OmegaPagerRecyclerView_android_orientation, HORIZONTAL));
            setPageSize(typedArray.getFloat(R.styleable.OmegaPagerRecyclerView_pageSize, DEFAULT_PAGE_SIZE));
            setItemTransitionTimeMillis(typedArray.getInteger(R.styleable.OmegaPagerRecyclerView_transitionTime, DEFAULT_TIME_FOR_ITEM_SETTLE));
            setSlideOnFlingThreshold(typedArray.getInteger(R.styleable.OmegaPagerRecyclerView_slideOnFlingThreshold, DEFAULT_FLING_THRESHOLD));
            mIsInfinite = typedArray.getBoolean(R.styleable.OmegaPagerRecyclerView_infinite, false);
            typedArray.recycle();
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            mCurrentPosition = mPendingPosition = NO_POSITION;
            mScrolled = mPendingScroll = 0;
            return;
        }
        if (mCurrentPosition == NO_POSITION) {
            mCurrentPosition = calculateFirstPosition();
        }
        if (!mIsFirstOrEmptyLayout) {
            mIsFirstOrEmptyLayout = getChildCount() == 0;
            if (mIsFirstOrEmptyLayout) {
                initChildDimensions(recycler);
            }
        }
        updateRecyclerDimensions();
        detachAndScrapAttachedViews(recycler);
        fill(recycler);
        applyItemTransformToChildren();
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        if (mIsFirstOrEmptyLayout) {
            mScrollStateListener.onCurrentViewFirstLayout();
            mIsFirstOrEmptyLayout = false;
        } else if (mDataSetChangeShiftedPosition) {
            mScrollStateListener.onDataSetChangeChangedPosition();
            mDataSetChangeShiftedPosition = false;
        }
    }

    private void initChildDimensions(RecyclerView.Recycler recycler) {
        View viewToMeasure = getMeasuredChildForAdapterPosition(0, recycler);

        int childViewWidth = getMeasuredWidthWithMargin(viewToMeasure);
        int childViewHeight = getMeasuredHeightWithMargin(viewToMeasure);

        mChildHalfWidth = childViewWidth / 2;
        mChildHalfHeight = childViewHeight / 2;

        mScrollToChangeCurrent = mOrientationHelper.getDistanceToChangeCurrent(childViewWidth,
                childViewHeight);

        mExtraLayoutSpace = mScrollToChangeCurrent * mOffscreenItems;

        detachAndScrapView(viewToMeasure, recycler);
    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec) {
        Rect decorRect = new Rect();
        calculateItemDecorationsForChild(child, decorRect);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + decorRect.left,
                lp.rightMargin + decorRect.right);
        heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + decorRect.top,
                lp.bottomMargin + decorRect.bottom);
        child.measure(widthSpec, heightSpec);
    }

    private int updateSpecWithExtra(int spec, int startInset, int endInset) {
        if (startInset == 0 && endInset == 0) {
            return spec;
        }
        final int mode = View.MeasureSpec.getMode(spec);
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.getSize(spec) - startInset - endInset, mode);
        }
        return spec;
    }

    private View getMeasuredChildForAdapterPosition(int position, RecyclerView.Recycler recycler) {
        View view = recycler.getViewForPosition(calculateRealPosition(position));
        addView(view);

        int width = getWidth();
        int height = getHeight();
        switch (mOrientation) {
            case HORIZONTAL:
                width *= mPageSize;
                break;
            case VERTICAL:
                height *= mPageSize;
                break;
        }

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
        return view;
    }

    private int calculateRealPosition(int position) {
        if (mIsInfinite) {
            int itemCount = super.getItemCount();
            return itemCount == 0 ? position : (position % itemCount);
        } else {
            return position;
        }
    }

    private int calculateFirstPosition() {
        if (mIsInfinite) {
            int itemCount = super.getItemCount();
            return itemCount == 0 ? 0 : (INFINITE_MIDDLE / itemCount) * itemCount;
        } else {
            return 0;
        }
    }

    private int getMeasuredWidthWithMargin(View child) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        return getDecoratedMeasuredWidth(child) + lp.leftMargin + lp.rightMargin;
    }

    private int getMeasuredHeightWithMargin(View child) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        return getDecoratedMeasuredHeight(child) + lp.topMargin + lp.bottomMargin;
    }

    private void updateRecyclerDimensions() {
        mRecyclerCenterPoint.set(getWidth() / 2, getHeight() / 2);
    }

    private void fill(RecyclerView.Recycler recycler) {
        cacheAndDetachAttachedViews();

        mOrientationHelper.setCurrentViewCenter(mRecyclerCenterPoint, mScrolled, mCurrentViewCenterPoint);

        final int endBound = mOrientationHelper.getViewEnd(getWidth(), getHeight());

        if (mScrolled >= 0) {
            layoutViews(recycler, Direction.START, endBound); //Layout items before the current item
            layoutViews(recycler, Direction.END, endBound); //Layout items after the current item
            if (isViewVisible(mCurrentViewCenterPoint, endBound)) {
                //Layout current
                layoutView(recycler, mCurrentPosition, mCurrentViewCenterPoint);
            }
        } else {
            if (isViewVisible(mCurrentViewCenterPoint, endBound)) {
                //Layout current
                layoutView(recycler, mCurrentPosition, mCurrentViewCenterPoint);
            }
            layoutViews(recycler, Direction.START, endBound); //Layout items before the current item
            layoutViews(recycler, Direction.END, endBound); //Layout items after the current item
        }

        recycleDetachedViewsAndClearCache(recycler);
    }

    private void layoutViews(RecyclerView.Recycler recycler, Direction direction, int endBound) {
        final int positionStep = direction.applyTo(1);

        //Predictive layout is required when we are doing smooth fast scroll towards mPendingPosition
        boolean noPredictiveLayoutRequired = mPendingPosition == NO_POSITION
                || !direction.sameAs(mPendingPosition - mCurrentPosition);

        mViewCenterIteratorPoint.set(mCurrentViewCenterPoint.x, mCurrentViewCenterPoint.y);
        for (int pos = mCurrentPosition + positionStep; isInBounds(pos); pos += positionStep) {
            if (pos == mPendingPosition) {
                noPredictiveLayoutRequired = true;
            }
            mOrientationHelper.shiftViewCenter(direction, mScrollToChangeCurrent, mViewCenterIteratorPoint);
            if (isViewVisible(mViewCenterIteratorPoint, endBound)) {
                layoutView(recycler, pos, mViewCenterIteratorPoint);
            } else if (noPredictiveLayoutRequired) {
                break;
            }
        }
    }

    private void layoutView(RecyclerView.Recycler recycler, int position, Point viewCenter) {
        if (position < 0) return;
        View v = mViewCacheArray.get(position);
        if (v == null) {
            v = getMeasuredChildForAdapterPosition(position, recycler);
            layoutDecoratedWithMargins(v,
                    viewCenter.x - mChildHalfWidth, viewCenter.y - mChildHalfHeight,
                    viewCenter.x + mChildHalfWidth, viewCenter.y + mChildHalfHeight);
        } else {
            attachView(v);
            mViewCacheArray.remove(position);
        }
    }

    private void cacheAndDetachAttachedViews() {
        mViewCacheArray.clear();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            mViewCacheArray.put(getPosition(child), child);
        }

        for (int i = 0; i < mViewCacheArray.size(); i++) {
            detachView(mViewCacheArray.valueAt(i));
        }
    }

    private void recycleDetachedViewsAndClearCache(RecyclerView.Recycler recycler) {
        for (int i = 0; i < mViewCacheArray.size(); i++) {
            View view = mViewCacheArray.valueAt(i);
            if (view.getParent() == null) {
                recycler.recycleView(view);
            }
        }
        mViewCacheArray.clear();
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        int newPosition = mCurrentPosition;
        if (mCurrentPosition == NO_POSITION) {
            newPosition = 0;
        } else if (mCurrentPosition >= positionStart) {
            newPosition = Math.min(mCurrentPosition + itemCount, getItemCount() - 1);
        }
        onNewPosition(newPosition);
    }

    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        int newPosition = mCurrentPosition;
        if (getItemCount() == 0) {
            newPosition = NO_POSITION;
        } else if (mCurrentPosition >= positionStart) {
            if (mCurrentPosition < positionStart + itemCount) {
                //If mCurrentPosition is in the removed items, then the new item became current
                mCurrentPosition = NO_POSITION;
            }
            newPosition = Math.max(0, mCurrentPosition - itemCount);
        }
        onNewPosition(newPosition);
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        //notifyDataSetChanged() was called. We need to ensure that mCurrentPosition is not out of bounds
        if (mCurrentPosition != NO_POSITION) {
            mCurrentPosition = Math.min(Math.max(0, mCurrentPosition), getItemCount() - 1);
        }
        mDataSetChangeShiftedPosition = true;
    }

    private void onNewPosition(int position) {
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
            mDataSetChangeShiftedPosition = true;
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollBy(dx, recycler);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollBy(dy, recycler);
    }

    private int scrollBy(int amount, RecyclerView.Recycler recycler) {
        if (getChildCount() == 0) {
            return 0;
        }

        Direction direction = Direction.fromDelta(amount);
        int leftToScroll = calculateAllowedScrollIn(direction);
        if (leftToScroll <= 0) {
            return 0;
        }

        int delta = direction.applyTo(Math.min(leftToScroll, Math.abs(amount)));
        mScrolled += delta;
        if (mPendingScroll != 0) {
            mPendingScroll -= delta;
        }

        mOrientationHelper.offsetChildren(-delta, this);

        if (mIsInfinite || mOrientationHelper.hasNewBecomeVisible(this)) {
            fill(recycler);
        }

        notifyScroll();

        applyItemTransformToChildren();

        return delta;
    }

    private void applyItemTransformToChildren() {
        if (mItemTransformer != null) {
            int clampAfterDistance = mScrollToChangeCurrent * mTransformClampItemCount;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                float position = getCenterRelativePositionOf(child, clampAfterDistance);
                mItemTransformer.transformItem(child, position, mOrientation == HORIZONTAL, mScrolled);
            }
        }
    }

    @Override
    public void scrollToPosition(int position) {
        int scrollPosition = calculateScrollPosition(position);
        if (mCurrentPosition == scrollPosition) {
            return;
        }

        mCurrentPosition = calculateScrollPosition(position);
        requestLayout();
    }

    private int calculateScrollPosition(int position) {
        int itemCount = super.getItemCount();
        if (mIsInfinite && itemCount != 0) {
            int currentWindowPosition = mCurrentPosition / itemCount;
            return currentWindowPosition * itemCount + position;
        }
        return position;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        int scrollPosition = calculateScrollPosition(position);
        if (mCurrentPosition == scrollPosition || mPendingPosition != NO_POSITION) {
            return;
        }
        smoothScrollToPosition(scrollPosition);
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientationHelper.canScrollHorizontally() && getChildCount() != 0;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientationHelper.canScrollVertically() && getChildCount() != 0;
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (mCurrentScrollState == RecyclerView.SCROLL_STATE_IDLE && mCurrentScrollState != state) {
            mScrollStateListener.onScrollStart();
        }

        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            //Scroll is not finished until current view is centered
            boolean isScrollEnded = onScrollEnd();
            if (isScrollEnded) {
                mScrollStateListener.onScrollEnd();
            } else {
                //Scroll continues and we don't want to set mCurrentScrollState to STATE_IDLE,
                //because this will then trigger .mScrollStateListener.onScrollStart()
                return;
            }
        } else if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            onDragStart();
        }
        mCurrentScrollState = state;
    }

    /**
     * @return true if scroll is ended and we don't need to settle items
     */
    private boolean onScrollEnd() {
        if (mPendingPosition != NO_POSITION) {
            mCurrentPosition = mPendingPosition;
            mPendingPosition = NO_POSITION;
            mScrolled = 0;
        }

        Direction scrollDirection = Direction.fromDelta(mScrolled);
        if (Math.abs(mScrolled) == mScrollToChangeCurrent) {
            mCurrentPosition += scrollDirection.applyTo(1);
            mScrolled = 0;
        }

        if (isAnotherItemCloserThanCurrent()) {
            mPendingScroll = getHowMuchIsLeftToScroll(mScrolled);
        } else {
            mPendingScroll = -mScrolled;
        }

        if (mPendingScroll == 0) {
            return true;
        } else {
            startSmoothPendingScroll();
            return false;
        }
    }

    private void onDragStart() {
        //Here we need to:
        //1. Stop any pending scroll
        //2. Set mCurrentPosition to position of the item that is closest to the center
        boolean isScrollingThroughMultiplePositions = Math.abs(mScrolled) > mScrollToChangeCurrent;
        if (isScrollingThroughMultiplePositions) {
            int scrolledPositions = mScrolled / mScrollToChangeCurrent;
            mCurrentPosition += scrolledPositions;
            mScrolled -= scrolledPositions * mScrollToChangeCurrent;
        }
        if (isAnotherItemCloserThanCurrent()) {
            Direction direction = Direction.fromDelta(mScrolled);
            mCurrentPosition += direction.applyTo(1);
            mScrolled = -getHowMuchIsLeftToScroll(mScrolled);
        }
        mPendingPosition = NO_POSITION;
        mPendingScroll = 0;
    }

    protected void onFling(int velocityX, int velocityY) {
        int velocity = mOrientationHelper.getFlingVelocity(velocityX, velocityY);
        int throttleValue = mShouldSlideOnFling ? Math.abs(velocity / mFlingThreshold) : 1;
        int newPosition = mCurrentPosition + Direction.fromDelta(velocity).applyTo(throttleValue);
        newPosition = checkNewOnFlingPositionIsInBounds(newPosition);
        boolean isInScrollDirection = velocity * mScrolled >= 0;
        boolean canFling = isInScrollDirection && isInBounds(newPosition);
        if (canFling) {
            smoothScrollToPosition(newPosition);
        } else {
            returnToCurrentPosition();
        }
    }

    protected void returnToCurrentPosition() {
        mPendingScroll = -mScrolled;
        if (mPendingScroll != 0) {
            startSmoothPendingScroll();
        }
    }

    private int calculateAllowedScrollIn(Direction direction) {
        if (mPendingScroll != 0) {
            return Math.abs(mPendingScroll);
        }
        int allowedScroll;
        boolean isBoundReached;
        boolean isScrollDirectionAsBefore = direction.applyTo(mScrolled) > 0;
        if (direction == Direction.START && mCurrentPosition == 0) {
            //We can scroll to the left when mCurrentPosition == 0 only if we mScrolled to the right before
            isBoundReached = mScrolled == 0;
            allowedScroll = isBoundReached ? 0 : Math.abs(mScrolled);
        } else if (direction == Direction.END && mCurrentPosition == getItemCount() - 1) {
            //We can scroll to the right when mCurrentPosition == last only if we mScrolled to the left before
            isBoundReached = mScrolled == 0;
            allowedScroll = isBoundReached ? 0 : Math.abs(mScrolled);
        } else {
            isBoundReached = false;
            allowedScroll = isScrollDirectionAsBefore ?
                    mScrollToChangeCurrent - Math.abs(mScrolled) :
                    mScrollToChangeCurrent + Math.abs(mScrolled);
        }
        mScrollStateListener.onIsBoundReachedFlagChange(isBoundReached);
        return allowedScroll;
    }

    private void startSmoothPendingScroll() {
        LinearSmoothScroller scroller = new DiscreteLinearSmoothScroller(mContext);
        scroller.setTargetPosition(calculateRealPosition(mCurrentPosition));
        startSmoothScroll(scroller);
    }

    public final void smoothScrollToNextPosition() {
        int nextPosition = mCurrentPosition + 1;
        if (!mIsInfinite && (mCurrentPosition == getItemCount() - 1)) {
            nextPosition = 0;
        }
        smoothScrollToPosition(nextPosition);
    }

    public final void smoothScrollToPosition(int position) {
        if (mCurrentPosition == position || position < 0 || position >= getItemCount()) return;
        mPendingScroll = -mScrolled;
        Direction direction = Direction.fromDelta(position - mCurrentPosition);
        int distanceToScroll = Math.abs(position - mCurrentPosition) * mScrollToChangeCurrent;
        mPendingScroll += direction.applyTo(distanceToScroll);
        mPendingPosition = position;
        startSmoothPendingScroll();
    }

    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        return computeScrollRange();
    }

    @Override
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        return computeScrollOffset(state);
    }

    @Override
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        return computeScrollExtent();
    }

    @Override
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        return computeScrollRange();
    }

    @Override
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        return computeScrollOffset(state);
    }

    @Override
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return computeScrollExtent();
    }

    private int computeScrollOffset(RecyclerView.State state) {
        int scrollbarSize = computeScrollExtent();
        int offset = (int) ((mScrolled / (float) mScrollToChangeCurrent) * scrollbarSize);
        return (mCurrentPosition * scrollbarSize) + offset;
    }

    private int computeScrollExtent() {
        return (getItemCount() == 0) ? 0 : ((int) (computeScrollRange() / (float) getItemCount()));
    }

    private int computeScrollRange() {
        return (getItemCount() == 0) ? 0 : (mScrollToChangeCurrent * (getItemCount() - 1));
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        clearState();
    }

    private void clearState() {
        mViewCacheArray.clear();
        mPendingPosition = NO_POSITION;
        mScrolled = mPendingScroll = 0;
        mCurrentPosition = NO_POSITION;
        removeAllViews();
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        if (mPendingPosition != NO_POSITION) {
            mCurrentPosition = mPendingPosition;
        }
        bundle.putInt(KEY_EXTRA_POSITION, mCurrentPosition);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        mCurrentPosition = bundle.getInt(KEY_EXTRA_POSITION);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    public int getNextPosition() {
        if (mScrolled == 0) {
            return mCurrentPosition;
        } else if (mPendingPosition != NO_POSITION) {
            return calculateRealPosition(mPendingPosition);
        } else {
            return calculateRealPosition(mCurrentPosition + Direction.fromDelta(mScrolled).applyTo(1));
        }
    }

    public void setItemTransformer(@Nullable ItemTransformer itemTransformer) {
        mViewCacheArray.clear();
        removeAllViews();
        mItemTransformer = itemTransformer;
    }

    public void setInterpolator(@Nullable Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setItemTransitionTimeMillis(@IntRange(from = 10) int millis) {
        if (millis < 10) {
            throw new IllegalStateException("Transition time should be more then 10 millis. Your value = " + millis);
        }
        mTimeForItemSettle = millis;
    }

    public void setOffscreenItems(int offscreenItems) {
        mOffscreenItems = offscreenItems;
        mExtraLayoutSpace = mScrollToChangeCurrent * offscreenItems;
        requestLayout();
    }

    public void setTransformClampItemCount(int transformClampItemCount) {
        mTransformClampItemCount = transformClampItemCount;
        applyItemTransformToChildren();
    }

    public void setPageSize(@FloatRange(from = 0f, to = 1f) float pageSize) {
        if (pageSize < 0.f || 1.f < pageSize) {
            throw new IllegalStateException("Item size should be from 0f to 1f. Your value = " + pageSize);
        }
        mPageSize = pageSize;
        removeAllViews();
        requestLayout();
    }

    public final void setInfinite(boolean infinite) {
        mIsInfinite = infinite;
        clearState();
    }

    public void setOrientation(int orientation) {
        switch (orientation) {
            case RecyclerView.HORIZONTAL:
                mOrientationHelper = new HorizontalHelper();
                break;
            case RecyclerView.VERTICAL:
                mOrientationHelper = new VerticalHelper();
                break;
            default:
                throw new IllegalArgumentException("Unknown orientation " + orientation);
        }
        mOrientation = orientation;
        removeAllViews();
        requestLayout();
    }

    public void setShouldSlideOnFling(boolean result) {
        mShouldSlideOnFling = result;
    }

    public void setSlideOnFlingThreshold(int threshold) {
        mFlingThreshold = threshold;
    }

    public int getCurrentPosition() {
        return calculateRealPosition(mCurrentPosition);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (getChildCount() > 0) {
            final AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(event);
            record.setFromIndex(getPosition(getFirstChild()));
            record.setToIndex(getPosition(getLastChild()));
        }
    }

    private float getCenterRelativePositionOf(View v, int maxDistance) {
        float distanceFromCenter = mOrientationHelper.getDistanceFromCenter(mRecyclerCenterPoint,
                getDecoratedLeft(v) + mChildHalfWidth,
                getDecoratedTop(v) + mChildHalfHeight);
        return (distanceFromCenter / maxDistance);
    }

    private int checkNewOnFlingPositionIsInBounds(int position) {
        final int itemCount = getItemCount();
        //The check is required in case slide through multiple items is turned on
        if (mCurrentPosition != 0 && position < 0) {
            //If mCurrentPosition == 0 && position < 0 we forbid scroll to the left,
            //but if mCurrentPosition != 0 we can slide to the first item
            return 0;
        } else if (mCurrentPosition != itemCount - 1 && position >= itemCount) {
            return itemCount - 1;
        }
        return position;
    }

    private int getHowMuchIsLeftToScroll(int dx) {
        return Direction.fromDelta(dx).applyTo(mScrollToChangeCurrent - Math.abs(mScrolled));
    }

    private boolean isAnotherItemCloserThanCurrent() {
        return Math.abs(mScrolled) >= mScrollToChangeCurrent * SCROLL_TO_SNAP_TO_ANOTHER_ITEM;
    }

    public View getFirstChild() {
        return getChildAt(0);
    }

    public View getLastChild() {
        return getChildAt(getChildCount() - 1);
    }

    public int getExtraLayoutSpace() {
        return mExtraLayoutSpace;
    }

    private void notifyScroll() {
        float amountToScroll =
                mPendingPosition != NO_POSITION ? Math.abs(mScrolled + mPendingScroll) : mScrollToChangeCurrent;
        float position = -Math.min(Math.max(-1f, mScrolled / amountToScroll), 1f);
        mScrollStateListener.onScroll(position);
    }

    private boolean isInBounds(int itemPosition) {
        return itemPosition >= 0 && itemPosition < getItemCount();
    }

    @Override
    public View findViewByPosition(int position) {
        return super.findViewByPosition(calculateRealPosition(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        if (mIsInfinite && itemCount != 0) {
            return Integer.MAX_VALUE;
        }
        return itemCount;
    }

    private boolean isViewVisible(Point viewCenter, int endBound) {
        return mOrientationHelper.isViewVisible(viewCenter, mChildHalfWidth, mChildHalfHeight,
                endBound, mExtraLayoutSpace);
    }

    public final boolean isInfinite() {
        return mIsInfinite;
    }

    private class DiscreteLinearSmoothScroller extends LinearSmoothScroller {

        DiscreteLinearSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDxToMakeVisible(View view, int snapPreference) {
            return mOrientationHelper.getPendingDx(-mPendingScroll);
        }

        @Override
        public int calculateDyToMakeVisible(View view, int snapPreference) {
            return mOrientationHelper.getPendingDy(-mPendingScroll);
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            float dist = Math.min(Math.abs(dx), mScrollToChangeCurrent);
            return (int) (Math.max(0.01f, dist / mScrollToChangeCurrent) * mTimeForItemSettle);
        }

        @Nullable
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return new PointF(mOrientationHelper.getPendingDx(mPendingScroll),
                    mOrientationHelper.getPendingDy(mPendingScroll));
        }

        @Override
        protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
            super.onTargetFound(targetView, state, action);
            if (mInterpolator != null) {
                action.setInterpolator(mInterpolator);
            }
        }
    }

    interface ScrollStateListener {

        void onIsBoundReachedFlagChange(boolean isBoundReached);

        void onScrollStart();

        void onScrollEnd();

        void onScroll(float currentViewPosition);

        void onCurrentViewFirstLayout();

        void onDataSetChangeChangedPosition();
    }

}