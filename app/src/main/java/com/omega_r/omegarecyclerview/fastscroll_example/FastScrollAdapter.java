package com.omega_r.omegarecyclerview.fastscroll_example;

import android.view.ViewGroup;
import android.widget.TextView;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.fast_scroll.SectionIndexer;
import com.omega_r.omegarecyclerview.R;

import androidx.annotation.NonNull;

public class FastScrollAdapter extends OmegaRecyclerView.Adapter<FastScrollAdapter.ViewHolder> implements SectionIndexer {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    @Override
    public String getSectionText(int position) {
        return String.valueOf(position);
    }

    class ViewHolder extends OmegaRecyclerView.ViewHolder {

        final TextView textView;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_text);
            textView = findViewById(R.id.text_item);
        }

    }

}
