package com.omega_r.libs.omegarecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.header.HeaderFooterWrapperAdapter;
import com.omega_r.libs.omegarecyclerview.item_decoration.DividerItemDecoration;
import com.omega_r.libs.omegarecyclerview.item_decoration.SpaceItemDecoration;
import com.omega_r.libs.omegarecyclerview.pagination.OnPageRequestListener;
import com.omega_r.libs.omegarecyclerview.pagination.PageRequester;
import com.omega_r.libs.omegarecyclerview.pagination.PaginationAdapter;
import com.omega_r.libs.omegarecyclerview.pagination.WrapperAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.BaseStickyDecoration;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.HeaderStickyDecoration;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.MiddleStickyDecoration;
import com.omega_r.libs.omegarecyclerview.swipe_menu.SwipeMenuHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ExpandedRecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OmegaRecyclerView extends ExpandedRecyclerView implements SwipeMenuHelper.Callback {

    private static final int[] DEFAULT_DIVIDER_ATTRS = new int[]{android.R.attr.listDivider};

    private View mEmptyView;
    private int mEmptyViewId;

    private SwipeMenuHelper mSwipeMenuHelper;
    private PageRequester mPageRequester = new PageRequester();
    private BaseStickyDecoration mBaseStickyDecoration;
    private int mStickyMode = StickyAdapter.Mode.HEADER;
    @LayoutRes
    private int mPaginationLayout = R.layout.pagination_omega_layout;
    @LayoutRes
    private int mPaginationErrorLayout = R.layout.pagination_error_omega_layout;
    private boolean mFinishedInflate = false;
    private List<View> mHeadersList = new ArrayList<>();
    private List<View> mFooterList = new ArrayList<>();
    private WeakHashMap<ViewGroup.LayoutParams, SectionState> mLayoutParamCache = new WeakHashMap<>();
    @Nullable
    private DividerItemDecoration mDividerItemDecoration;
    private int mItemSpace;
    private int mDividerSize;

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
            initItemSpace(a);
            initDivider(a);
            initEmptyView(a);
            initPagination(a);
            initStickyMode(a);
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
            int showDivider = a.getInt(R.styleable.OmegaRecyclerView_dividerShow, DividerItemDecoration.ShowDivider.NONE);
            mItemSpace = (int) a.getDimension(R.styleable.OmegaRecyclerView_itemSpace, 0);
            addItemDecoration(new SpaceItemDecoration(showDivider, mItemSpace));
        }
    }

    public void initDivider(TypedArray a) {
        if (a.hasValue(R.styleable.OmegaRecyclerView_dividerShow)) {
            int showDivider = a.getInt(R.styleable.OmegaRecyclerView_dividerShow, DividerItemDecoration.ShowDivider.NONE);
            if (showDivider != DividerItemDecoration.ShowDivider.NONE) {
                Drawable dividerDrawable = a.getDrawable(R.styleable.OmegaRecyclerView_android_divider);
                if (dividerDrawable == null) {
                    dividerDrawable = a.getDrawable(R.styleable.OmegaRecyclerView_divider);
                    if (dividerDrawable == null) {
                        dividerDrawable = getDefaultDivider();
                    }
                }


                mDividerSize = (int) a.getDimension(R.styleable.OmegaRecyclerView_dividerHeight,

                        a.getDimension(R.styleable.OmegaRecyclerView_android_dividerHeight, -1));
                float alpha = a.getFloat(R.styleable.OmegaRecyclerView_dividerAlpha, 1);

                mDividerItemDecoration = new DividerItemDecoration(
                        dividerDrawable,
                        mDividerSize,
                        showDivider,
                        mItemSpace / 2,
                        alpha
                );

                int paddingStartDivider = a.getDimensionPixelSize(R.styleable.OmegaRecyclerView_dividerPaddingStart, 0);
                int paddingEndDivider = a.getDimensionPixelSize(R.styleable.OmegaRecyclerView_dividerPaddingEnd, 0);

                mDividerItemDecoration.setPaddingStart(paddingStartDivider);
                mDividerItemDecoration.setPaddingEnd(paddingEndDivider);

                if (a.hasValue(R.styleable.OmegaRecyclerView_dividerPadding)) {
                    int paddingDivider = a.getDimensionPixelSize(R.styleable.OmegaRecyclerView_dividerPadding, 0);
                    mDividerItemDecoration.setPadding(paddingDivider);
                }

                addItemDecoration(mDividerItemDecoration);
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

    private void initStickyMode(TypedArray a) {
        mStickyMode = a.getInt(R.styleable.OmegaRecyclerView_stickyMode, mStickyMode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setAdapter(RecyclerView.Adapter adapter) {
        unregisterObservers();

        if (adapter == null) {
            super.setAdapter(null);
            updateStickyDecoration(null);
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
        updateStickyDecoration(shellAdapter);
    }

    @Nullable
    public RecyclerView.Adapter getRealAdapter() {
        return getRealAdapter(getAdapter());
    }

    private RecyclerView.Adapter getRealAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            return getRealAdapter(((HeaderFooterWrapperAdapter) adapter).getWrappedAdapter());
        } else if (adapter instanceof WrapperAdapter) {
            return getRealAdapter(((WrapperAdapter) adapter).getLastWrappedAdapter());
        }
        return adapter;
    }

    private void unregisterObservers() {
        RecyclerView.Adapter currentAdapter = super.getAdapter();
        if (currentAdapter != null) {
            currentAdapter.unregisterAdapterDataObserver(mEmptyObserver);
            if (currentAdapter instanceof HeaderFooterWrapperAdapter) {
                ((HeaderFooterWrapperAdapter) currentAdapter).getWrappedAdapter().unregisterAdapterDataObserver(mHeaderObserver);
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

    protected void updateStickyDecoration(@Nullable RecyclerView.Adapter adapter) {
        if (adapter == null) {
            if (mBaseStickyDecoration != null) removeItemDecoration(mBaseStickyDecoration);
        } else {
            StickyAdapter stickyAdapter = null;
            if (adapter instanceof WrapperAdapter) {
                RecyclerView.Adapter wrappedAdapter = ((WrapperAdapter) adapter).getLastWrappedAdapter();
                if (wrappedAdapter instanceof StickyAdapter) {
                    stickyAdapter = (StickyAdapter) wrappedAdapter;
                }
            } else if (adapter instanceof HeaderFooterWrapperAdapter) {
                if (((HeaderFooterWrapperAdapter) adapter).getStickyAdapter() != null) {
                    stickyAdapter = (StickyAdapter) adapter;
                }
            } else if (adapter instanceof StickyAdapter) {
                stickyAdapter = (StickyAdapter) adapter;
            }
            if (mBaseStickyDecoration == null) {
                mBaseStickyDecoration = provideStickyDecoration(adapter, stickyAdapter);
                if (mBaseStickyDecoration == null) return;
                mBaseStickyDecoration.setItemSpace(mItemSpace + mDividerSize);
                addItemDecoration(mBaseStickyDecoration);
            } else {
                mBaseStickyDecoration.setStickyAdapter(stickyAdapter);
                invalidateItemDecorations();
            }
        }
    }

    @Nullable
    protected BaseStickyDecoration provideStickyDecoration(@NonNull RecyclerView.Adapter adapter, @Nullable StickyAdapter stickyAdapter) {
        if (stickyAdapter == null) return null;
        switch (mStickyMode) {
            case StickyAdapter.Mode.MIDDLE:
                return new MiddleStickyDecoration(stickyAdapter);
            case StickyAdapter.Mode.HEADER:
            default:
                return new HeaderStickyDecoration(stickyAdapter);
        }
    }

    @Nullable
    protected final BaseStickyDecoration getStickyDecoration() {
        return mBaseStickyDecoration;
    }


    @Override
    public void addView(View view, int index, ViewGroup.LayoutParams params) {
        if (mFinishedInflate) {
            super.addView(view, index, params);
        } else {
            view.setLayoutParams(params);
            SectionState sectionState = mLayoutParamCache.get(params);
            if (sectionState == null) return;
            view.setTag(R.id.section_show_divider, sectionState.showDivider);

            if (sectionState.position == 0) {
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
            boolean showDivider = typedArray.getBoolean(R.styleable.OmegaRecyclerView_Layout_layout_showDivider, true);
            typedArray.recycle();
            mLayoutParamCache.put(layoutParams, new SectionState(section, showDivider));
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
            mEmptyObserver.onChanged();
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

    public void setDividerAlpha(float dividerAlpha) {
        if (0 > dividerAlpha || dividerAlpha > 1) return;

        if (mDividerItemDecoration != null) {
            mDividerItemDecoration.setDividerAlpha(dividerAlpha);
        }

        boolean hasDividerDecoration = false;
        for (int i = 0; i < getItemDecorationCount(); i++) {
            RecyclerView.ItemDecoration itemDecoration = getItemDecorationAt(i);
            if (itemDecoration instanceof DividerItemDecoration) {
                hasDividerDecoration = true;
                invalidateItemDecorations();
                break;
            }
        }

        if (!hasDividerDecoration && mDividerItemDecoration != null) {
            addItemDecoration(mDividerItemDecoration);
        }
    }

    public void setDividerDrawable(@Nullable Drawable drawable) {
        if (mDividerItemDecoration != null && drawable != null) {
            mDividerItemDecoration.setDividerDrawable(drawable);
        }

        boolean hasDividerDecoration = false;
        for (int i = 0; i < getItemDecorationCount(); i++) {
            RecyclerView.ItemDecoration itemDecoration = getItemDecorationAt(i);
            if (itemDecoration instanceof DividerItemDecoration) {
                hasDividerDecoration = true;
                if (drawable == null) {
                    removeItemDecoration(itemDecoration);
                    break;
                } else {
                    invalidateItemDecorations();
                    break;
                }
            }
        }

        if (drawable != null && !hasDividerDecoration) {
            addItemDecoration(mDividerItemDecoration);
        }
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

    @Override
    protected int getAdapterPositionFor(RecyclerView.ViewHolder viewHolder) {
        RecyclerView.Adapter adapter = getAdapter();
        int realPosition = super.getAdapterPositionFor(viewHolder);

        if (adapter == null) return realPosition;
        if (adapter instanceof HeaderFooterWrapperAdapter) {
            return ((HeaderFooterWrapperAdapter) adapter).applyRealPositionToChildPosition(realPosition);
        }
        return realPosition;
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

    private class SectionState {

        int position;
        boolean showDivider;

        public SectionState(int position, boolean showDivider) {
            this.position = position;
            this.showDivider = showDivider;
        }
    }

    private final AdapterDataObserver mHeaderObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                ((Adapter) getAdapter()).tryNotifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                ((Adapter) getAdapter()).tryNotifyItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                ((Adapter) getAdapter()).tryNotifyItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                ((Adapter) getAdapter()).tryNotifyItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                ((Adapter) getAdapter()).tryNotifyItemRangeRemoved(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (getAdapter() instanceof HeaderFooterWrapperAdapter) {
                ((Adapter) getAdapter()).tryNotifyItemMoved(fromPosition, toPosition);
            }
        }
    };

    public abstract static class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        private RecyclerView recyclerView;

        public boolean isDividerAllowedAbove(int position) {
            return true;
        }

        public boolean isDividerAllowedBelow(int position) {
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

        protected void tryNotifyItemChanged(final int position) {
            if (recyclerView == null) return;

            if (!recyclerView.isComputingLayout()) {
                notifyItemChanged(position);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tryNotifyItemChanged(position);
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

        protected void tryNotifyItemRemoved(final int position) {
            if (recyclerView == null) return;

            if (!recyclerView.isComputingLayout()) {
                notifyItemRemoved(position);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tryNotifyItemRemoved(position);
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

        protected void tryNotifyItemRangeRemoved(final int positionStart, final int itemCount) {
            if (recyclerView == null) return;

            if (!recyclerView.isComputingLayout()) {
                notifyItemRangeRemoved(positionStart, itemCount);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tryNotifyItemRangeRemoved(positionStart, itemCount);
                    }
                });
            }
        }

        protected void tryNotifyItemMoved(final int fromPosition, final int toPosition) {
            if (recyclerView == null) return;

            if (!recyclerView.isComputingLayout()) {
                notifyItemMoved(fromPosition, toPosition);
            } else {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        tryNotifyItemRangeRemoved(fromPosition, toPosition);
                    }
                });
            }
        }
    }

    public static class ViewHolder extends ExpandedRecyclerView.ViewHolder {

        public ViewHolder(ViewGroup parent, @LayoutRes int res) {
            this(parent, LayoutInflater.from(parent.getContext()), res);
        }

        public ViewHolder(ViewGroup parent, LayoutInflater layoutInflater, @LayoutRes int res) {
            this(layoutInflater.inflate(res, parent, false));
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public final <T extends View> T findViewById(int id) {
            //noinspection unchecked
            return (T) itemView.findViewById(id);
        }

        public final Context getContext() {
            return itemView.getContext();
        }

        protected final Resources getResources() {
            return getContext().getResources();
        }

        protected final String getString(@StringRes int res) {
            return getResources().getString(res);
        }

        protected final String getString(@StringRes int res, Object... formatArgs) {
            return getResources().getString(res, formatArgs);
        }

        @ColorInt
        protected final int getColor(@ColorRes int id) {
            return ContextCompat.getColor(getContext(), id);
        }
    }

    public static class ItemDecoration extends RecyclerView.ItemDecoration {

        protected int getAdapterPosition(RecyclerView parent, View view) {
            int childPosition = parent.getChildAdapterPosition(view);

            if (parent instanceof OmegaRecyclerView) {
                RecyclerView.Adapter adapter = parent.getAdapter();
                if (adapter instanceof HeaderFooterWrapperAdapter) {
                    return ((HeaderFooterWrapperAdapter) adapter).applyChildPositionToRealPosition(childPosition);
                }
            }

            return childPosition;
        }
    }

}
