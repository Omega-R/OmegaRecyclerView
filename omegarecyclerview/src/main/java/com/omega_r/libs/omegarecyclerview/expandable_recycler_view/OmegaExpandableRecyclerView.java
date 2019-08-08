package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.R;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.AnimationHelper;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.OnAnimationEndListener;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations.DropDownItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations.FadeItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.ExpandableViewData;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.FlatGroupingList;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.GroupProvider;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.Range;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data.UniqueIdProvider;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.layout_manager.ExpandableLayoutManager;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.BaseStickyDecoration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OmegaExpandableRecyclerView extends OmegaRecyclerView {
    private static final String TAG = OmegaExpandableRecyclerView.class.getName();

    public static final int CHILD_ANIM_DEFAULT = 0;
    public static final int CHILD_ANIM_FADE = 1;
    public static final int CHILD_ANIM_DROPDOWN = 2;

    public static final int EXPAND_MODE_SINGLE = 0;
    public static final int EXPAND_MODE_MULTIPLE = 1;

    private static final String KEY_ADAPTER_DATA = "OmegaExpandableRecyclerView.KEY_ADAPTER_DATA";
    private static final String KEY_RECYCLER_DATA = "OmegaExpandableRecyclerView.KEY_RECYCLER_DATA";
    private static final int NO_RESOURCE = -1;

    @ExpandMode
    private int mExpandMode = EXPAND_MODE_SINGLE;

    @ExpandAnimation
    private int mChildExpandAnimation = CHILD_ANIM_DEFAULT;

    private boolean mShouldUseStickyGroups;

    @Nullable
    private Rect mHeaderRect;

    @Nullable
    private Adapter.GroupViewHolder mHeaderViewHolder;

    private boolean mIsTouchEventStartsInStickyHeader;

    @DrawableRes
    private int mItemsBackgroundRes;

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
        switch (mChildExpandAnimation) {
            case CHILD_ANIM_DEFAULT:
                return new DefaultItemAnimator();
            case CHILD_ANIM_DROPDOWN:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return new DropDownItemAnimator();
                } else {
                    Log.e(TAG, "DropDownItemAnimator supported only since Lollipop");
                    return new DefaultItemAnimator();
                }
            case CHILD_ANIM_FADE:
                return new FadeItemAnimator();
        }
        return null;
    }

    private void parseAttributes(AttributeSet attributeSet, int defStyleAttr) {
        TypedArray attrs = getContext().getTheme()
                .obtainStyledAttributes(attributeSet, R.styleable.OmegaExpandableRecyclerView, defStyleAttr, 0);
        try {
            mChildExpandAnimation = attrs.getInteger(R.styleable.OmegaExpandableRecyclerView_childAnimation, CHILD_ANIM_DEFAULT);
            mExpandMode = attrs.getInteger(R.styleable.OmegaExpandableRecyclerView_expandMode, EXPAND_MODE_SINGLE);
            mShouldUseStickyGroups = attrs.getBoolean(R.styleable.OmegaExpandableRecyclerView_stickyGroups, false);
            mItemsBackgroundRes = attrs.getResourceId(R.styleable.OmegaExpandableRecyclerView_backgrounds, NO_RESOURCE);
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

    @ExpandAnimation
    public int getChildExpandAnimation() {
        return mChildExpandAnimation;
    }

    public void setChildExpandAnimation(@ExpandAnimation int childExpandAnimation) {
        mChildExpandAnimation = childExpandAnimation;
        setItemAnimator(requestItemAnimator());
    }

    @ExpandMode
    public int getExpandMode() {
        return mExpandMode;
    }

    public void setExpandMode(@ExpandMode int expandMode) {
        mExpandMode = expandMode;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null) {
            bundle.putParcelable(KEY_ADAPTER_DATA, ((Adapter) adapter).onSaveInstanceState());
        }

        bundle.putParcelable(KEY_RECYCLER_DATA, super.onSaveInstanceState());

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            if (bundle.containsKey(KEY_RECYCLER_DATA)) {
                super.onRestoreInstanceState(bundle.getParcelable(KEY_RECYCLER_DATA));
            }
            if (bundle.containsKey(KEY_ADAPTER_DATA) && getAdapter() != null) {
                ((Adapter) getAdapter()).onRestoreInstanceState(bundle.getBundle(KEY_ADAPTER_DATA));
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void updateStickyDecoration(@Nullable RecyclerView.Adapter adapter) {
        BaseStickyDecoration stickyDecoration = getStickyDecoration();
        if (adapter != null && stickyDecoration != null) {
            if (adapter instanceof OmegaExpandableRecyclerView.Adapter
                && stickyDecoration instanceof ExpandableStickyDecoration) {
                ((ExpandableStickyDecoration) stickyDecoration).setExpandableAdapter((Adapter) adapter);
            }
        }
        super.updateStickyDecoration(adapter);
    }

    @Nullable
    @Override
    protected BaseStickyDecoration provideStickyDecoration(@NonNull RecyclerView.Adapter adapter, @Nullable StickyAdapter stickyAdapter) {
        if (adapter instanceof OmegaExpandableRecyclerView.Adapter && mShouldUseStickyGroups) {
            return new ExpandableStickyDecoration(stickyAdapter, (Adapter) adapter);
        } else {
            return super.provideStickyDecoration(adapter, stickyAdapter);
        }
    }

    public void notifyHeaderPosition(Adapter.GroupViewHolder headerHolder, Rect viewRect) {
        mHeaderViewHolder = headerHolder;
        mHeaderRect = viewRect;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mHeaderRect != null && mHeaderViewHolder != null && getAdapter() != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                    boolean shouldReturn = false;
                    if (isEventInRect(ev, mHeaderRect) && mIsTouchEventStartsInStickyHeader) {
                        ((Adapter) getAdapter()).notifyExpandFired(mHeaderViewHolder);
                        shouldReturn = true;
                    }
                    mIsTouchEventStartsInStickyHeader = false;
                    if (shouldReturn) return true;
                    break;
                case MotionEvent.ACTION_DOWN:
                    mIsTouchEventStartsInStickyHeader = isEventInRect(ev, mHeaderRect);
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isEventInRect(MotionEvent ev, Rect rect) {
        return ev.getX() >= rect.left && ev.getX() <= rect.right &&
                ev.getY() >= rect.top && ev.getY() <= rect.bottom;
    }

    @DrawableRes
    public int getItemsBackgroundRes() {
        return mItemsBackgroundRes;
    }

    public void setItemsBackgroundRes(@DrawableRes int itemsBackgroundRes) {
        mItemsBackgroundRes = itemsBackgroundRes;
    }

    private void subscribeOnRemoveItemAnimationEnd(OnAnimationEndListener listener) {
        ItemAnimator itemAnimator = getItemAnimator();
        if (itemAnimator instanceof ExpandableItemAnimator) {
            ((ExpandableItemAnimator) itemAnimator).subscribeOnRemoveAnimationEnd(listener);
        }
    }

    private void subscribeOnAddItemAnimationEnd(OnAnimationEndListener listener) {
        ItemAnimator itemAnimator = getItemAnimator();
        if (itemAnimator instanceof ExpandableItemAnimator) {
            ((ExpandableItemAnimator) itemAnimator).subscribeOnAddAnimationEnd(listener);
        }
    }
    // endregion

    //region Adapter
    public static abstract class Adapter<G, CH> extends OmegaRecyclerView.Adapter<BaseViewHolder> {

        static final int VH_TYPE_GROUP = 238956;
        static final int VH_TYPE_CHILD = 238957;

        private static final long ANTI_SPAM_DELAY = 400;

        private final Map<GroupViewHolder, G> attachedGroupViewHolders = new HashMap<>();

        private long antiSpamTimestamp = SystemClock.elapsedRealtime();

        private FlatGroupingList<G, CH> items;
        private OmegaExpandableRecyclerView recyclerView;

        private Drawable defaultGroupDrawable;
        private Drawable defaultChildDrawable;

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
                expandableViewData.add(ExpandableViewData.of(groupProvider.provideGroup(), groupProvider.provideStickyId(), groupProvider.provideChilds()));
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
            switch (items.getType(position)) {
                case GROUP:
                    GroupViewHolder groupViewHolder = (GroupViewHolder) baseViewHolder;
                    groupViewHolder.bind((G) items.get(position));
                    groupViewHolder.updateBackground();
                    attachedGroupViewHolders.put(groupViewHolder, groupViewHolder.getItem());
                    break;
                case CHILD:
                    ((ChildViewHolder) baseViewHolder).bindWithBackground(
                            (CH) items.get(position),
                            recyclerView.mItemsBackgroundRes,
                            getChildDrawableLevel(position));
                    break;
            }
        }

        private int getChildDrawableLevel(int position) {
            switch (items.getChildPositionAt(position)) {
                case LAST:
                    return getResources().getInteger(R.integer.backgroundLastChild);
                case FIRST:
                    return getResources().getInteger(R.integer.backgroundFirstChild);
            }
            return getResources().getInteger(R.integer.backgroundChild);
        }

        @SuppressWarnings("unchecked")
        public void bindGroupViewHolder(@NonNull BaseViewHolder baseViewHolder, int position) {
            baseViewHolder.bind(items.getGroupOfPosition(position));
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

            List<GroupViewHolder> groupViewHolders = getViewHoldersOf(group);
            if (groupViewHolders != null) {
                for (GroupViewHolder holder : groupViewHolders) {
                    holder.onCollapse(holder, items.getGroupIndex(group));
                    holder.onAnimationEnd(); // don't subscribe - expand background ASAP
                }
            }
        }

        public void collapse(G group) {
            List<GroupViewHolder> groupViewHolders = getViewHoldersOf(group);
            if (groupViewHolders != null) {
                for (GroupViewHolder holder : groupViewHolders) {
                    recyclerView.subscribeOnRemoveItemAnimationEnd(holder);
                    holder.onExpand(holder, items.getGroupIndex(group));
                }
            }

            items.onExpandStateChanged(group, false);

            int childsCount = items.getChildsCount(group);
            if (childsCount > 0) {
                tryNotifyItemRangeRemoved(items.getVisiblePosition(group) + 1, childsCount);
            }
        }

        // there is possible situation, when exists two viewHolders (oldState and newState) for one item while in animation transition. We need to handle both.
        @Nullable
        private List<GroupViewHolder> getViewHoldersOf(G group) {
            List<GroupViewHolder> result = new ArrayList<>();
            if (!attachedGroupViewHolders.containsValue(group)) return null;
            for (Map.Entry<GroupViewHolder, G> entry : attachedGroupViewHolders.entrySet()) {
                if (entry.getValue().equals(group)) result.add(entry.getKey());
            }
            return result;
        }

        private void notifyExpandFired(GroupViewHolder viewHolder) {
            long lastTimestamp = antiSpamTimestamp;
            antiSpamTimestamp = SystemClock.elapsedRealtime();
            if (antiSpamTimestamp - lastTimestamp < ANTI_SPAM_DELAY) return;

            G group = viewHolder.getItem();
            if (isExpanded(group)) {
                collapse(group);
            } else {
                expand(group);
            }
        }

        public boolean isExpanded(G group) {
            return items.isExpanded(group);
        }

        protected Parcelable onSaveInstanceState() {
            return items.onSaveInstanceState();
        }

        protected void onRestoreInstanceState(Bundle savedInstanceState) {
            items.onRestoreInstanceState(savedInstanceState);
            tryNotifyDataSetChanged();
        }

        public List<ExpandableViewData<G, CH>> getItems() {
            return items.getItems();
        }

        @Nullable
        public ExpandableViewData<G, CH> getItem(int position) {
            return items.getDataAtVisiblePosition(position);
        }

        public long getGroupUniqueId(int position) {
            G group = getItem(position).getGroup();
            if (group instanceof UniqueIdProvider) {
                return ((UniqueIdProvider) group).provideUniqueId();
            } else {
                return group.hashCode();
            }
        }

        public Context getContext() {
            return recyclerView.getContext();
        }

        public Resources getResources() {
            return getContext().getResources();
        }

        public abstract class GroupViewHolder extends BaseViewHolder<G> implements OnAnimationEndListener {

            private final int expandedResLevel;
            private final int collapsedResLevel;

            private boolean isBackgroundSet = false;

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
                expandedResLevel = getResources().getInteger(R.integer.backgroundGroupExpanded);
                collapsedResLevel = getResources().getInteger(R.integer.backgroundGroupCollapsed);
                setExpandFiringView(itemView);
            }

            protected void setExpandFiringView(View firingView) {
                currentExpandFiringView.setOnClickListener(null);
                currentExpandFiringView = firingView;
                currentExpandFiringView.setOnClickListener(clickListener);
            }

            @CallSuper
            @Override
            public void bind(G item) {
                super.bind(item);
                isBackgroundSet = false;
                if (defaultGroupDrawable == null) defaultGroupDrawable = itemView.getBackground();
            }

            private void updateBackground() {
                if (isBackgroundSet) {
                    Drawable background = itemView.getBackground();
                    if (background != null) {
                        background.setLevel(isExpanded() ? expandedResLevel : collapsedResLevel);
                    }
                } else {
                    if (isExpanded()) {
                        changeBackground(itemView, defaultGroupDrawable, recyclerView.mItemsBackgroundRes, expandedResLevel);
                    } else {
                        changeBackground(itemView, defaultGroupDrawable, recyclerView.mItemsBackgroundRes, collapsedResLevel);
                    }
                }

                isBackgroundSet = true;
            }

            @Override
            public void onAnimationEnd() {
                updateBackground();
            }

            private boolean isExpanded() {
                return Adapter.this.isExpanded(getItem());
            }
        }

        public abstract class ChildViewHolder extends BaseViewHolder<CH> {

            public View contentView;

            public final AnimationHelper animationHelper = new AnimationHelper();

            public ChildViewHolder(ViewGroup parent, @LayoutRes int res) {
                this(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
            }

            private ChildViewHolder(View view) {
                super(new ChildClippingFrameLayout(view));
                contentView = view;
            }

            @Override
            public void bind(CH item) {
                super.bind(item);
                animationHelper.visibleAdapterPosition = getAdapterPosition();
                if (defaultChildDrawable == null) defaultChildDrawable = contentView.getBackground();
            }

            private void bindWithBackground(CH item, @DrawableRes int backgroundsRes, int backgroundDrawableLevel) {
                bind(item);
                changeBackground(contentView, defaultChildDrawable, backgroundsRes, backgroundDrawableLevel);
            }
        }
    }
    //endregion

    //region ViewHolders
    static abstract class BaseViewHolder<T> extends OmegaRecyclerView.ViewHolder {
        private T item;

        BaseViewHolder(ViewGroup parent, @LayoutRes int res) {
            super(parent, res);
        }

        BaseViewHolder(View view) {
            super(view);
        }

        @CallSuper
        public void bind(T item) {
            this.item = item;
            onBind(item);
        }

        @NonNull
        public T getItem() {
            return item;
        }

        protected abstract void onBind(T item);

        void changeBackground(View view, Drawable defaultDrawable, @DrawableRes int backgroundsRes, int drawableLevel) {
            if (backgroundsRes == NO_RESOURCE) {
                setBackground(view, defaultDrawable);
            } else {
                Drawable drawable = getResources().getDrawable(backgroundsRes);
                drawable.setLevel(drawableLevel);
                setBackground(view, drawable);
            }
        }

        private void setBackground(View view, Drawable defaultDrawable) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(defaultDrawable);
            } else {
                view.setBackground(defaultDrawable);
            }
        }
    }

    //endregion

    @IntDef({CHILD_ANIM_DEFAULT, CHILD_ANIM_FADE, CHILD_ANIM_DROPDOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExpandAnimation {
        // nothing
    }

    @IntDef({EXPAND_MODE_SINGLE, EXPAND_MODE_MULTIPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExpandMode {
        // nothing
    }
}
