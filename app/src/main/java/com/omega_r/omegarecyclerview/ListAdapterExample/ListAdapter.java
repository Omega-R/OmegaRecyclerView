package com.omega_r.omegarecyclerview.ListAdapterExample;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.omega_r.libs.omegarecyclerview.BaseListAdapter;
import com.omega_r.omegarecyclerview.R;

public class ListAdapter extends BaseListAdapter<String> {

    public ListAdapter(BaseListAdapter.OnItemClickListener<String> clickListener,
                       BaseListAdapter.OnItemLongClickListener<String> longClickListener) {
        super(clickListener, longClickListener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SampleViewHolder(parent);
    }

    class SampleViewHolder extends ViewHolder {

        private TextView textView;

        SampleViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_string);
            textView = findViewById(R.id.textview);
        }

        @Override
        protected void onBind(String item) {
            textView.setText(item);
        }
    }
}
