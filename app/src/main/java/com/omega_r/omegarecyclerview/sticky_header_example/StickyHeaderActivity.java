package com.omega_r.omegarecyclerview.sticky_header_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderDecoration;
import com.omega_r.omegarecyclerview.R;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class StickyHeaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_heder);

        OmegaRecyclerView omegaRecyclerView = findViewById(R.id.recycler_list);
        StickyHeaderTestAdapter adapter = new StickyHeaderTestAdapter(this);
        StickyHeaderDecoration stickyHeaderDecoration = new StickyHeaderDecoration(adapter);

        stickyHeaderDecoration.setItemSpace(omegaRecyclerView.getItemSpace());
        omegaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        omegaRecyclerView.setAdapter(adapter);
        omegaRecyclerView.addItemDecoration(stickyHeaderDecoration);
    }
}
