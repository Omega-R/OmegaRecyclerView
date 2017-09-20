package com.omega_r.omegarecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.omega_r.omegarecyclerview.sticky_header_example.StickyHeaderActivity;
import com.omega_r.omegarecyclerview.swipe_menu_example.SwipeMenuActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button testButton = findViewById(R.id.button_swipe_menu);
        Button stickyHeaderButton = findViewById(R.id.button_sticky_header);

        testButton.setOnClickListener(this);
        stickyHeaderButton.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
