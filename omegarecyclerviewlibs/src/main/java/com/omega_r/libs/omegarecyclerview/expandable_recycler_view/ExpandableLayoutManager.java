package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ExpandableLayoutManager extends RecyclerView.LayoutManager {

    public static final int INVALID_POSITION = -1;
    private static final int DEFAULT_ANIMATION_DURATION = 1000;

    private Context mContext;
    private final ExpandStateChangedListener mExpandStateChangedListener;

    ExpandableLayoutManager(@NonNull Context context,
                            @Nullable AttributeSet attrs, int defStyleAttr,
                            @NonNull ExpandStateChangedListener expandStateChangedListener) {
        mContext = context;
        mExpandStateChangedListener = expandStateChangedListener;
        setAutoMeasureEnabled(true);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    public interface ExpandStateChangedListener {
        void onExpandStateChanged(State state);
    }

    public enum State {
        EXPANDED, COLLAPSED
    }
}
