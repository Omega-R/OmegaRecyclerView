package com.omega_r.libs.omegarecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeMenuHelper;

public class OmegaRecyclerView extends RecyclerView implements SwipeMenuHelper.Callback {

    private static final int[] DEFAULT_DIVIDER_ATTRS = new int[]{android.R.attr.listDivider};

    private View mEmptyView;
    private int mEmptyViewId;

    private int mItemSpace;

    private SwipeMenuHelper mSwipeMenuHelper;

    public OmegaRecyclerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OmegaRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OmegaRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initDefaultLayoutManager();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OmegaRecyclerView, defStyleAttr, 0);
            initItemSpace(a);
            initDivider(a);
            initEmptyView(a);
            a.recycle();
        }
        mSwipeMenuHelper = new SwipeMenuHelper(getContext(), this);
    }

    private void initDefaultLayoutManager() {
        if (getLayoutManager() == null) {
            setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void initEmptyView(TypedArray a) {
        if (a.hasValue(R.styleable.OmegaRecyclerView_emptyView)) {
            mEmptyViewId = a.getResourceId(R.styleable.OmegaRecyclerView_emptyView, 0);
        }
    }

    public void initItemSpace(TypedArray a) {
        if (a.hasValue(R.styleable.OmegaRecyclerView_itemSpace)) {
            mItemSpace = (int) a.getDimension(R.styleable.OmegaRecyclerView_itemSpace, 0);
            addItemSpace(mItemSpace);
        }
    }

    public void initDivider(TypedArray a) {
        if (a.hasValue(R.styleable.OmegaRecyclerView_showDivider)) {
            int showDivider = a.getInt(R.styleable.OmegaRecyclerView_showDivider, DividerItemDecoration.ShowDivider.NONE);
            if (showDivider != DividerItemDecoration.ShowDivider.NONE) {
                Drawable dividerDrawable = a.getDrawable(R.styleable.OmegaRecyclerView_android_divider);
                if (dividerDrawable == null) {
                    dividerDrawable = a.getDrawable(R.styleable.OmegaRecyclerView_divider);
                    if (dividerDrawable == null) {
                        dividerDrawable = getDefaultDivider();
                    }
                }

                float dividerHeight = a.getDimension(R.styleable.OmegaRecyclerView_heightDivider,
                        a.getDimension(R.styleable.OmegaRecyclerView_android_dividerHeight, -1));
                float alpha = a.getFloat(R.styleable.OmegaRecyclerView_alphaDivider, 1);
                addItemDecoration(new DividerItemDecoration(dividerDrawable, (int) dividerHeight, showDivider, mItemSpace / 2, alpha));
            }
        }
    }

    private AdapterDataObserver mEmptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            RecyclerView.Adapter adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                if (adapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mEmptyObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mEmptyObserver);
        }
        super.setAdapter(adapter);
        mEmptyObserver.onChanged();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        findEmptyView();
    }

    private void findEmptyView() {
        if (mEmptyViewId == 0 || isInEditMode()) {
            return;
        }
        if (getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            mEmptyView = viewGroup.findViewById(mEmptyViewId);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercepted = super.onInterceptTouchEvent(ev);
        if (ev.getActionIndex() != 0) return true;
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isIntercepted = mSwipeMenuHelper.handleListDownTouchEvent(ev, isIntercepted);
                break;
        }

        return isIntercepted;
    }

    private Drawable getDefaultDivider() {
        TypedArray attributes = getContext().obtainStyledAttributes(DEFAULT_DIVIDER_ATTRS);
        Drawable dividerDrawable = attributes.getDrawable(0);
        attributes.recycle();

        if (dividerDrawable == null) {
            dividerDrawable = new ColorDrawable(Color.GRAY);
        }

        return dividerDrawable;
    }

    public void addItemSpace(int space) {
        addItemDecoration(new SpaceItemDecoration(space));
    }

    public int getItemSpace() {
        return mItemSpace;
    }

    @Override
    public int getPositionForView(View view) {
        return getChildAdapterPosition(view);
    }

    @Override
    public int getRealChildCount() {
        return getChildCount();
    }

    @Override
    public View getRealChildAt(int index) {
        return getChildAt(index);
    }

    @Override
    public View transformTouchView(int touchPosition, View touchView) {
        RecyclerView.ViewHolder viewHolder = findViewHolderForAdapterPosition(touchPosition);

        if (viewHolder != null) {
            return viewHolder.itemView;
        }

        return touchView;
    }

    public abstract static class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        public boolean isShowDivided(int position) {
            return true;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(ViewGroup parent, @LayoutRes int res) {
            this(parent, LayoutInflater.from(parent.getContext()), res);
        }

        public ViewHolder(ViewGroup parent, LayoutInflater layoutInflater, @LayoutRes int res) {
            this(layoutInflater.inflate(res, parent, false));
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        protected final <T extends View> T findViewById(int id) {
            //noinspection unchecked
            return (T) itemView.findViewById(id);
        }
    }
}
