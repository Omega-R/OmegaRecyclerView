package com.omega_r.omegarecyclerview.fragments;


import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderDecoration;
import com.omega_r.omegarecyclerview.adapter.StickyTestAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class StickyHeaderFragment extends BaseFragment {

    private StickyHeaderDecoration decor;

    public StickyHeaderFragment() {
        // Required empty public constructor
    }

    @Override
    protected void setAdapterAndDecor(RecyclerView list) {
        final StickyTestAdapter adapter = new StickyTestAdapter(this.getActivity());
        decor = new StickyHeaderDecoration(adapter);
        setHasOptionsMenu(true);

        list.setAdapter(adapter);
        list.addItemDecoration(decor, 1);
    }

}
