package com.omega_r.omegarecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.omega_r.omegarecyclerview.sticky_header_example.StickyHeaderFragment;
import com.omega_r.omegarecyclerview.test_example.TestFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestFragment testFragment = new TestFragment();
        StickyHeaderFragment stickyHeaderFragment = new StickyHeaderFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_test_first, testFragment)
                .add(R.id.fragment_test_second, stickyHeaderFragment)
                .commit();
    }
}
