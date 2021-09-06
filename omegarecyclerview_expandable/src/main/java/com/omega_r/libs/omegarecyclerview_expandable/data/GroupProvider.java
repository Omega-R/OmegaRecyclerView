package com.omega_r.libs.omegarecyclerview_expandable.data;

import androidx.annotation.Nullable;

import java.util.List;

public interface GroupProvider<G, CH> {
    G provideGroup();

    List<CH> provideChilds();

    @Nullable
    Integer provideStickyId();
}
