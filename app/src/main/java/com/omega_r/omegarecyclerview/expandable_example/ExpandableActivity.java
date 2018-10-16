package com.omega_r.omegarecyclerview.expandable_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations.DropDownItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations.FadeItemAnimator;
import com.omega_r.omegarecyclerview.R;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class ExpandableActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final ExpandableItemAnimator FADE_ANIMATOR = new FadeItemAnimator();
    private static final ExpandableItemAnimator DROPDOWN_ANIMATOR = new DropDownItemAnimator();

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable);

        mRecyclerView = findViewById(R.id.recyclerview);
        ExpandableAdapter adapter = new ExpandableAdapter();
        mRecyclerView.setAdapter(adapter);

        adapter.setItems(
                SimpleData.from(getString(R.string.group_text_1), getString(R.string.child_text_1)),
                SimpleData.from(getString(R.string.group_text_2), getString(R.string.child_text_2)),
                SimpleData.from(getString(R.string.group_text_3), getString(R.string.child_text_3)),
                SimpleData.from(getString(R.string.group_text_4),
                        getString(R.string.child_text_1),
                        getString(R.string.child_text_2),
                        getString(R.string.child_text_3),
                        getString(R.string.child_text_4),
                        getString(R.string.child_text_5))
        );

        ((RadioButton)findViewById(R.id.radiobutton_dropdown)).setOnCheckedChangeListener(this);
        ((RadioButton)findViewById(R.id.radiobutton_fade)).setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radiobutton_fade:
                    mRecyclerView.setItemAnimator(FADE_ANIMATOR);
                    break;
                case R.id.radiobutton_dropdown:
                    mRecyclerView.setItemAnimator(DROPDOWN_ANIMATOR);
                    break;
            }
        }
    }
}
