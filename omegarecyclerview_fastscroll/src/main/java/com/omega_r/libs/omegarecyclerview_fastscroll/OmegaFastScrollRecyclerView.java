package com.omega_r.libs.omegarecyclerview_fastscroll;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

import static com.omega_r.libs.omegarecyclerview_fastscroll.DrawableUtils.getTintedDrawable;
import static com.omega_r.libs.omegarecyclerview_fastscroll.Position.LEFT;
import static com.omega_r.libs.omegarecyclerview_fastscroll.Position.RIGHT;

public class OmegaFastScrollRecyclerView extends OmegaRecyclerView {

    private static final String TAG = OmegaFastScrollRecyclerView.class.getSimpleName();
    private static final long ANIM_DURATION_IN_MILLIS = 300;
    private static final long TRACK_HIDE_DELAY_IN_MILLIS = 1000;

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

    private Runnable mHiderRunnable = new Runnable() {
        @Override
        public void run() {
            cancelAllAnimations();
            if (isShowTrack() && isAutoHideTrack()) hideTrack();
            if (isShowScrollbar() && isAutoHideScrollbar()) hideScrollbar();
        }
    };

    @Nullable
    private FastScrollAdapter mFastScrollAdapter;
    private Drawable mTrackDrawable;
    private Drawable mBubbleUpDrawable;
    private Drawable mBubbleDownDrawable;
    @Nullable
    private Drawable mBubbleDrawable;

    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mTextBounds = new Rect();
    private String mSectionText;

    private Position mPosition = RIGHT;

    private int mTrackPaddingLeft;
    private int mTrackPaddingTop;
    private int mTrackPaddingRight;
    private int mTrackPaddingBottom;

    private final Rect mScrollbarBounds = new Rect();
    private final Paint mScrollbarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final ValueAnimator mShowTrackAnimator = ValueAnimator.ofInt().setDuration(ANIM_DURATION_IN_MILLIS);
    private final ValueAnimator mHideTrackAnimator = ValueAnimator.ofInt().setDuration(ANIM_DURATION_IN_MILLIS);
    private final ValueAnimator mShowScrollbarAnimator = ValueAnimator.ofInt().setDuration(ANIM_DURATION_IN_MILLIS);
    private final ValueAnimator mHideScrollbarAnimator = ValueAnimator.ofInt().setDuration(ANIM_DURATION_IN_MILLIS);

    private int mElementsVisibility = FastScrollVisibility.NONE;
    private int mAutoHide = FastScrollAutoHide.NONE;

    private boolean mIsScrollbarVisible;
    private boolean mIsTrackVisible;
    private boolean mIsTrackTouched;

    private float mActionDownYDiff;

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
        mBubbleUpDrawable = ContextCompat.getDrawable(context, R.drawable.ic_bubble_up);
        mBubbleDownDrawable = ContextCompat.getDrawable(context, R.drawable.ic_bubble_down);

        mTrackPaddingLeft = getResources().getDimensionPixelSize(R.dimen.omega_track_padding_left);
        mTrackPaddingTop = getResources().getDimensionPixelSize(R.dimen.omega_track_padding_top);
        mTrackPaddingRight = getResources().getDimensionPixelSize(R.dimen.omega_track_padding_right);
        mTrackPaddingBottom = getResources().getDimensionPixelSize(R.dimen.omega_track_padding_bottom);

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

        initAnimators();
        addOnScrollListener(mScrollListener);
        setWillNotDraw(false);
    }

    private void initAttrs(@NonNull TypedArray typedArray) {
        Drawable trackDrawable = typedArray.getDrawable(R.styleable.OmegaFastScrollRecyclerView_trackDrawable);
        if (trackDrawable != null) mTrackDrawable = trackDrawable;
        int trackColor = typedArray.getColor(R.styleable.OmegaFastScrollRecyclerView_omega_trackColor, Color.TRANSPARENT);
        if (trackColor != Color.TRANSPARENT) mTrackDrawable = getTintedDrawable(mTrackDrawable, trackColor);

        Drawable bubbleUp = typedArray.getDrawable(R.styleable.OmegaFastScrollRecyclerView_bubbleUpDrawable);
        if (bubbleUp != null) mBubbleUpDrawable = bubbleUp;
        int bubbleUpColor = typedArray.getColor(R.styleable.OmegaFastScrollRecyclerView_bubbleUpColor, Color.TRANSPARENT);
        if (bubbleUpColor != Color.TRANSPARENT) mBubbleUpDrawable = getTintedDrawable(mBubbleUpDrawable, bubbleUpColor);

        Drawable bubbleDown = typedArray.getDrawable(R.styleable.OmegaFastScrollRecyclerView_bubbleDownDrawable);
        if (bubbleDown != null) mBubbleDownDrawable = bubbleDown;
        int bubbleDownColor = typedArray.getColor(R.styleable.OmegaFastScrollRecyclerView_bubbleDownColor, Color.TRANSPARENT);
        if (bubbleUpColor != Color.TRANSPARENT) mBubbleDownDrawable = getTintedDrawable(mBubbleDownDrawable, bubbleDownColor);

        int position = typedArray.getInt(R.styleable.OmegaFastScrollRecyclerView_position, 0);
        if (position == 0) {
            mPosition = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL ? LEFT : RIGHT;
        } else {
            mPosition = Position.values()[position];
        }

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
        mShowScrollbarAnimator.addUpdateListener(scrollbarAnimator);
        mShowScrollbarAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsScrollbarVisible = true;
            }
        });
        mHideScrollbarAnimator.addUpdateListener(scrollbarAnimator);
        mHideScrollbarAnimator.addListener(new AnimatorListener() {
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
        mShowTrackAnimator.addUpdateListener(trackAnimator);
        mShowTrackAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsTrackVisible = true;
            }
        });
        mHideTrackAnimator.addUpdateListener(trackAnimator);
        mHideTrackAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsTrackVisible = false;
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
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof FastScrollAdapter) {
            setSectionIndexer((FastScrollAdapter) adapter);
        } else if (adapter == null) {
            setSectionIndexer(null);
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || getVisibility() != VISIBLE) return super.onTouchEvent(event);

        int action = event.getAction();
        float eventX = event.getX();
        float eventY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                boolean isScrollbarTouched = isScrollbarTouched(eventX);
                boolean isTrackTouched = isTrackTouched(eventX, eventY);
                if (isShowScrollbar() || isShowTrack()) {
                    mIsTrackTouched = isTrackTouched || isScrollbarTouched;
                    if (mIsTrackTouched) {
                        mActionDownYDiff = isTrackTouched ? mTrackDrawable.getBounds().top - eventY : 0;
                        requestDisallowInterceptTouchEvent(true);
                        if (!isTrackTouched) {
                            removeOnScrollListener(mScrollListener);
                            int halfHeight = mTrackDrawable.getIntrinsicHeight() / 2;
                            updateRecyclerViewPosition(eventY - halfHeight);
                            onScrolled(eventY - halfHeight);
                        }
                        return true;
                    }
                }
            case MotionEvent.ACTION_MOVE:
                if (mIsTrackTouched) {
                    removeOnScrollListener(mScrollListener);
                    float y = Math.min(getHeight(), Math.max(0, eventY + mActionDownYDiff));
                    updateRecyclerViewPosition(y);
                    onScrolled(y);
                    return true;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsTrackTouched) {
                    postDelayed(mHiderRunnable, TRACK_HIDE_DELAY_IN_MILLIS);
                    requestDisallowInterceptTouchEvent(false);
                    mIsTrackTouched = false;
                    invalidate();
                    addOnScrollListener(mScrollListener);
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

    private void updateRecyclerViewPosition(float eventY) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        RecyclerView.Adapter adapter = getAdapter();
        if (layoutManager == null || adapter == null) return;

        int itemCount = adapter.getItemCount();
        float positionPercent = (eventY * 100) / getHeight();
        int newPosition = (int) ((itemCount * positionPercent) / 100);

        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(newPosition, 0);
        } else {
            layoutManager.scrollToPosition(newPosition);
        }
    }

    private boolean isTrackVisible() {
        return mIsTrackVisible || mShowTrackAnimator.isRunning();
    }

    private boolean isScrollbarVisible() {
        return mIsScrollbarVisible || mShowScrollbarAnimator.isRunning();
    }

    public final void setSectionIndexer(FastScrollAdapter adapter) {
        mFastScrollAdapter = adapter;
        invalidate();
    }

    private void onScrollScreenStateChanged(int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                removeCallbacks(mHiderRunnable);
                cancelAllAnimations();
                if (isShowScrollbar() && isAutoHideScrollbar() && !isScrollbarVisible()) showScrollbar();
                if (isShowTrack() && isAutoHideTrack() && !isTrackVisible()) showTrack();
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                postDelayed(mHiderRunnable, TRACK_HIDE_DELAY_IN_MILLIS);
                break;
        }
    }

    private void cancelAllAnimations() {
        cancelAnimation(mShowTrackAnimator, mHideTrackAnimator, mShowScrollbarAnimator, mHideScrollbarAnimator);
    }

    private void cancelAnimation(Animator... animators) {
        for (Animator animator : animators) animator.cancel();
    }

    private void showTrack() {
        Rect bounds = mTrackDrawable.getBounds();
        int trackWidth = mTrackDrawable.getIntrinsicWidth();
        int from = 0;
        int to = 0;
        switch (mPosition) {
            case LEFT:
                from = bounds.left == 0 ? -trackWidth : bounds.left;
                to = mTrackPaddingLeft;
                break;
            case RIGHT:
                from = bounds.right == 0 ? getWidth() : bounds.left;
                to = getWidth() - mTrackPaddingRight - trackWidth;
                break;
        }
        mShowTrackAnimator.setIntValues(from, to);
        mShowTrackAnimator.start();
    }

    private void hideTrack() {
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
        mHideTrackAnimator.setIntValues(from, to);
        mHideTrackAnimator.start();
    }

    private void showScrollbar() {
        int scrollbarWidth = (int) mScrollbarPaint.getStrokeWidth();
        Rect bounds = mScrollbarBounds;
        int from = 0;
        int to = 0;
        switch (mPosition) {
            case LEFT:
                from = bounds.left == 0 ? -scrollbarWidth : bounds.left;
                to = 0;
                break;
            case RIGHT:
                from = bounds.right == 0 ? getWidth() : bounds.right;
                to = getWidth() - scrollbarWidth;
                break;
        }
        mShowScrollbarAnimator.setIntValues(from, to);
        mShowScrollbarAnimator.start();
    }

    private void hideScrollbar() {
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
        mHideScrollbarAnimator.setIntValues(from, to);
        mHideScrollbarAnimator.start();
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
        boolean isAnimationRunning = mShowTrackAnimator != null && mShowTrackAnimator.isRunning() ||
                mHideTrackAnimator != null && mHideTrackAnimator.isRunning();

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

    private void updateSectionText(int adapterPosition) {
        if (mFastScrollAdapter == null || mBubbleDrawable == null || adapterPosition < 0) return;
        mSectionText = "T";
        mFastScrollAdapter.getFastScrollSection(adapterPosition);
        if (mSectionText == null) return;
        Rect mBubbleBounds = mBubbleDrawable.getBounds();
        int centerY = mBubbleBounds.centerY();
        int centerX = mBubbleBounds.centerX();
        mTextPaint.getTextBounds(mSectionText, 0, mSectionText.length() - 1, mTextBounds);
        int halfTextWidth = mTextBounds.width() / 2;
        int halfTextHeight = mTextBounds.height() / 2;
        mTextBounds.set(centerX - halfTextWidth, centerY - halfTextHeight, centerX + halfTextWidth, centerY + halfTextHeight);
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

        if (isShowScrollbar()) {
            canvas.drawRect(mScrollbarBounds, mScrollbarPaint);
        }
        if (isShowTrack()) {
            mTrackDrawable.draw(canvas);
        }
        if (isShowBubble() && mBubbleDrawable != null) {
            mBubbleDrawable.draw(canvas);
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        LayoutManager layoutManager = getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) return;
        if (mFastScrollAdapter == null) return;
        int position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        if (position == RecyclerView.NO_POSITION) return;

        mFastScrollAdapter.getFastScrollSection(position);


    }

}