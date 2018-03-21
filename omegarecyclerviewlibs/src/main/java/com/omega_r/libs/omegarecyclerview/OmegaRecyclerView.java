package com.omega_r.libs.omegarecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.header.HeaderFooterWrapperAdapter;
import com.omega_r.libs.omegarecyclerview.pagination.PaginationAdapter;
import com.omega_r.libs.omegarecyclerview.pagination.OnPageRequestListener;
import com.omega_r.libs.omegarecyclerview.pagination.PageRequester;
import com.omega_r.libs.omegarecyclerview.pagination.WrapperAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderDecoration;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeMenuHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class OmegaRecyclerView extends RecyclerView implements SwipeMenuHelper.Callback {

    private static final int[] DEFAULT_DIVIDER_ATTRS = new int[]{android.R.attr.listDivider};

    private View mEmptyView;
    private int mEmptyViewId;

    private int mItemSpace;

    private SwipeMenuHelper mSwipeMenuHelper;
    private PageRequester mPageRequester = new PageRequester();
    private StickyHeaderDecoration mStickyHeaderDecoration;
    @LayoutRes
    private int mPaginationLayout = R.layout.pagination_omega_layout;
    @LayoutRes
    private int mPaginationErrorLayout = R.layout.pagination_error_omega_layout;
    private boolean mFinishedInflate = false;
    private List<View> mHeadersList = new ArrayList<>();
    private List<View> mFooterList = new ArrayList<>();
    private WeakHashMap<ViewGroup.LayoutParams, Integer> mLayoutParamCache = new WeakHashMap<>();
    private int mShowDivider;

    public OmegaRecyclerView(Context context) {
        super(context);
        mFinishedInflate = true;
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
        initDefaultLayoutManager(attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OmegaRecyclerView, defStyleAttr, 0);
            initDivider(a);
            initItemSpace(a);
            initEmptyView(a);
            initPagination(a);
            a.recycle();
        }
        mSwipeMenuHelper = new SwipeMenuHelper(getContext(), this);
        mPageRequester.attach(this);
    }

    protected void initDefaultLayoutManager(@Nullable AttributeSet attrs, int defStyleAttr) {
        if (getLayoutManager() == null) {
            setLayoutManager(new LinearLayoutManager(getContext(), attrs, defStyleAttr, 0));
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
            boolean addSpaceAboveFirstItem = (mShowDivider & DividerItemDecoration.ShowDivider.BEGINNING) == DividerItemDecoration.ShowDivider.BEGINNING;
            boolean addSpaceBelowLastItem = (mShowDivider & DividerItemDecoration.ShowDivider.END) == DividerItemDecoration.ShowDivider.END;
            addItemSpace(mItemSpace, addSpaceAboveFirstItem, addSpaceBelowLastItem);
        }
    }

    public void initDivider(TypedArray a) {
        if (a.hasValue(R.styleable.OmegaRecyclerView_showDivider)) {
            mShowDivider = a.getInt(R.styleable.OmegaRecyclerView_showDivider, DividerItemDecoration.ShowDivider.NONE);
            if (mShowDivider != DividerItemDecoration.ShowDivider.NONE) {
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
                addItemDecoration(new DividerItemDecoration(dividerDrawable, (int) dividerHeight, mShowDivider, mItemSpace / 2, alpha));
            }
        }
    }

    private void initPagination(TypedArray a) {
        if (a.hasValue(R.styleable.OmegaRecyclerView_paginationLayout)) {
            mPaginationLayout = a.getResourceId(R.styleable.OmegaRecyclerView_paginationLayout, R.layout.pagination_omega_layout);
        }
        if (a.hasValue(R.styleable.OmegaRecyclerView_paginationErrorLayout)) {
            mPaginationErrorLayout = a.getResourceId(R.styleable.OmegaRecyclerView_paginationErrorLayout, R.layout.pagination_error_omega_layout);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setAdapter(RecyclerView.Adapter adapter) {
        unregisterObservers();

        if (adapter == null) {
            super.setAdapter(null);
            updateStickyHeader(null);
            return;
        }

        if (adapter instanceof OnPageRequestListener) {
            setPaginationCallback((OnPageRequestListener) adapter);
        }

        RecyclerView.Adapter shellAdapter = adapter;
        if (!(adapter instanceof HeaderFooterWrapperAdapter)) {
            if (mPageRequester.getCallback() != null && !(adapter instanceof PaginationAdapter)) {
                shellAdapter = new PaginationAdapter(adapter, mPaginationLayout, mPaginationErrorLayout);
            }
            if (!mHeadersList.isEmpty() || !mFooterList.isEmpty()) {
                shellAdapter = new HeaderFooterWrapperAdapter(shellAdapter);
                ((HeaderFooterWrapperAdapter) shellAdapter).setHeaders(mHeadersList);
                ((HeaderFooterWrapperAdapter) shellAdapter).setFooters(mFooterList);
            }
        }
        super.setAdapter(shellAdapter);
        mPageRequester.reset();

        registerObservers(shellAdapter);
        updateStickyHeader(shellAdapter);
    }

    private void unregisterObservers() {
        RecyclerView.Adapter currentAdapter = super.getAdapter();
        if (currentAdapter != null) {
            currentAdapter.unregisterAdapterDataObserver(mEmptyObserver);
            if (currentAdapter instanceof HeaderFooterWrapperAdapter) {
                currentAdapter.unregisterAdapterDataObserver(mHeaderObserver);
            }
        }
        mEmptyObserver.onChanged();
        mHeaderObserver.onChanged();
    }

    private void registerObservers(RecyclerView.Adapter adapter) {
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            ((HeaderFooterWrapperAdapter) adapter).getWrappedAdapter().registerAdapterDataObserver(mHeaderObserver);
        }
        adapter.registerAdapterDataObserver(mEmptyObserver);
    }

    private void updateStickyHeader(@Nullable RecyclerView.Adapter adapter) {
        if (adapter == null) {
            if (mStickyHeaderDecoration != null) removeItemDecoration(mStickyHeaderDecoration);
        } else {
            RecyclerView.Adapter realAdapter = adapter;
            if (adapter instanceof WrapperAdapter) {
                realAdapter = ((WrapperAdapter) adapter).getLastWrappedAdapter();
            }
            if (realAdapter instanceof StickyHeaderAdapter) {
                if (mStickyHeaderDecoration == null) {
                    mStickyHeaderDecoration = new StickyHeaderDecoration((StickyHeaderAdapter) realAdapter);
                    addItemDecoration(mStickyHeaderDecoration);
                } else {
                    mStickyHeaderDecoration.setAdapter((StickyHeaderAdapter) realAdapter);
                    invalidateItemDecorations();
                }
            }
        }
    }

    @Override
    public void addView(View view, int index, ViewGroup.LayoutParams params) {
        if (mFinishedInflate) {
            super.addView(view, index, params);
        } else {
            view.setLayoutParams(params);
            Integer integer = mLayoutParamCache.get(params);
            if (integer == null || integer == 0) {
                mHeadersList.add(view);
            } else {
                mFooterList.add(view);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFinishedInflate = true;
        mLayoutParamCache.clear();
        if (getAdapter() != null) {
            setAdapter(getAdapter());
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    protected <T extends View> T findViewTraversal(@IdRes int id) {
        if (id == getId()) {
            return (T) this;
        }
        for (View view : mHeadersList) {
            View viewById = view.findViewById(id);
            if (viewById != null) return (T) viewById;
        }
        for (View view : mFooterList) {
            View viewById = view.findViewById(id);
            if (viewById != null) return (T) viewById;
        }

        final int len = getChildCount();

        for (int i = 0; i < len; i++) {
            View v = getChildAt(i);

            v = v.findViewById(id);

            if (v != null) {
                return (T) v;
            }
        }

        return null;
    }

    @SuppressLint("CustomViewStyleable")
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        ViewGroup.LayoutParams layoutParams = super.generateLayoutParams(attrs);
        if (!mFinishedInflate) {
            final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.OmegaRecyclerView_Layout);
            int section = typedArray.getInt(R.styleable.OmegaRecyclerView_Layout_layout_section, 0);
            typedArray.recycle();
            mLayoutParamCache.put(layoutParams, section);
        }
        return layoutParams;
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

    public void addItemSpace(int space, boolean addSpaceAboveFirstItem, boolean addSpaceBelowLastItem) {
        addItemDecoration(new SpaceItemDecoration(space, addSpaceAboveFirstItem, addSpaceBelowLastItem));
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

    public void setPaginationCallback(OnPageRequestListener callback) {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            adapter = ((HeaderFooterWrapperAdapter) adapter).getWrappedAdapter();
        }
        if (adapter != null && mPageRequester.getCallback() != null && !(adapter instanceof PaginationAdapter)) {
            setAdapter(new PaginationAdapter(adapter, mPaginationLayout, mPaginationErrorLayout));
        }
        mPageRequester.setPaginationCallback(callback);
    }

    public void showProgressPagination() {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            adapter = ((HeaderFooterWrapperAdapter) adapter).getWrappedAdapter();
        }
        if (adapter instanceof PaginationAdapter) {
            ((PaginationAdapter) adapter).showProgressPagination();
            mPageRequester.setEnabled(true);
        }
    }

    public void showErrorPagination() {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            adapter = ((HeaderFooterWrapperAdapter) adapter).getWrappedAdapter();
        }
        if (adapter instanceof PaginationAdapter) {
            ((PaginationAdapter) adapter).showErrorPagination();
            mPageRequester.setEnabled(false);
        }
    }

    public void hidePagination() {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            adapter = ((HeaderFooterWrapperAdapter) adapter).getWrappedAdapter();
        }
        if (adapter instanceof PaginationAdapter) {
            ((PaginationAdapter) adapter).hidePagination();
            mPageRequester.setEnabled(false);
        }
    }

    public void setHeadersVisibility(boolean visible) {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            ((HeaderFooterWrapperAdapter) adapter).setHeadersVisible(visible);
        }
    }

    public void setFootersVisibility(boolean visible) {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            ((HeaderFooterWrapperAdapter) adapter).setFootersVisible(visible);
        }
    }

    private final AdapterDataObserver mEmptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            RecyclerView.Adapter adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                mEmptyView.setVisibility(adapter.getItemCount() == 0 ? VISIBLE : GONE);
            }
        }
    };

    private final AdapterDataObserver mHeaderObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                getAdapter().notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                getAdapter().notifyItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                getAdapter().notifyItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                getAdapter().notifyItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                getAdapter().notifyItemRangeRemoved(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                getAdapter().notifyItemMoved(fromPosition, toPosition);
            }
        }
    };

    public abstract static class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        private RecyclerView recyclerView;

        public boolean isShowDivided(int position) {
            return true;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.recyclerView = recyclerView;
        }

        protected void tryNotifyDataSetChanged() {
            if (recyclerView == null) return;

            if (!recyclerView.isComputingLayout()) {
                notifyDataSetChanged();
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tryNotifyDataSetChanged();
                    }
                });
            }
        }

        protected void tryNotifyItemRangeInserted(final int positionStart, final int itemCount) {
            if (recyclerView == null) return;

            if (!recyclerView.isComputingLayout()) {
                notifyItemRangeInserted(positionStart, itemCount);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tryNotifyItemRangeInserted(positionStart, itemCount);
                    }
                });
            }
        }

        protected void tryNotifyItemRemoved(final int positionStart, final int itemCount) {
            if (recyclerView == null) return;

            if (!recyclerView.isComputingLayout()) {
                notifyItemRangeRemoved(positionStart, itemCount);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tryNotifyItemRemoved(positionStart, itemCount);
                    }
                });
            }
        }

        protected void tryNotifyItemRangeChanged(final int positionStart, final int itemCount, final Object payload) {
            if (recyclerView == null) return;

            if (!recyclerView.isComputingLayout()) {
                notifyItemRangeChanged(positionStart, itemCount, payload);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tryNotifyItemRangeChanged(positionStart, itemCount, payload);
                    }
                });
            }
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
