package com.omega_r.omegarecyclerview.expandable_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.omega_r.libs.omegaintentbuilder.AppOmegaIntentBuilder;
import com.omega_r.omegarecyclerview.R;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class ChooseExpandableActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_expandable);

        findViewById(R.id.button_core).setOnClickListener(this);
        findViewById(R.id.button_sticky_support).setOnClickListener(this);
        findViewById(R.id.button_group_sticky).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_core:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .expandableActivity()
                        .createIntentHandler()
                        .startActivity();
                break;
            case R.id.button_sticky_support:
                AppOmegaIntentBuilder.from(this)
                        .appActivities()
                        .defaultStickyExpandableActivity()
                        .createIntentHandler()
                        .startActivity();
                break;
            case R.id.button_group_sticky:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
