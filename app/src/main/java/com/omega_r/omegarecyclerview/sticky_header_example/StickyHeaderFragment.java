package com.omega_r.omegarecyclerview.sticky_header_example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderDecoration;
import com.omega_r.omegarecyclerview.R;

public class StickyHeaderFragment extends Fragment {

    public StickyHeaderFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sticky_header, container, false);
        OmegaRecyclerView omegaRecyclerView = view.findViewById(R.id.recycler_list);
        StickyHeaderTestAdapter adapter = new StickyHeaderTestAdapter(this.getActivity());
        StickyHeaderDecoration stickyHeaderDecoration = new StickyHeaderDecoration(adapter);

        stickyHeaderDecoration.setItemSpace(omegaRecyclerView.getItemSpace());
        omegaRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        omegaRecyclerView.setAdapter(adapter);
        omegaRecyclerView.addItemDecoration(stickyHeaderDecoration);
        return view;
    }
}
