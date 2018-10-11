package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;


import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class Group<P, CH> {
    private final P mParent;
    private final List<CH> mChilds;

    public Group(P parent, List<CH> childs) {
        mParent = parent;
        mChilds = childs;
    }

    public P getParent() {
        return mParent;
    }

    public List<CH> getChilds() {
        return mChilds;
    }

    @NonNull
    public static <P, CH> Group<P, CH> of(P parent, List<CH> childs) {
        return new Group<P, CH>(parent, childs);
    }

    @NonNull
    public static <P, CH> Group<P, CH> of(P parent, CH... childs) {
        return new Group<P, CH>(parent, Arrays.asList(childs));
    }
}
