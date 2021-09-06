package com.omega_r.omegarecyclerview;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.omega_r.libs.omegaintentbuilder.AppOmegaIntentBuilder;
import com.omega_r.libs.omegaintentbuilder.OmegaIntentBuilder;
import com.omega_r.omegarecyclerview.fastscroll_example.FastScrollActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_swipe_menu).setOnClickListener(this);
        findViewById(R.id.button_sticky_header).setOnClickListener(this);
        findViewById(R.id.button_pagination).setOnClickListener(this);
        findViewById(R.id.button_sections).setOnClickListener(this);
        findViewById(R.id.button_viewpager).setOnClickListener(this);
        findViewById(R.id.button_list_adapter).setOnClickListener(this);
        findViewById(R.id.button_expandable_recyclerview).setOnClickListener(this);
        findViewById(R.id.button_fastscroll).setOnClickListener(this);
    }

    /**
     * AppOmegaIntentBuilder is amazing library for start different intents
     * See on GitHub {@link "https://github.com/Omega-R/OmegaIntentBuilder"}.
     */
    @Override
    public void onClick(View view) {
        
        switch (view.getId()) {
            case R.id.button_swipe_menu:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .swipeMenuActivity()
                        .startActivity();
                break;
            case R.id.button_sticky_header:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .stickyHeaderActivity()
                        .startActivity();
                break;
            case R.id.button_pagination:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .paginationActivity()
                        .startActivity();
                break;
            case R.id.button_sections:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .sectionsActivity()
                        .startActivity();
                break;
            case R.id.button_viewpager:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .viewPagerActivity()
                        .startActivity();
                break;
            case R.id.button_list_adapter:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .listAdapterActivity()
                        .startActivity();
                break;
            case R.id.button_expandable_recyclerview:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .chooseExpandableActivity()
                        .startActivity();
                break;
            case R.id.button_fastscroll:
                OmegaIntentBuilder.from(this)
                        .activity(FastScrollActivity.class)
                        .startActivity();
                break;
        }
    }
}
