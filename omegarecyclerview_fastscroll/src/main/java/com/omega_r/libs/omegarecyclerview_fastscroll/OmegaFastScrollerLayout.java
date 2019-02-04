package com.omega_r.libs.omegarecyclerview_fastscroll;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OmegaFastScrollerLayout extends LinearLayout {

    private static final AnimatorListener sDefaultAnimatorListener = new AnimatorListener();
    private static final long BUBBLE_ANIM_DURATION_IN_MILLIS = 100;
    private static final long SCROLLBAR_ANIM_DURATION_IN_MILLIS = 300;
    private static final long SCROLLBAR_HIDE_DELAY_IN_MILLIS = 1000;
    private static final long TRACK_SNAP_RANGE = 5;

    private RecyclerView mRecyclerView;
    private TextView mBubbleTextView;
    private ImageView mHandleImageView;
    private ImageView mTrackImageView;
    private View mScrollbarContainer;
    private Drawable mBubbleImageUpDrawable;
    private Drawable mBubbleImageDownDrawable;
    private Drawable mHandleImageDrawable;
    private Drawable mTrackImageDrawable;

    @ColorInt
    private int mBubbleColor;
    @ColorInt
    private int mHandleColor;
    private int mBubbleHeight;
    private int mHandleHeight;
    private int mViewHeight;
    private boolean mHideScrollbar;
    private boolean mShowBubble;
    private SectionAdapter mSectionAdapter;
    private ViewPropertyAnimator mScrollbarAnimator;
    private ViewPropertyAnimator mBubbleAnimator;
    private FastScrollStateChangeListener mFastScrollStateChangeListener;
    private final Runnable mScrollbarHider = new Runnable() {
        @Override
        public void run() {
            hideScrollbar();
        }
    };
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (!mHandleImageView.isSelected() && isEnabled()) {
                setViewPositions(getScrollProportion(recyclerView));
            }
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (!isEnabled()) return;
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    getHandler().removeCallbacks(mScrollbarHider);
                    cancelAnimation(mScrollbarAnimator);
                    if (!isViewVisible(mScrollbarContainer)) showScrollbar();
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (mHideScrollbar && !mHandleImageView.isSelected()) {
                        getHandler().postDelayed(mScrollbarHider, SCROLLBAR_HIDE_DELAY_IN_MILLIS);
                    }
                    break;
            }
        }
    };
    private int mScrollbarPaddingEnd;
    private Animator.AnimatorListener mHideScrollbarListener = new AnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animation) {
            onAnimationCancel(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mScrollbarContainer.setVisibility(View.GONE);
            mScrollbarAnimator = null;
        }
    };
    private Animator.AnimatorListener mHideBubbleListener = new AnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animation) {
            onAnimationCancel(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mBubbleTextView.setVisibility(View.GONE);
            mBubbleAnimator = null;
        }
    };

    public OmegaFastScrollerLayout(Context context) {
        super(context);
        init(context, null);
    }

    public OmegaFastScrollerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OmegaFastScrollerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OmegaFastScrollerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.view_fastscroller, this);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        setClipChildren(false);
        setOrientation(HORIZONTAL);

        mScrollbarPaddingEnd = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end);
        mBubbleTextView = findViewById(R.id.textview_bubble);
        mHandleImageView = findViewById(R.id.imageview_handle);
        mTrackImageView = findViewById(R.id.imageview_track);
        mScrollbarContainer = findViewById(R.id.scrollbar_container);

        @ColorInt int bubbleColor = Color.GRAY;
        @ColorInt int handleColor = Color.DKGRAY;
        @ColorInt int trackColor = Color.LTGRAY;
        @ColorInt int textColor = Color.WHITE;

        boolean hideScrollbar = true;
        boolean showBubble = true;
        boolean showTrack = false;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OmegaFastScrollerLayout, 0, 0);

            bubbleColor = typedArray.getColor(R.styleable.OmegaFastScrollerLayout_bubbleColor, bubbleColor);
            handleColor = typedArray.getColor(R.styleable.OmegaFastScrollerLayout_handleColor, handleColor);
            trackColor = typedArray.getColor(R.styleable.OmegaFastScrollerLayout_trackColor, trackColor);
            textColor = typedArray.getColor(R.styleable.OmegaFastScrollerLayout_bubbleTextColor, textColor);
            hideScrollbar = typedArray.getBoolean(R.styleable.OmegaFastScrollerLayout_hideScrollbar, hideScrollbar);
            showBubble = typedArray.getBoolean(R.styleable.OmegaFastScrollerLayout_showBubble, showBubble);
            showTrack = typedArray.getBoolean(R.styleable.OmegaFastScrollerLayout_showTrack, showTrack);
            typedArray.recycle();
        }
        mTrackImageDrawable = ContextCompat.getDrawable(context, R.drawable.ic_track);
        mHandleImageDrawable = ContextCompat.getDrawable(context, R.drawable.ic_handle);
        mBubbleImageUpDrawable = ContextCompat.getDrawable(context, R.drawable.ic_bubble_up);
        mBubbleImageDownDrawable = ContextCompat.getDrawable(context, R.drawable.ic_bubble_down);

        setTrackColor(trackColor);
        setHandleColor(handleColor);
        setBubbleColor(bubbleColor);
        setBubbleTextColor(textColor);
        setHideScrollbar(hideScrollbar);
        setBubbleVisible(showBubble);
        setTrackVisible(showTrack);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.width = LayoutParams.WRAP_CONTENT;
        super.setLayoutParams(params);
    }

    final void setLayoutParams(ViewGroup viewGroup) {
        @IdRes int recyclerViewId = mRecyclerView == null ? View.NO_ID : mRecyclerView.getId();

        if (recyclerViewId == View.NO_ID) {
            throw new IllegalArgumentException("RecyclerView must have a view ID");
        }

        int marginTop = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_margin_top);
        int marginBottom = getResources().getDimensionPixelSize(R.dimen.fastscroll_scrollbar_margin_bottom);

        if (viewGroup instanceof ConstraintLayout) {
            ConstraintSet constraintSet = new ConstraintSet();
            int id = getId();
            constraintSet.clone((ConstraintLayout) viewGroup);
            constraintSet.connect(id, ConstraintSet.TOP, recyclerViewId, ConstraintSet.TOP);
            constraintSet.connect(id, ConstraintSet.BOTTOM, recyclerViewId, ConstraintSet.BOTTOM);
            constraintSet.connect(id, ConstraintSet.END, recyclerViewId, ConstraintSet.END);
            constraintSet.applyTo((ConstraintLayout) viewGroup);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) getLayoutParams();
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);
        } else if (viewGroup instanceof CoordinatorLayout) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) getLayoutParams();
            layoutParams.setAnchorId(recyclerViewId);
            layoutParams.anchorGravity = GravityCompat.END;
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);
        } else if (viewGroup instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            layoutParams.gravity = GravityCompat.END;
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);
        } else if (viewGroup instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            int endRule = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                    RelativeLayout.ALIGN_END : RelativeLayout.ALIGN_RIGHT;

            layoutParams.addRule(RelativeLayout.ALIGN_TOP, recyclerViewId);
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, recyclerViewId);
            layoutParams.addRule(endRule, recyclerViewId);
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
            setLayoutParams(layoutParams);
        } else {
            throw new IllegalArgumentException("Parent ViewGroup must be a ConstraintLayout, " +
                    "CoordinatorLayout, FrameLayout, or RelativeLayout");
        }
        updateViewHeights();
    }

    private void updateViewHeights() {
        int measureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        mBubbleTextView.measure(measureSpec, measureSpec);
        mBubbleHeight = mBubbleTextView.getMeasuredHeight();
        mHandleImageView.measure(measureSpec, measureSpec);
        mHandleHeight = mHandleImageView.getMeasuredHeight();
    }

    public final void setSectionIndexer(@Nullable SectionAdapter sectionAdapter) {
        mSectionAdapter = sectionAdapter;
    }

    public final void attachRecyclerView(RecyclerView recyclerView) {
        detachRecyclerView();
        this.mRecyclerView = recyclerView;
        if (this.mRecyclerView != null) {
            this.mRecyclerView.addOnScrollListener(mScrollListener);
            post(new Runnable() {
                @Override
                public void run() {
                    // set initial positions for bubble and handle
                    setViewPositions(getScrollProportion(OmegaFastScrollerLayout.this.mRecyclerView));
                }
            });
        }
    }

    public final void detachRecyclerView() {
        if (mRecyclerView != null) {
            mRecyclerView.removeOnScrollListener(mScrollListener);
            mRecyclerView = null;
        }
    }

    public final void setHideScrollbar(boolean hideScrollbar) {
        this.mHideScrollbar = hideScrollbar;
        mScrollbarContainer.setVisibility(hideScrollbar ? View.GONE : View.VISIBLE);
    }

    public final void setBubbleVisible(boolean visible) {
        mShowBubble = visible;
    }

    public final void setTrackVisible(boolean visible) {
        mTrackImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public final void setTrackColor(@ColorInt int color) {
        DrawableCompat.setTint(mTrackImageDrawable, color);
        mTrackImageView.setImageDrawable(mTrackImageDrawable);
    }

    public final void setHandleColor(@ColorInt int color) {
        mHandleColor = color;
        DrawableCompat.setTint(mHandleImageDrawable, mHandleColor);
        mHandleImageView.setImageDrawable(mHandleImageDrawable);
    }

    public final void setBubbleColor(@ColorInt int color) {
        mBubbleColor = color;

        DrawableCompat.setTint(mBubbleImageUpDrawable, mBubbleColor);
        DrawableCompat.setTint(mBubbleImageDownDrawable, mBubbleColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBubbleTextView.setBackground(mBubbleImageUpDrawable);
        } else {
            mBubbleTextView.setBackgroundDrawable(mBubbleImageUpDrawable);
        }
    }

    public final void setBubbleTextColor(@ColorInt int color) {
        mBubbleTextView.setTextColor(color);
    }

    public final void setFastScrollStateChangeListener(@Nullable FastScrollStateChangeListener listener) {
        mFastScrollStateChangeListener = listener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setVisibility(enabled ? VISIBLE : GONE);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < mHandleImageView.getX() - ViewCompat.getPaddingStart(mHandleImageView)) {
                    return false;
                }

                requestDisallowInterceptTouchEvent(true);
                setHandleSelected(true);
                getHandler().removeCallbacks(mScrollbarHider);

                cancelAnimation(mScrollbarAnimator);
                cancelAnimation(mBubbleAnimator);

                if (!isViewVisible(mScrollbarContainer)) {
                    showScrollbar();
                }
                if (mShowBubble && mSectionAdapter != null) {
                    showBubble();
                }
                if (mFastScrollStateChangeListener != null) {
                    mFastScrollStateChangeListener.onFastScrollStart(this);
                }
                setViewPositions(y);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_MOVE:
                setViewPositions(y);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                requestDisallowInterceptTouchEvent(false);
                setHandleSelected(false);
                if (mHideScrollbar) {
                    getHandler().postDelayed(mScrollbarHider, SCROLLBAR_HIDE_DELAY_IN_MILLIS);
                }
                hideBubble();
                if (mFastScrollStateChangeListener != null) {
                    mFastScrollStateChangeListener.onFastScrollStop(this);
                }

                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeight = h;
    }

    private void setRecyclerViewPosition(float positionY) {
        if (mRecyclerView == null) return;
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) return;
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) return;

        int itemCount = adapter.getItemCount();
        float proportion;
        if (mHandleImageView.getY() == 0f) {
            proportion = 0f;
        } else if (mHandleImageView.getY() + mHandleHeight >= mViewHeight - TRACK_SNAP_RANGE) {
            proportion = 1f;
        } else {
            proportion = positionY / mViewHeight;
        }
        int scrolledItemCount = Math.round(proportion * itemCount);

        if (isLayoutReversed(layoutManager)) {
            scrolledItemCount = itemCount - scrolledItemCount;
        }
        int targetPos = getValueInRange(itemCount - 1, scrolledItemCount);
        layoutManager.scrollToPosition(targetPos);

        if (mShowBubble && mSectionAdapter != null) {
            mBubbleTextView.setText(mSectionAdapter.getSectionText(targetPos));
        }
    }

    private float getScrollProportion(@Nullable RecyclerView recyclerView) {
        if (recyclerView == null) return 0f;

        int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
        int verticalScrollRange = recyclerView.computeVerticalScrollRange();
        float rangeDiff = verticalScrollRange - mViewHeight;
        float proportion = verticalScrollOffset / (rangeDiff > 0 ? rangeDiff : 1f);

        return mViewHeight * proportion;
    }

    private void setViewPositions(float y) {
        mBubbleHeight = mBubbleTextView.getHeight();
        mHandleHeight = mHandleImageView.getHeight();

        int bubbleY = getValueInRange(mViewHeight - mBubbleHeight, (int) y);
        if (y > mViewHeight / 2) {
            bubbleY = getValueInRange(mViewHeight - mBubbleHeight - mHandleHeight / 2, (int) (y - mBubbleHeight));
            Drawable background = mBubbleTextView.getBackground();
            if (background != null && background != mBubbleImageDownDrawable) {
                ViewCompat.setBackground(mBubbleTextView, mBubbleImageDownDrawable);
            }
        } else {
            Drawable background = mBubbleTextView.getBackground();
            if (background != null && background != mBubbleImageUpDrawable) {
                ViewCompat.setBackground(mBubbleTextView, mBubbleImageUpDrawable);
            }
        }
        int handleY = getValueInRange(mViewHeight - mHandleHeight, (int) (y - mHandleHeight / 2));
        if (mShowBubble) mBubbleTextView.setY(bubbleY);
        mHandleImageView.setY(handleY);
    }

    private int getValueInRange(int max, int value) {
        return Math.min(Math.max(0, value), max);
    }

    private boolean isLayoutReversed(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).getReverseLayout();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getReverseLayout();
        }
        return false;
    }

    private boolean isViewVisible(@Nullable View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    private void cancelAnimation(@Nullable ViewPropertyAnimator animator) {
        if (animator != null) animator.cancel();
    }

    private void showBubble() {
        if (!isViewVisible(mBubbleTextView)) {
            mBubbleTextView.setVisibility(View.VISIBLE);
            mBubbleAnimator = mBubbleTextView.animate()
                    .alpha(1f)
                    .setDuration(BUBBLE_ANIM_DURATION_IN_MILLIS)
                    .setListener(sDefaultAnimatorListener);
        }
    }

    private void hideBubble() {
        if (isViewVisible(mBubbleTextView)) {
            mBubbleAnimator = mBubbleTextView.animate()
                    .alpha(0f)
                    .setDuration(BUBBLE_ANIM_DURATION_IN_MILLIS)
                    .setListener(mHideBubbleListener);
        }
    }

    private void showScrollbar() {
        if (mRecyclerView != null && mRecyclerView.computeVerticalScrollRange() - mViewHeight > 0) {
            mScrollbarContainer.setTranslationX(mScrollbarPaddingEnd);
            mScrollbarContainer.setVisibility(View.VISIBLE);
            mScrollbarAnimator = mScrollbarContainer.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(SCROLLBAR_ANIM_DURATION_IN_MILLIS)
                    .setListener(sDefaultAnimatorListener);
        }
    }

    private void hideScrollbar() {
        mScrollbarAnimator = mScrollbarContainer.animate()
                .alpha(0f)
                .translationX(mScrollbarPaddingEnd)
                .setDuration(SCROLLBAR_ANIM_DURATION_IN_MILLIS)
                .setListener(mHideScrollbarListener);
    }

    private void setHandleSelected(boolean selected) {
        mHandleImageView.setSelected(selected);
        DrawableCompat.setTint(mHandleImageDrawable, selected ? mBubbleColor : mHandleColor);
    }

}