package com.omega_r.omegarecyclerview.expandable_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.ExpandableViewData;
import com.omega_r.omegarecyclerview.R;

import java.util.Arrays;
import java.util.List;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class ExpandableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        ExpandableAdapter adapter = new ExpandableAdapter();
        recyclerView.setAdapter(adapter);

        List<ExpandableViewData<String, String>> items = Arrays.asList(
                ExpandableViewData.of(getString(R.string.group_text_1), getString(R.string.child_text_1)),
                ExpandableViewData.of(getString(R.string.group_text_2), getString(R.string.child_text_2)),
                ExpandableViewData.of(getString(R.string.group_text_3), getString(R.string.child_text_3)),
                ExpandableViewData.of(getString(R.string.group_text_4),
                        getString(R.string.child_text_1),
                        getString(R.string.child_text_2),
                        getString(R.string.child_text_3),
                        getString(R.string.child_text_4),
                        getString(R.string.child_text_5))
        );

        adapter.setItems(items);
    }
}
