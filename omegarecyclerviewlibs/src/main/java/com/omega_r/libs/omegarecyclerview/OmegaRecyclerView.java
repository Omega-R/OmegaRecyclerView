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
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mac on 10.05.17.
 */

public class OmegaRecyclerView extends RecyclerView {

    private static final int[] DEFAULT_DIVIDER_ATTRS = new int[]{android.R.attr.listDivider};

    private View mEmptyView;
    private int mEmptyViewId;

    private int mItemSpace;

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
                    dividerDrawable = getDefaultDivider();
                }

                float dividerHeight = a.getDimension(R.styleable.OmegaRecyclerView_android_dividerHeight, -1);
                float alpha = a.getFloat(R.styleable.OmegaRecyclerView_alphaDivider, 1);
                addItemDecoration(new DividerItemDecoration(dividerDrawable, (int) dividerHeight, showDivider, mItemSpace / 2, alpha));
            }
        }
    }

    private Drawable getDefaultDivider() {
        final TypedArray attributes = getContext().obtainStyledAttributes(DEFAULT_DIVIDER_ATTRS);
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
            return (T) itemView.findViewById(id);
        }
    }
}
