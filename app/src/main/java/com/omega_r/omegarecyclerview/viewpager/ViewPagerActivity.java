package com.omega_r.omegarecyclerview.viewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.omega_r.libs.omegarecyclerview.viewpager.ViewPagerOmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;
import com.omega_r.omegarecyclerview.pagination_example.Image;
import com.omega_r.omegarecyclerview.pagination_example.ImageAdapter;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class ViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ViewPagerOmegaRecyclerView recyclerView = findViewById(R.id.recyclerview);
        ImageAdapter adapter = new ImageAdapter();
        recyclerView.setAdapter(adapter);
        adapter.addValues(Image.createImageList(10));
    }

}

