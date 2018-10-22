package com.omega_r.omegarecyclerview.expandable_example.support_sticky;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.omegarecyclerview.expandable_example.core.ExpandableActivity;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class DefaultStickyExpandableActivity extends ExpandableActivity {

    @Override
    protected OmegaExpandableRecyclerView.Adapter provideAdapter() {
        return new DefaultStickyAdapter();
    }
}
