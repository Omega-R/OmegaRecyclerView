package com.omega_r.omegarecyclerview.expandable_example.core;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.omega_r.libs.omegarecyclerview_expandable.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview_expandable.animation.ExpandableItemAnimator;
import com.omega_r.libs.omegarecyclerview_expandable.animation.standard_animations.DropDownItemAnimator;
import com.omega_r.libs.omegarecyclerview_expandable.animation.standard_animations.FadeItemAnimator;
import com.omega_r.libs.omegarecyclerview_expandable.data.GroupProvider;
import com.omega_r.omegarecyclerview.R;

import java.util.Arrays;
import java.util.List;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class ExpandableActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final ExpandableItemAnimator FADE_ANIMATOR = new FadeItemAnimator();
    private static final ExpandableItemAnimator DROPDOWN_ANIMATOR = new DropDownItemAnimator();

    private OmegaExpandableRecyclerView mRecyclerView;
    private OmegaExpandableRecyclerView.Adapter mAdapter = provideAdapter();

    protected OmegaExpandableRecyclerView.Adapter provideAdapter() {
        return new ExpandableAdapter();
    }

    private List<? extends GroupProvider<QuoteGlobalInfo, Quote>> items;

    @LayoutRes
    protected int provideContentLayoutRes() {
        return R.layout.activity_expandable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentLayoutRes());

        createItems();

        setupRecyclerView();
        setupRadioButtons();
        fillAdapter();

        findViewById(R.id.button_test_update_child).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Quote quote = items.get(3).provideChilds().get(1);
                quote.setQuote("UPDATED QUOTE");
                mAdapter.notifyChildChanged(quote);
            }
        });
    }

    private void createItems() {
        items = Arrays.asList(
                SimpleData.from(
                        new QuoteGlobalInfo(getString(R.string.group_text_1), 1500),
                        new Quote(getString(R.string.child_text_1))
                ),
                SimpleData.from(
                        new QuoteGlobalInfo(getString(R.string.group_text_2), 1500),
                        new Quote(getString(R.string.child_text_2))
                ),
                SimpleData.from(
                        new QuoteGlobalInfo(getString(R.string.group_text_3), 1914),
                        new Quote(getString(R.string.child_text_3)),
                        new Quote(getString(R.string.child_text_5))
                ),
                SimpleData.from(
                        new QuoteGlobalInfo(getString(R.string.group_text_5), 1914),
                        new Quote(getString(R.string.child_text_1)),
                        new Quote(getString(R.string.child_text_2)),
                        new Quote(getString(R.string.child_text_3)),
                        new Quote(getString(R.string.child_text_4)),
                        new Quote(getString(R.string.child_text_5))
                )
        );
    }

    protected void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected void fillAdapter() {
        mAdapter.setItemsAsGroupProviders(items);
    }

    protected void setupRadioButtons() {
        RadioButton dropdownRadioButton = findViewById(R.id.radiobutton_dropdown);
        RadioButton fadeRadioButton = findViewById(R.id.radiobutton_fade);
        RadioButton singleRadioButton = findViewById(R.id.radiobutton_single);
        RadioButton multipleRadioButton = findViewById(R.id.radiobutton_multiple);

        switch (mRecyclerView.getChildExpandAnimation()) {
            case OmegaExpandableRecyclerView.CHILD_ANIM_DROPDOWN:
                dropdownRadioButton.setChecked(true);
                break;
            case OmegaExpandableRecyclerView.CHILD_ANIM_FADE:
                fadeRadioButton.setChecked(true);
                break;
        }

        switch (mRecyclerView.getChildExpandAnimation()) {
            case OmegaExpandableRecyclerView.CHILD_ANIM_DROPDOWN:
                dropdownRadioButton.setChecked(true);
                break;
            case OmegaExpandableRecyclerView.CHILD_ANIM_FADE:
                fadeRadioButton.setChecked(true);
                break;
        }

        dropdownRadioButton.setOnCheckedChangeListener(this);
        fadeRadioButton.setOnCheckedChangeListener(this);
        singleRadioButton.setOnCheckedChangeListener(this);
        multipleRadioButton.setOnCheckedChangeListener(this);
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
                case R.id.radiobutton_single:
                    mRecyclerView.setExpandMode(OmegaExpandableRecyclerView.EXPAND_MODE_SINGLE);
                    break;
                case R.id.radiobutton_multiple:
                    mRecyclerView.setExpandMode(OmegaExpandableRecyclerView.EXPAND_MODE_MULTIPLE);
                    break;
            }
        }
    }
}
