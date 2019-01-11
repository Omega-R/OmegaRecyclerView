package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class ExpandableViewData<G, CH> {
    private final G mGroup;
    private final List<CH> mChilds;

    @Nullable
    private final Integer mStickyId;

    public ExpandableViewData(G group, List<CH> childs, @Nullable Integer stickyId) {
        mGroup = group;
        mChilds = childs;
        mStickyId = stickyId;
    }

    public G getGroup() {
        return mGroup;
    }

    public List<CH> getChilds() {
        return mChilds;
    }

    @Nullable
    public Integer getStickyId() {
        return mStickyId;
    }

    public boolean is(G otherGroup) {
        return mGroup.equals(otherGroup);
    }

    public Object get(PositionData positionData) {
        return positionData.isGroup ? mGroup : mChilds.get(positionData.childIndex);
    }

    @NonNull
    public static <G, CH> ExpandableViewData<G, CH> of(G group, @Nullable Integer stickyId, List<CH> childs) {
        return new ExpandableViewData<>(group, childs, stickyId);
    }

    @SafeVarargs
    @NonNull
    public static <G, CH> ExpandableViewData<G, CH> of(G group, @Nullable Integer stickyId, CH... childs) {
        return new ExpandableViewData<>(group, Arrays.asList(childs), stickyId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpandableViewData)) return false;
        ExpandableViewData<?, ?> that = (ExpandableViewData<?, ?>) o;
        return areObjectsEqual(mGroup, that.mGroup) &&
                areObjectsEqual(mChilds, that.mChilds) &&
                areObjectsEqual(mStickyId, that.mStickyId);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{mGroup, mChilds, mStickyId});
    }

    private boolean areObjectsEqual(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
