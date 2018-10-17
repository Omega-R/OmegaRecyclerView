package com.omega_r.omegarecyclerview.expandable_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.ExpandableItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations.DropDownItemAnimator;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation.standard_animations.FadeItemAnimator;
import com.omega_r.omegarecyclerview.R;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class ExpandableActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final ExpandableItemAnimator FADE_ANIMATOR = new FadeItemAnimator();
    private static final ExpandableItemAnimator DROPDOWN_ANIMATOR = new DropDownItemAnimator();

    private OmegaExpandableRecyclerView mRecyclerView;

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
                SimpleData.from(getString(R.string.group_text_3),
                        getString(R.string.child_text_3),
                        getString(R.string.child_text_5)),
                SimpleData.from(getString(R.string.group_text_4),
                        getString(R.string.child_text_1),
                        getString(R.string.child_text_2),
                        getString(R.string.child_text_3),
                        getString(R.string.child_text_4),
                        getString(R.string.child_text_5))
        );

        setupRadioButtons();
    }

    private void setupRadioButtons() {
        RadioButton dropdownRB = findViewById(R.id.radiobutton_dropdown);
        RadioButton fadeRB = findViewById(R.id.radiobutton_fade);
        switch (mRecyclerView.getChildAnimInt()) {
            case OmegaExpandableRecyclerView.CHILD_ANIM_DROPDOWN:
                dropdownRB.setChecked(true);
                break;
            case OmegaExpandableRecyclerView.CHILD_ANIM_FADE:
                fadeRB.setChecked(true);
                break;
        }
        dropdownRB.setOnCheckedChangeListener(this);
        fadeRB.setOnCheckedChangeListener(this);
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
