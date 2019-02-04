package com.omega_r.omegarecyclerview.fastscroll_example;

import android.os.Bundle;
import android.util.Log;

import com.omega_r.libs.omegarecyclerview_fastscroll.FastScrollStateChangeListener;
import com.omega_r.libs.omegarecyclerview_fastscroll.OmegaFastScrollRecyclerView;
import com.omega_r.libs.omegarecyclerview_fastscroll.OmegaFastScrollerLayout;
import com.omega_r.omegarecyclerview.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FastScrollActivity extends AppCompatActivity implements FastScrollStateChangeListener {

    private static final String TAG = FastScrollActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastscroll);
        OmegaFastScrollRecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(new FastScrollAdapter());
        recyclerView.setFastScrollStateChangeListener(this);
    }

    @Override
    public void onFastScrollStart(OmegaFastScrollerLayout fastScrollerLayout) {
        Log.d(TAG, "onFastScrollStart");
    }

    @Override
    public void onFastScrollStop(OmegaFastScrollerLayout fastScrollerLayout) {
        Log.d(TAG, "onFastScrollStop");
    }

}
