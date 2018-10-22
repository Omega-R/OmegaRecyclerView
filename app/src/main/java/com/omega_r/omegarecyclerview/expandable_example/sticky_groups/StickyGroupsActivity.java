package com.omega_r.omegarecyclerview.expandable_example.sticky_groups;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.omegarecyclerview.R;
import com.omega_r.omegarecyclerview.expandable_example.core.ExpandableActivity;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class StickyGroupsActivity extends ExpandableActivity {

    protected OmegaExpandableRecyclerView.Adapter provideAdapter() {
        return new StickyAdapter();
    }

    @Override
    protected int provideContentLayoutRes() {
        return R.layout.activity_sticky_groups;
    }
}
