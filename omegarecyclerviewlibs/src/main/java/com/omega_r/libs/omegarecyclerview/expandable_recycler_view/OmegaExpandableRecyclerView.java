package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.R;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.AnimationHelper;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations.DropDownItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations.FadeItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.ExpandableViewData;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.FlatGroupingList;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.GroupProvider;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.Range;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.layout_manager.ExpandableLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OmegaExpandableRecyclerView extends OmegaRecyclerView {

    public static final int CHILD_ANIM_DEFAULT = 0;
    public static final int CHILD_ANIM_FADE = 1;
    public static final int CHILD_ANIM_DROPDOWN = 2;

    public static final int EXPAND_MODE_SINGLE = 0;
    public static final int EXPAND_MODE_MULTIPLE = 1;

    private int mExpandMode = EXPAND_MODE_SINGLE;
    private int mChildAnimInt = CHILD_ANIM_DEFAULT;

    //region Recycler

    public OmegaExpandableRecyclerView(Context context) {
        super(context);
        init();
    }

    public OmegaExpandableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(attrs, 0);
        init();
    }

    public OmegaExpandableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(attrs, defStyle);
        init();
    }


    private void init() {
        setItemAnimator(requestItemAnimator());
        setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
            @Override
            public int onGetChildDrawingOrder(int childCount, int i) {
                return childCount - i - 1;
            }
        });
    }

    @Nullable
    private ItemAnimator requestItemAnimator() {
        switch (mChildAnimInt) {
            case CHILD_ANIM_DEFAULT:
                return new DefaultItemAnimator();
            case CHILD_ANIM_DROPDOWN:
                return new DropDownItemAnimator();
            case CHILD_ANIM_FADE:
                return new FadeItemAnimator();
        }
        return null;
    }

    private void parseAttributes(AttributeSet attributeSet, int defStyleAttr) {
        TypedArray attrs = getContext().getTheme()
                .obtainStyledAttributes(attributeSet, R.styleable.OmegaExpandableRecyclerView, defStyleAttr, 0);
        try {
            mChildAnimInt = attrs.getInteger(R.styleable.OmegaExpandableRecyclerView_childAnimation, CHILD_ANIM_DEFAULT);
            mExpandMode = attrs.getInteger(R.styleable.OmegaExpandableRecyclerView_expandMode, EXPAND_MODE_SINGLE);
        } finally {
            attrs.recycle();
        }

    }

    @Override
    protected void initDefaultLayoutManager(@Nullable AttributeSet attrs, int defStyleAttr) {
        if (getLayoutManager() == null) {
            setLayoutManager(new ExpandableLayoutManager(getContext(), attrs, defStyleAttr, 0));
        }
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layoutManager) {
        if (layoutManager != null && !(layoutManager instanceof ExpandableLayoutManager)) {
            throw new IllegalStateException("LayoutManager " + layoutManager.toString() + " should be ExpandableLayoutManager");
        }
        super.setLayoutManager(layoutManager);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (!(adapter instanceof Adapter))
            throw new IllegalStateException("Adapter should extend OmegaExpandableRecyclerView.Adapter");
        super.setAdapter(adapter);
    }

    public int getChildAnimInt() {
        return mChildAnimInt;
    }

    public void setChildAnimInt(@IntRange(from = CHILD_ANIM_DEFAULT, to = CHILD_ANIM_DROPDOWN) int childAnimInt) {
        mChildAnimInt = childAnimInt;
    }

    public int getExpandMode() {
        return mExpandMode;
    }
    // endregion

    //region Adapter
    public static abstract class Adapter<G, CH> extends OmegaRecyclerView.Adapter<BaseViewHolder> {

        private static final int VH_TYPE_GROUP = 0;
        private static final int VH_TYPE_CHILD = 1;

        private static final long ANTI_SPAM_DELAY = 400;

        private long mAntiSpamTimestamp = SystemClock.elapsedRealtime();

        private FlatGroupingList<G, CH> items;
        private OmegaExpandableRecyclerView recyclerView;

        protected abstract GroupViewHolder provideGroupViewHolder(@NonNull ViewGroup viewGroup);

        protected abstract ChildViewHolder provideChildViewHolder(@NonNull ViewGroup viewGroup);

        @SafeVarargs
        public Adapter(ExpandableViewData<G, CH>... expandableViewData) {
            items = new FlatGroupingList<>(Arrays.asList(expandableViewData));
        }

        @SafeVarargs
        public Adapter(GroupProvider<G, CH>... groupProviders) {
            items = new FlatGroupingList<>(convertFrom(groupProviders));
        }

        public Adapter() {
            items = new FlatGroupingList<>(Collections.<ExpandableViewData<G, CH>>emptyList());
        }

        @NonNull
        private List<ExpandableViewData<G, CH>> convertFrom(GroupProvider<G, CH>[] groupProviders) {
            List<ExpandableViewData<G, CH>> expandableViewData = new ArrayList<>();
            for (GroupProvider<G, CH> groupProvider : groupProviders) {
                expandableViewData.add(ExpandableViewData.of(groupProvider.provideGroup(), groupProvider.provideChilds()));
            }
            return expandableViewData;
        }

        public final void setItems(@NonNull List<ExpandableViewData<G, CH>> expandableViewData) {
            items = new FlatGroupingList<>(expandableViewData);
            tryNotifyDataSetChanged();
        }

        @SafeVarargs
        public final void setItems(ExpandableViewData<G, CH>... expandableViewData) {
            setItems(Arrays.asList(expandableViewData));
        }

        @SafeVarargs
        public final void setItems(GroupProvider<G, CH>... groupProviders) {
            setItems(convertFrom(groupProviders));
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
            switch (itemType) {
                case VH_TYPE_CHILD:
                    return provideChildViewHolder(viewGroup);
                case VH_TYPE_GROUP:
                    return provideGroupViewHolder(viewGroup);
            }
            throw new IllegalStateException("Incorrect view type");
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int position) {
            baseViewHolder.bind(items.get(position));
        }

        @Override
        public void onViewRecycled(@NonNull BaseViewHolder holder) {
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {
            return items.getVisibleItemsCount();
        }

        @Override
        public int getItemViewType(int position) {
            switch (items.getType(position)) {
                case GROUP:
                    return VH_TYPE_GROUP;
                case CHILD:
                    return VH_TYPE_CHILD;
            }
            return super.getItemViewType(position);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.recyclerView = (OmegaExpandableRecyclerView) recyclerView;
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            this.recyclerView = null;
        }

        public void expand(G group) {
            if (recyclerView != null && recyclerView.getExpandMode() == EXPAND_MODE_SINGLE) {
                List<G> expandedGroups = items.getExpandedGroups();
                for (G expandedGroup : expandedGroups) {
                    collapse(expandedGroup);
                }
            }

            items.onExpandStateChanged(group, true);

            int childsCount = items.getChildsCount(group);
            int positionStart = items.getVisiblePosition(group) + 1;

            if (childsCount > 0) {

                if (recyclerView != null) {
                    ExpandableLayoutManager lm = (ExpandableLayoutManager) recyclerView.getLayoutManager();
                    if (lm != null) lm.setAddedRange(Range.ofLength(positionStart, childsCount));
                }

                tryNotifyItemRangeInserted(positionStart, childsCount);
            }
        }

        public void collapse(G group) {
            items.onExpandStateChanged(group, false);

            int childsCount = items.getChildsCount(group);
            if (childsCount > 0) {
                tryNotifyItemRangeRemoved(items.getVisiblePosition(group) + 1, childsCount);
            }
        }

        private void notifyExpandFired(GroupViewHolder viewHolder) {
            long lastTimestamp = mAntiSpamTimestamp;
            mAntiSpamTimestamp = SystemClock.elapsedRealtime();
            if (mAntiSpamTimestamp - lastTimestamp < ANTI_SPAM_DELAY) return;

            G group = viewHolder.getItem();
            if (items.isExpanded(group)) {
                collapse(group);
                viewHolder.onCollapse(viewHolder, items.getGroupIndex(group));
            } else {
                expand(group);
                viewHolder.onExpand(viewHolder, items.getGroupIndex(group));
            }
        }

        public abstract class GroupViewHolder extends BaseViewHolder<G> {

            private View currentExpandFiringView = itemView;
            private final OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyExpandFired(GroupViewHolder.this);
                }
            };

            protected abstract void onExpand(GroupViewHolder viewHolder, int groupIndex);

            protected abstract void onCollapse(GroupViewHolder viewHolder, int groupIndex);

            public GroupViewHolder(ViewGroup parent, @LayoutRes int res) {
                super(parent, res);
                setExpandFiringView(itemView);
            }

            protected void setExpandFiringView(View firingView) {
                currentExpandFiringView.setOnClickListener(null);
                currentExpandFiringView = firingView;
                currentExpandFiringView.setOnClickListener(clickListener);
            }
        }

        public abstract class ChildViewHolder extends BaseViewHolder<CH> {

            public AnimationHelper animationHelper = new AnimationHelper();

            public ChildViewHolder(ViewGroup parent, @LayoutRes int res) {
                super(parent, res);
            }

            @Override
            protected void bind(CH item) {
                super.bind(item);
                animationHelper.visibleAdapterPosition = getAdapterPosition();
            }
        }
    }
    //endregion

    //region ViewHolders
    private static abstract class BaseViewHolder<T> extends OmegaRecyclerView.ViewHolder {
        private T item;

        BaseViewHolder(ViewGroup parent, @LayoutRes int res) {
            super(parent, res);
        }

        protected void bind(T item) {
            this.item = item;
            onBind(item);
        }

        @NonNull
        T getItem() {
            return item;
        }

        protected abstract void onBind(T item);
    }

    //endregion
}
