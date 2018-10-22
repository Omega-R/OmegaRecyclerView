package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        return new ExpandableViewData<G, CH>(group, childs, stickyId);
    }

    @SafeVarargs
    @NonNull
    public static <G, CH> ExpandableViewData<G, CH> of(G group, @Nullable Integer stickyId, CH... childs) {
        return new ExpandableViewData<G, CH>(group, Arrays.asList(childs), stickyId);
    }
}
