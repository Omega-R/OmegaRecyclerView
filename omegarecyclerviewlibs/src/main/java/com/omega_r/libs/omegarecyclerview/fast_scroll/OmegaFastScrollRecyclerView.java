package com.omega_r.libs.omegarecyclerview.fast_scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class OmegaFastScrollRecyclerView extends OmegaRecyclerView {

    private OmegaFastScrollerLayout mFastScrollerLayout;

    public OmegaFastScrollRecyclerView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public OmegaFastScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OmegaFastScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        mFastScrollerLayout = new OmegaFastScrollerLayout(context, attrs);
    }

    @Override
    public final void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof SectionIndexer) {
            setSectionIndexer((SectionIndexer) adapter);
        } else if (adapter == null) {
            setSectionIndexer(null);
        }
    }

    @Override
    public final void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mFastScrollerLayout.setVisibility(visibility);
    }

    /**
     * Set the [SectionIndexer] for the [FastScroller].
     *
     * @param indexer The SectionIndexer that provides section text for the FastScroller
     */
    public final void setSectionIndexer(SectionIndexer indexer) {
        mFastScrollerLayout.setSectionIndexer(indexer);
    }

    /**
     * Set the enabled state of fast scrolling.
     *
     * @param enabled True to enable fast scrolling, false otherwise
     */
    public final void setFastScrollEnabled(boolean enabled) {
        mFastScrollerLayout.setEnabled(enabled);
    }

    /**
     * Hide the scrollbar when not scrolling.
     *
     * @param hide True to hide the scrollbar, false to show
     */
    public final void setHideScrollbar(boolean hide) {
        mFastScrollerLayout.setHideScrollbar(hide);
    }


    /**
     * Display a scroll track while scrolling.
     *
     * @param visible True to show scroll track, false to hide
     */
    public final void setTrackVisible(boolean visible) {
        mFastScrollerLayout.setTrackVisible(visible);
    }

    /**
     * Set the color of the scroll track.
     *
     * @param color The color for the scroll track
     */
    public final void setTrackColor(@ColorInt int color) {
        mFastScrollerLayout.setTrackColor(color);
    }

    /**
     * Set the color for the scroll handle.
     *
     * @param color The color for the scroll handle
     */
    public final void setHandleColor(@ColorInt int color) {
        mFastScrollerLayout.setHandleColor(color);
    }

    /**
     * Show the section bubble while scrolling.
     *
     * @param visible True to show the bubble, false to hide
     */
    public final void setBubbleVisible(boolean visible) {
        mFastScrollerLayout.setBubbleVisible(visible);
    }

    /**
     * Set the background color of the index bubble.
     *
     * @param color The background color for the index bubble
     */
    public final void setBubbleColor(@ColorInt int color) {
        mFastScrollerLayout.setBubbleColor(color);
    }

    /**
     * Set the text color of the index bubble.
     *
     * @param color The text color for the index bubble
     */
    public final void setBubbleTextColor(@ColorInt int color) {
        mFastScrollerLayout.setBubbleTextColor(color);
    }

    /**
     * Set the fast scroll state change listener.
     *
     * @param listener The interface that will listen to fastscroll state change events
     */
    public final void setFastScrollStateChangeListener(FastScrollStateChangeListener listener) {
        mFastScrollerLayout.setFastScrollStateChangeListener(listener);
    }

    @Override
    protected final void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFastScrollerLayout.attachRecyclerView(this);

        ViewParent parent = getParent();

        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).addView(mFastScrollerLayout);
            mFastScrollerLayout.setLayoutParams((ViewGroup) parent);
        }
    }

    @Override
    protected final void onDetachedFromWindow() {
        mFastScrollerLayout.detachRecyclerView();
        super.onDetachedFromWindow();
    }

}