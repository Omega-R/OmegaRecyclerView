package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data;


import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class ExpandableViewData<G, CH> {
    private final G mGroup;
    private final List<CH> mChilds;

    public ExpandableViewData(G group, List<CH> childs) {
        mGroup = group;
        mChilds = childs;
    }

    public G getGroup() {
        return mGroup;
    }

    public List<CH> getChilds() {
        return mChilds;
    }

    public boolean is(G otherGroup) {
        return mGroup.equals(otherGroup);
    }

    public Object get(PositionData positionData) {
        return positionData.isGroup ? mGroup : mChilds.get(positionData.childIndex);
    }

    @NonNull
    public static <G, CH> ExpandableViewData<G, CH> of(G group, List<CH> childs) {
        return new ExpandableViewData<G, CH>(group, childs);
    }

    @NonNull
    public static <G, CH> ExpandableViewData<G, CH> of(G group, CH... childs) {
        return new ExpandableViewData<G, CH>(group, Arrays.asList(childs));
    }
}
