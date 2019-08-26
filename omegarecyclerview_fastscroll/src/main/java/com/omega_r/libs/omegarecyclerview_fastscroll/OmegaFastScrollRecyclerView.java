package com.omega_r.libs.omegarecyclerview_fastscroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

import static android.view.View.MeasureSpec.EXACTLY;
import static com.omega_r.libs.omegarecyclerview.utils.ViewUtils.isReverseLayout;
import static com.omega_r.libs.omegarecyclerview_fastscroll.DrawableUtils.getTintedDrawable;
import static com.omega_r.libs.omegarecyclerview_fastscroll.Position.LEFT;
import static com.omega_r.libs.omegarecyclerview_fastscroll.Position.RIGHT;

public class OmegaFastScrollRecyclerView extends OmegaRecyclerView {

    private static final long ANIM_DURATION_IN_MILLIS = 300;
    private static final long BUBBLE_ANIM_DURATION_IN_MILLIS = 100;
    private static final long TRACK_HIDE_DELAY_IN_MILLIS = 1500;
    private static final int MAX_ALPHA = 255;
    private static final int MIN_ALPHA = 0;

    private final OnScrollListener mScrollListener = new OnScrollListener() {

        private boolean isFirstScroll = true;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (!isEnabled() || mFastScrollAdapter == null) return;
            OmegaFastScrollRecyclerView.this.onScrollScreenStateChanged(newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (isFirstScroll) {
                isFirstScroll = false;
                return;
            }
            if (!isEnabled() || mFastScrollAdapter == null) return;
            OmegaFastScrollRecyclerView.this.onScrolled();
        }
    };

    private final ValueAnimator mShowTrackAnimator = ValueAnimator.ofInt();
    private final ValueAnimator mHideTrackAnimator = ValueAnimator.ofInt();
    private final ValueAnimator mShowScrollbarAnimator = ValueAnimator.ofInt();
    private final ValueAnimator mHideScrollbarAnimator = ValueAnimator.ofInt();
    private final ValueAnimator mShowBubbleAnimator = ValueAnimator.ofInt();
    private final ValueAnimator mHideBubbleAnimator = ValueAnimator.ofInt();

    private final Runnable mHiderRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShowTrack() && isAutoHideTrack()) hideTrack();
            if (isShowScrollbar() && isAutoHideScrollbar()) hideScrollbar();
            if (isShowBubble() && isAutoHideBubble()) hideBubble();
        }
    };

    private final SparseArray<RecyclerView.ViewHolder> mHolderSparseArray = new SparseArray<>();
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mTextBounds = new Rect();

    @Nullable
    private FastScrollAdapter mFastScrollAdapter;

    private Drawable mBubbleUpDrawable;
    private Drawable mBubbleDownDrawable;
    @Nullable
    private Drawable mBubbleDrawable;

    private Drawable mTrackDrawable;
    private int mTrackPaddingLeft;
    private int mTrackPaddingTop;
    private int mTrackPaddingRight;
    private int mTrackPaddingBottom;

    private final Rect mScrollbarBounds = new Rect();
    private final Paint mScrollbarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Position mPosition;
    private int mElementsVisibility = FastScrollVisibility.NONE;
    private int mAutoHide = FastScrollAutoHide.NONE;

    private boolean mIsScrollbarVisible;
    private boolean mIsScrollbarTouched;
    private boolean mIsTrackVisible;
    private boolean mIsTrackTouched;
    private boolean mIsBubbleVisible;
    private boolean mIsBubbleTouched;

    private long mAnimDuration = ANIM_DURATION_IN_MILLIS;
    private long mBubbleAnimDuration = BUBBLE_ANIM_DURATION_IN_MILLIS;
    private long mHideTrackDelay = TRACK_HIDE_DELAY_IN_MILLIS;

    private float mLastEventY;

    public OmegaFastScrollRecyclerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OmegaFastScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OmegaFastScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {
        mPosition = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL ? LEFT : RIGHT;
        mTrackDrawable = ContextCompat.getDrawable(context, R.drawable.ic_track);
        initBubbleDrawables(mPosition);

        mTrackPaddingLeft = getResources().getDimensionPixelSize(R.dimen.fastscroll_track_padding_left);
        mTrackPaddingTop = getResources().getDimensionPixelSize(R.dimen.fastscroll_track_padding_top);
        mTrackPaddingRight = getResources().getDimensionPixelSize(R.dimen.fastscroll_track_padding_right);
        mTrackPaddingBottom = getResources().getDimensionPixelSize(R.dimen.fastscroll_track_padding_bottom);

        mScrollbarPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(90);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OmegaFastScrollRecyclerView);
            initAttrs(typedArray);
            typedArray.recycle();
        }

        if (isShowScrollbar() && !isAutoHideScrollbar()) mIsScrollbarVisible = true;
        if (isShowTrack() && !isAutoHideTrack()) mIsTrackVisible = true;
        if (isShowTrack() && !isAutoHideBubble()) mIsBubbleVisible = true;

        initAnimators();
        addOnScrollListener(mScrollListener);
        setWillNotDraw(false);
    }

    private void initAttrs(@NonNull TypedArray typedArray) {
        int position = typedArray.getInt(R.styleable.OmegaFastScrollRecyclerView_position, 0);
        Position newPosition;
        if (position == 0) {
            newPosition = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL ? LEFT : RIGHT;
        } else {
            newPosition = Position.values()[position - 1];
        }
        if (newPosition != mPosition) {
            mPosition = newPosition;
            initBubbleDrawables(mPosition);
        }

        Drawable trackDrawable = typedArray.getDrawable(R.styleable.OmegaFastScrollRecyclerView_trackDrawable);
        if (trackDrawable != null) mTrackDrawable = trackDrawable;
        int trackColor = typedArray.getColor(R.styleable.OmegaFastScrollRecyclerView_omega_trackColor, Color.TRANSPARENT);
        if (trackColor != Color.TRANSPARENT)
            mTrackDrawable = getTintedDrawable(mTrackDrawable, trackColor);

        Drawable bubbleUp = typedArray.getDrawable(R.styleable.OmegaFastScrollRecyclerView_bubbleUpDrawable);
        if (bubbleUp != null) mBubbleUpDrawable = bubbleUp;
        int bubbleUpColor = typedArray.getColor(R.styleable.OmegaFastScrollRecyclerView_bubbleUpColor, Color.TRANSPARENT);
        if (bubbleUpColor != Color.TRANSPARENT)
            mBubbleUpDrawable = getTintedDrawable(mBubbleUpDrawable, bubbleUpColor);

        Drawable bubbleDown = typedArray.getDrawable(R.styleable.OmegaFastScrollRecyclerView_bubbleDownDrawable);
        if (bubbleDown != null) mBubbleDownDrawable = bubbleDown;
        int bubbleDownColor = typedArray.getColor(R.styleable.OmegaFastScrollRecyclerView_bubbleDownColor, Color.TRANSPARENT);
        if (bubbleUpColor != Color.TRANSPARENT)
            mBubbleDownDrawable = getTintedDrawable(mBubbleDownDrawable, bubbleDownColor);


        int trackPadding = typedArray.getDimensionPixelSize(R.styleable.OmegaFastScrollRecyclerView_fastScrollTrackPadding, -1);
        if (trackPadding != -1) {
            mTrackPaddingLeft = trackPadding;
            mTrackPaddingTop = trackPadding;
            mTrackPaddingRight = trackPadding;
            mTrackPaddingBottom = trackPadding;
        }

        mTrackPaddingLeft = typedArray.getDimensionPixelSize(
                R.styleable.OmegaFastScrollRecyclerView_fastScrollTrackPaddingLeft,
                mTrackPaddingLeft
        );
        mTrackPaddingTop = typedArray.getDimensionPixelSize(
                R.styleable.OmegaFastScrollRecyclerView_fastScrollTrackPaddingTop,
                mTrackPaddingTop
        );
        mTrackPaddingRight = typedArray.getDimensionPixelSize(
                R.styleable.OmegaFastScrollRecyclerView_fastScrollTrackPaddingRight,
                mTrackPaddingRight
        );
        mTrackPaddingBottom = typedArray.getDimensionPixelSize(
                R.styleable.OmegaFastScrollRecyclerView_fastScrollTrackPaddingBottom,
                mTrackPaddingBottom
        );

        mElementsVisibility = typedArray.getInt(R.styleable.OmegaFastScrollRecyclerView_fastScrollVisibility, FastScrollVisibility.NONE);
        mAutoHide = typedArray.getInt(R.styleable.OmegaFastScrollRecyclerView_fastScrollAutoHide, FastScrollAutoHide.NONE);

        int scrollbarColor = typedArray.getColor(R.styleable.OmegaFastScrollRecyclerView_fastScrollbarColor, Color.TRANSPARENT);
        if (scrollbarColor != Color.TRANSPARENT) mScrollbarPaint.setColor(scrollbarColor);
        int scrollbarWidth = typedArray.getDimensionPixelSize(R.styleable.OmegaFastScrollRecyclerView_fastScrollbarWidth, 0);
        if (scrollbarWidth >= 0) mScrollbarPaint.setStrokeWidth(scrollbarWidth);

        mAnimDuration = typedArray.getInt(R.styleable.OmegaFastScrollRecyclerView_fastScrollAnimDuration, (int) mAnimDuration);
        mBubbleAnimDuration = typedArray.getInt(R.styleable.OmegaFastScrollRecyclerView_fastScrollBubbleAnimDuration, (int) mBubbleAnimDuration);
        mHideTrackDelay = typedArray.getInt(R.styleable.OmegaFastScrollRecyclerView_fastScrollHideTrackDelay, (int) mHideTrackDelay);

        if (isShowBubble() && isAutoHideBubble()) {
            mBubbleUpDrawable.setAlpha(MIN_ALPHA);
            mBubbleDownDrawable.setAlpha(MIN_ALPHA);
        }

        mTextPaint.setColor(typedArray.getColor(R.styleable.OmegaFastScrollRecyclerView_fastScrollTextColor, Color.WHITE));
        int textSize = typedArray.getDimensionPixelSize(R.styleable.OmegaFastScrollRecyclerView_fastScrollTextSize,
                getResources().getDimensionPixelSize(R.dimen.fastscroll_text_size));
        mTextPaint.setTextSize(textSize);
    }

    private void initBubbleDrawables(Position position) {
        switch (position) {
            case LEFT:
                mBubbleUpDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_bubble_up_left);
                mBubbleDownDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_bubble_down_left);
                break;
            case RIGHT:
                mBubbleUpDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_bubble_up_right);
                mBubbleDownDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_bubble_down_right);
                break;
        }
    }

    private void initAnimators() {
        ValueAnimator.AnimatorUpdateListener scrollbarAnimator = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) animation.getAnimatedValue();
                int right = (int) (left + mScrollbarPaint.getStrokeWidth());
                mScrollbarBounds.set(left, mScrollbarBounds.top, right, mScrollbarBounds.bottom);
                invalidate();
            }
        };
        mShowScrollbarAnimator.setDuration(mAnimDuration);
        mShowScrollbarAnimator.addUpdateListener(scrollbarAnimator);
        mShowScrollbarAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsScrollbarVisible = true;
            }
        });
        mHideScrollbarAnimator.setDuration(mAnimDuration);
        mHideScrollbarAnimator.addUpdateListener(scrollbarAnimator);
        mHideScrollbarAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsScrollbarVisible = false;
            }
        });

        ValueAnimator.AnimatorUpdateListener trackAnimator = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int left = (int) animation.getAnimatedValue();
                int right = left + mTrackDrawable.getIntrinsicWidth();
                Rect bounds = mTrackDrawable.getBounds();
                mTrackDrawable.setBounds(left, bounds.top, right, bounds.bottom);
                invalidate();
            }
        };
        mShowTrackAnimator.setDuration(mAnimDuration);
        mShowTrackAnimator.addUpdateListener(trackAnimator);
        mShowTrackAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsTrackVisible = true;
            }
        });
        mHideTrackAnimator.setDuration(mAnimDuration);
        mHideTrackAnimator.addUpdateListener(trackAnimator);
        mHideTrackAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsTrackVisible = false;
            }
        });

        ValueAnimator.AnimatorUpdateListener bubbleAnimator = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int alpha = (int) animation.getAnimatedValue();
                mBubbleUpDrawable.setAlpha(alpha);
                mBubbleDownDrawable.setAlpha(alpha);
                invalidate();
            }
        };
        mShowBubbleAnimator.setDuration(mBubbleAnimDuration);
        mShowBubbleAnimator.addUpdateListener(bubbleAnimator);
        mShowBubbleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsBubbleVisible = true;
            }
        });
        mHideBubbleAnimator.setDuration(mBubbleAnimDuration);
        mHideBubbleAnimator.addUpdateListener(bubbleAnimator);
        mHideBubbleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsBubbleVisible = false;
            }
        });
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        if (isShowScrollbar()) {
            boolean isAutoHideScrollbar = isAutoHideScrollbar();
            if (isAutoHideScrollbar) {
                mScrollbarBounds.set(mScrollbarBounds.left, 0, mScrollbarBounds.right, height);
                return;
            }

            int scrollbarWidth = (int) mScrollbarPaint.getStrokeWidth();
            int left = 0;
            int right = scrollbarWidth;
            if (mPosition == RIGHT) {
                right = getWidth();
                left = right - scrollbarWidth;
            }
            mScrollbarBounds.set(left, 0, right, height);
        }
        if (isShowTrack() && !isAutoHideTrack()) {
            Rect bounds = mTrackDrawable.getBounds();
            int trackWidth = mTrackDrawable.getIntrinsicWidth();

            int left = 0;
            int right = 0;
            switch (mPosition) {
                case LEFT:
                    left = mTrackPaddingLeft;
                    right = left + trackWidth;
                    break;
                case RIGHT:
                    left = width - mTrackPaddingRight - trackWidth;
                    right = left + trackWidth;
                    break;
            }
            mTrackDrawable.setBounds(left, bounds.top, right, bounds.bottom);
            onScrolled();
        }
        if (isShowBubble() && (!isShowTrack() || !isAutoHideTrack())) onScrolled();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof FastScrollAdapter) {
            mFastScrollAdapter = (FastScrollAdapter) adapter;
        } else if (adapter == null) {
            mFastScrollAdapter = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mShowBubbleAnimator.cancel();
        mShowScrollbarAnimator.cancel();
        mShowTrackAnimator.cancel();
        mHideBubbleAnimator.cancel();
        mHideScrollbarAnimator.cancel();
        mHideTrackAnimator.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || getVisibility() != VISIBLE) return super.onTouchEvent(event);
        if (!isShowScrollbar() && !isShowTrack() && !isShowBubble()) return super.onTouchEvent(event);

        int action = event.getAction();
        float eventX = event.getX();
        float eventY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsScrollbarTouched = isScrollbarTouched(eventX);
                mIsTrackTouched = isTrackTouched(eventX, eventY);
                mIsBubbleTouched = isBubbleTouched(eventX, eventY);
                if (mIsTrackTouched || mIsScrollbarTouched || mIsBubbleTouched) {
                    mLastEventY = eventY;
                    requestDisallowInterceptTouchEvent(true);
                    removeOnScrollListener(mScrollListener);
                    removeCallbacks(mHiderRunnable);

                    if (mIsTrackTouched) {
                        showBubble();
                    } else if (!mIsBubbleTouched) {
                        updateRecyclerViewPosition(eventY);
                        onScrolled(eventY);
                    }
                    showScrollbar();
                    showTrack();
                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                if (mIsTrackTouched || mIsScrollbarTouched || mIsBubbleTouched) {
                    float diff = eventY - mLastEventY;
                    Rect bounds = mTrackDrawable.getBounds();
                    float y = bounds.top + diff;
                    mLastEventY = eventY;
                    y = Math.min(getHeight(), Math.max(0, y));
                    updateRecyclerViewPosition(isReverseLayout(this) ? y + bounds.height() : y);
                    onScrolled(y);
                    return true;
                }
            case MotionEvent.ACTION_UP:
                if (mIsTrackTouched || mIsScrollbarTouched || mIsBubbleTouched) {
                    postDelayed(mHiderRunnable, mHideTrackDelay);
                    requestDisallowInterceptTouchEvent(false);
                    mIsTrackTouched = isShowTrack() && !isAutoHideTrack();
                    mIsScrollbarTouched = isShowScrollbar() && !isAutoHideScrollbar();
                    mIsBubbleTouched = isShowBubble() && !isAutoHideBubble();
                    invalidate();
                    addOnScrollListener(mScrollListener);
                    hideBubble();
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    private boolean isTrackTouched(float eventX, float eventY) {
        Rect bounds = mTrackDrawable.getBounds();
        int left = bounds.left - mTrackPaddingLeft;
        int right = bounds.right + mTrackPaddingRight;
        int top = bounds.top + mTrackPaddingTop;
        int bottom = bounds.bottom + mTrackPaddingBottom;
        return left <= eventX && eventX <= right && top <= eventY && eventY <= bottom;
    }

    private boolean isScrollbarTouched(float eventX) {
        return eventX >= mScrollbarBounds.left && eventX <= mScrollbarBounds.right;
    }

    private boolean isBubbleTouched(float eventX, float eventY) {
        if (mBubbleDrawable == null) return false;
        Rect bounds = mBubbleDrawable.getBounds();
        return bounds.left <= eventX && eventX <= bounds.right && bounds.top <= eventY && eventY <= bounds.bottom;
    }

    private void updateRecyclerViewPosition(float eventY) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        RecyclerView.Adapter adapter = getAdapter();
        if (layoutManager == null || adapter == null) return;

        int itemCount = adapter.getItemCount();
        float positionPercent = (eventY * 100) / getHeight();
        int newPosition = Math.round((itemCount * positionPercent) / 100);
        if (isReverseLayout(this)) newPosition = itemCount - newPosition;

        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(newPosition, 0);
        } else {
            layoutManager.scrollToPosition(newPosition);
        }
    }

    private void onScrollScreenStateChanged(int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
            case RecyclerView.SCROLL_STATE_SETTLING:
                removeCallbacks(mHiderRunnable);
                showScrollbar();
                showTrack();
                showBubble();
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                postDelayed(mHiderRunnable, mHideTrackDelay);
                break;
        }
    }

    private void showTrack() {
        if (!isShowTrack() || !isAutoHideTrack()) return;
        Rect bounds = mTrackDrawable.getBounds();
        int trackWidth = mTrackDrawable.getIntrinsicWidth();
        int from = 0;
        int to = 0;
        switch (mPosition) {
            case LEFT:
                from = bounds.right == 0 ? -trackWidth : bounds.left;
                to = mTrackPaddingLeft;
                break;
            case RIGHT:
                from = bounds.right == 0 ? getWidth() : bounds.left;
                to = getWidth() - mTrackPaddingRight - trackWidth;
                break;
        }
        mHideTrackAnimator.cancel();
        mShowTrackAnimator.cancel();
        mShowTrackAnimator.setIntValues(from, to);
        mShowTrackAnimator.start();
    }

    private void hideTrack() {
        if (!isShowTrack() || !isAutoHideTrack()) return;
        Rect bounds = mTrackDrawable.getBounds();
        int from = 0;
        int to = 0;
        switch (mPosition) {
            case LEFT:
                from = bounds.left;
                to = -mTrackDrawable.getIntrinsicWidth();
                break;
            case RIGHT:
                from = bounds.left;
                to = getWidth();
                break;
        }
        mShowTrackAnimator.cancel();
        mHideTrackAnimator.cancel();
        mHideTrackAnimator.setIntValues(from, to);
        mHideTrackAnimator.start();
    }

    private void showScrollbar() {
        if (!isShowScrollbar() || !isAutoHideScrollbar()) return;
        int scrollbarWidth = (int) mScrollbarPaint.getStrokeWidth();
        Rect bounds = mScrollbarBounds;
        int from = 0;
        int to = 0;
        switch (mPosition) {
            case LEFT:
                from = bounds.right == 0 ? -scrollbarWidth : bounds.left;
                to = 0;
                break;
            case RIGHT:
                from = bounds.right == 0 ? getWidth() : bounds.left;
                to = getWidth() - scrollbarWidth;
                break;
        }
        mHideScrollbarAnimator.cancel();
        mShowScrollbarAnimator.cancel();
        mShowScrollbarAnimator.setIntValues(from, to);
        mShowScrollbarAnimator.start();
    }

    private void hideScrollbar() {
        if (!isShowScrollbar() || !isAutoHideScrollbar()) return;
        Rect bounds = mScrollbarBounds;
        int from = 0;
        int to = 0;
        switch (mPosition) {
            case LEFT:
                from = bounds.left;
                to = (int) -mScrollbarPaint.getStrokeWidth();
                break;
            case RIGHT:
                from = bounds.left;
                to = getWidth();
                break;
        }
        mShowScrollbarAnimator.cancel();
        mHideScrollbarAnimator.cancel();
        mHideScrollbarAnimator.setIntValues(from, to);
        mHideScrollbarAnimator.start();
    }

    private void showBubble() {
        if (mBubbleDrawable == null) return;
        if (!isShowBubble() || !isAutoHideBubble() || mIsBubbleVisible || !mIsTrackTouched) return;
        int startAlpha = DrawableUtils.getAlpha(mBubbleDrawable);
        if (startAlpha == MAX_ALPHA) startAlpha = MIN_ALPHA;
        mHideBubbleAnimator.cancel();
        mShowBubbleAnimator.cancel();
        mShowBubbleAnimator.setIntValues(startAlpha, MAX_ALPHA);
        mShowBubbleAnimator.start();
    }

    private void hideBubble() {
        if (mBubbleDrawable == null) return;
        if (!isShowBubble() || !isAutoHideBubble() || !mIsBubbleVisible) return;
        mShowBubbleAnimator.cancel();
        mHideBubbleAnimator.cancel();
        mHideBubbleAnimator.setIntValues(DrawableUtils.getAlpha(mBubbleDrawable), MIN_ALPHA);
        mHideBubbleAnimator.start();
    }

    private void onScrolled() {
        int width = getWidth();
        int height = getHeight();

        int scrollPosition = getScrollPosition();
        updateTrackPosition(height, width, scrollPosition);
        updateBubblePosition(height, width, scrollPosition);
        invalidate();
    }

    private int getScrollPosition() {
        int height = getHeight();
        int scrollRange = computeVerticalScrollRange() - height;
        int scrollOffset = computeVerticalScrollOffset();
        double scrollPercents = (scrollOffset == 0) ? 0 : (scrollOffset * 100.0 / scrollRange);
        height = height - mTrackPaddingTop - mTrackPaddingBottom - mTrackDrawable.getIntrinsicHeight();
        if (isReverseLayout(this) && scrollRange < 0) return height;
        return (int) (mTrackPaddingTop + ((height * scrollPercents) / 100));
    }

    private void onScrolled(float eventY) {
        int width = getWidth();
        int height = getHeight();
        int scrollPosition = (int) eventY;
        updateTrackPosition(height, width, scrollPosition);
        updateBubblePosition(height, width, scrollPosition);
        invalidate();
    }

    private void updateTrackPosition(int screenHeight, int screenWidth, int scrollPosition) {
        boolean isAnimationRunning = mShowTrackAnimator.isRunning() || mHideTrackAnimator.isRunning();

        Rect bounds = mTrackDrawable.getBounds();
        int trackWidth = mTrackDrawable.getIntrinsicWidth();
        int trackHeight = mTrackDrawable.getIntrinsicHeight();

        int formattedTop = scrollPosition < mTrackPaddingTop ? mTrackPaddingTop : scrollPosition;
        if (formattedTop + trackHeight + mTrackPaddingBottom >= screenHeight) {
            formattedTop = screenHeight - trackHeight - mTrackPaddingBottom;
        }
        int bottom = formattedTop + trackHeight;

        int left = 0;
        int right = 0;
        switch (mPosition) {
            case LEFT:
                left = isAnimationRunning ? bounds.left : mTrackPaddingLeft;
                right = isAnimationRunning ? bounds.right : mTrackPaddingLeft + trackWidth;
                break;
            case RIGHT:
                left = isAnimationRunning ? bounds.left : (screenWidth - mTrackPaddingRight - trackWidth);
                right = isAnimationRunning ? bounds.right : (screenWidth - mTrackPaddingRight);
                break;
        }
        mTrackDrawable.setBounds(left, formattedTop, right, bottom);
    }

    private void updateBubblePosition(int screenHeight, int screenWidth, int scrollPosition) {
        int halfScreenHeight = screenHeight / 2;
        boolean isHalfScrolled = scrollPosition + (mTrackDrawable.getIntrinsicHeight() / 2) >= halfScreenHeight;
        mBubbleDrawable = isHalfScrolled ? mBubbleDownDrawable : mBubbleUpDrawable;

        int intrinsicWidth = mBubbleDrawable.getIntrinsicWidth();
        int intrinsicHeight = mBubbleDrawable.getIntrinsicHeight();
        int trackCenterY = mTrackDrawable.getBounds().centerY();
        int trackWidth = mTrackDrawable.getIntrinsicWidth() + mTrackPaddingLeft + mTrackPaddingRight;

        int top = isHalfScrolled ? trackCenterY - intrinsicHeight : trackCenterY;
        int bottom = top + intrinsicHeight;
        switch (mPosition) {
            case LEFT:
                mBubbleDrawable.setBounds(trackWidth, top, trackWidth + intrinsicWidth, bottom);
                break;
            case RIGHT:
                mBubbleDrawable.setBounds(screenWidth - trackWidth - intrinsicWidth, top, screenWidth - trackWidth, bottom);
                break;
        }
    }

    private boolean isShowScrollbar() {
        return (mElementsVisibility & FastScrollVisibility.SCROLLBAR) == FastScrollVisibility.SCROLLBAR;
    }

    private boolean isShowTrack() {
        return (mElementsVisibility & FastScrollVisibility.TRACK) == FastScrollVisibility.TRACK;
    }

    private boolean isShowBubble() {
        return (mElementsVisibility & FastScrollVisibility.BUBBLE) == FastScrollVisibility.BUBBLE;
    }

    private boolean isAutoHideScrollbar() {
        return (mAutoHide & FastScrollAutoHide.SCROLLBAR) == FastScrollAutoHide.SCROLLBAR;
    }

    private boolean isAutoHideTrack() {
        return (mAutoHide & FastScrollAutoHide.TRACK) == FastScrollAutoHide.TRACK;
    }

    private boolean isAutoHideBubble() {
        return (mAutoHide & FastScrollAutoHide.BUBBLE) == FastScrollAutoHide.BUBBLE;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter == null || adapter.getItemCount() == 0) return;

        if (isShowScrollbar()) canvas.drawRect(mScrollbarBounds, mScrollbarPaint);
        if (isShowTrack()) mTrackDrawable.draw(canvas);
        if (isShowBubble() && mBubbleDrawable != null) {
            mBubbleDrawable.draw(canvas);
            if (mIsBubbleVisible || mShowBubbleAnimator.isRunning() || mHideBubbleAnimator.isRunning()) {
                drawBubbleContent(canvas);
            }
        }
    }

    private void drawBubbleContent(Canvas canvas) {
        if (mFastScrollAdapter == null) return;
        LayoutManager layoutManager = getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) return;
        int position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        if (position == RecyclerView.NO_POSITION) return;

        if (mFastScrollAdapter instanceof FastScrollAdapter.Text) {
            drawText(canvas, ((FastScrollAdapter.Text) mFastScrollAdapter).getText(position));
        } else if (mFastScrollAdapter instanceof FastScrollAdapter.ViewHolder) {
            drawGroupViewHolder(canvas, (FastScrollAdapter.ViewHolder) mFastScrollAdapter, position);
        }
    }

    private void drawText(Canvas canvas, @Nullable String text) {
        if (text == null || mBubbleDrawable == null) return;
        Rect bubbleBounds = mBubbleDrawable.getBounds();
        int centerY = bubbleBounds.centerY();
        int centerX = bubbleBounds.centerX();
        mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);
        mTextPaint.setAlpha(DrawableUtils.getAlpha(mBubbleDrawable));
        canvas.drawText(text, centerX, centerY + (mTextBounds.height() / 2F), mTextPaint);
    }

    @SuppressWarnings("unchecked")
    private void drawGroupViewHolder(Canvas canvas, FastScrollAdapter.ViewHolder adapter, int position) {
        if (mBubbleDrawable == null) return;
        Rect bounds = mBubbleDrawable.getBounds();

        assert mFastScrollAdapter != null;
        long groupId = adapter.getFastScrollGroupId(position);
        RecyclerView.ViewHolder viewHolder = mHolderSparseArray.get((int) groupId);
        if (viewHolder == null) {
            viewHolder = adapter.onCreateFastScrollViewHolder(this);
            if (viewHolder == null) return;

            mHolderSparseArray.put((int) groupId, viewHolder);
            View itemView = viewHolder.itemView;

            int width = mBubbleDrawable.getIntrinsicWidth();
            int height = mBubbleUpDrawable.getIntrinsicHeight();
            int widthSpec = MeasureSpec.makeMeasureSpec(width, EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(height, EXACTLY);
            itemView.measure(widthSpec, heightSpec);
            itemView.layout(0, 0, width, height);
        }
        adapter.onBindFastScrollViewHolder(viewHolder, position);
        View itemView = viewHolder.itemView;

        canvas.save();
        int alpha = DrawableUtils.getAlpha(mBubbleDrawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, alpha);
        } else {
            Drawable backgroundDrawable = itemView.getBackground();
            if (backgroundDrawable != null) backgroundDrawable.setAlpha(alpha);
        }
        canvas.translate(bounds.left, bounds.top);
        itemView.setTranslationX(bounds.left);
        itemView.setTranslationY(bounds.top);
        itemView.draw(canvas);
        canvas.restore();
    }

}