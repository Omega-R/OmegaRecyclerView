package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data;

import java.util.List;

public interface GroupProvider<G, CH> {
    G provideGroup();

    List<CH> provideChilds();
}
