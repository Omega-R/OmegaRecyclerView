package com.omega_r.omegarecyclerview;

import android.app.Activity;
import android.os.Bundle;

import com.omega_r.omegarecyclerview.fragments.StickyHeaderFragment;
import com.omega_r.omegarecyclerview.fragments.TestFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestFragment testFragment = new TestFragment();
        StickyHeaderFragment stickyHeaderFragment = new StickyHeaderFragment();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_test_first, testFragment)
                .add(R.id.fragment_test_second, stickyHeaderFragment)
                .commit();
    }
}
