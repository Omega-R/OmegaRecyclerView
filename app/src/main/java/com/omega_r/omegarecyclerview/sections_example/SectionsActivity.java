package com.omega_r.omegarecyclerview.sections_example;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.omega_r.libs.omegaintentbuilder.OmegaIntentBuilder;
import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.omegarecyclerview.R;
import com.omega_r.omegarecyclerview.pagination_example.Image;
import com.omega_r.omegarecyclerview.pagination_example.ImageAdapter;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class SectionsActivity extends AppCompatActivity implements View.OnClickListener {

    private OmegaRecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);
        mRecyclerView = findViewById(R.id.recyclerview);
        findViewById(R.id.textview_header).setOnClickListener(this);
        findViewById(R.id.textview_footer).setOnClickListener(this);
        mAdapter = new ImageAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addValues(Image.createImageList(4));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_header:
                Toast.makeText(getApplicationContext(), mRecyclerView.getAdapter().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.textview_footer:
                OmegaIntentBuilder.from(this)
                        .email()
                        .text("Hello world")
                        .emailTo("develop@omega-r.com")
                        .subject("Great library")
                        .createIntentHandler()
                        .failToast("Sorry, you don't have app for sending email")
                        .startActivity();
                break;
        }
    }
}
