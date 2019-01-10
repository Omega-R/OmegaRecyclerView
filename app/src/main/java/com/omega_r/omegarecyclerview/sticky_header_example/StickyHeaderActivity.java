package com.omega_r.omegarecyclerview.sticky_header_example;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class StickyHeaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_header);

        OmegaRecyclerView omegaRecyclerView = findViewById(R.id.recycler_list);
        omegaRecyclerView.setAdapter(new StickyHeaderAdapter(this));
    }
}
