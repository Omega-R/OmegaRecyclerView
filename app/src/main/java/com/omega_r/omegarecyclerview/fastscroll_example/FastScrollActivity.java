package com.omega_r.omegarecyclerview.fastscroll_example;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.omega_r.omegarecyclerview.R;

public class FastScrollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastscroll);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(new FastScrollAdapter());
    }

}