package com.omega_r.omegarecyclerview.fastscroll_example;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.omega_r.omegarecyclerview.R;

public class FastScrollOriginalActivity extends AppCompatActivity  {

    private static final String TAG = FastScrollOriginalActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastscroll_original);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(new FastScrollAdapter());
//        recyclerView.setFastScrollStateChangeListener(this);
    }

//    @Override
//    public void onFastScrollStart(OmegaFastScrollerLayout fastScrollerLayout) {
//        Log.d(TAG, "onFastScrollStart");
//    }
//
//    @Override
//    public void onFastScrollStop(OmegaFastScrollerLayout fastScrollerLayout) {
//        Log.d(TAG, "onFastScrollStop");
//    }

}
