package com.omega_r.omegarecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.omega_r.omegarecyclerview.pagination_example.PaginationActivity;
import com.omega_r.omegarecyclerview.sticky_header_example.StickyHeaderActivity;
import com.omega_r.omegarecyclerview.swipe_menu_example.SwipeMenuActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_swipe_menu).setOnClickListener(this);
        findViewById(R.id.button_sticky_header).setOnClickListener(this);
        findViewById(R.id.button_pagination).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_swipe_menu:
                startActivity(new Intent(this, SwipeMenuActivity.class));
                break;
            case R.id.button_sticky_header:
                startActivity(new Intent(this, StickyHeaderActivity.class));
                break;
            case R.id.button_pagination:
                startActivity(new Intent(this, PaginationActivity.class));
                break;
            default:
                break;
        }
    }
}
