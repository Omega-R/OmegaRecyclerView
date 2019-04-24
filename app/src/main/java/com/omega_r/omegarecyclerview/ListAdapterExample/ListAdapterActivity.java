package com.omega_r.omegarecyclerview.ListAdapterExample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.BaseListAdapter;
import com.omega_r.omegarecyclerview.R;

import java.util.ArrayList;
import java.util.List;

import omega.com.annotations.OmegaActivity;

@OmegaActivity
public class ListAdapterActivity extends AppCompatActivity implements
        BaseListAdapter.OnItemClickListener<String>,
        BaseListAdapter.OnItemLongClickListener<String> {

    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter = new ListAdapter(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_adapter);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setAdapter(mAdapter);

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            items.add("Item" + i);
        }
        mAdapter.setItems(items);
    }

    @Override
    public void onItemClick(String item) {
        Toast.makeText(this, String.format("onItemClick(item = %s)", item), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(String item) {
        Toast.makeText(this, String.format("onItemLongClick(item = %s)", item), Toast.LENGTH_SHORT).show();
    }
}
