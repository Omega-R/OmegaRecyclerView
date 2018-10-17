package com.omega_r.omegarecyclerview.expandable_example;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.omegarecyclerview.R;

import java.util.Random;

public class ExpandableAdapter extends OmegaExpandableRecyclerView.Adapter<String, String> {

    @Override
    protected ExGroupViewHolder provideGroupViewHolder(@NonNull ViewGroup viewGroup) {
        return new ExGroupViewHolder(viewGroup);
    }

    @Override
    protected ExChildViewHolder provideChildViewHolder(@NonNull ViewGroup viewGroup) {
        return new ExChildViewHolder(viewGroup);
    }

    class ExGroupViewHolder extends GroupViewHolder {

        private TextView textView;

        ExGroupViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_exp_group);
            textView = findViewById(R.id.textview_group_name);
        }

        @Override
        protected void onExpand(GroupViewHolder viewHolder, int groupIndex) {
            Toast.makeText(getContext(), "onExpand " + groupIndex, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCollapse(GroupViewHolder viewHolder, int groupIndex) {
            Toast.makeText(getContext(), "onCollapse " + groupIndex, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onBind(String item) {
            textView.setText(item);
        }
    }

    class ExChildViewHolder extends ChildViewHolder {

        private TextView textView;

        ExChildViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_exp_child);
            textView = findViewById(R.id.textview_child_content);
            if (Build.VERSION.SDK_INT >= 26) {
                contentView.setBackgroundColor(color());
            }
        }

        @RequiresApi(26)
        private int color() {
            int rnd = new Random().nextInt();
            float r = ((rnd >> 16) & 0xff) / 255.0f;
            float g = ((rnd >>  8) & 0xff) / 255.0f;
            float b = ((rnd      ) & 0xff) / 255.0f;
            return Color.argb(1f, r, g, b);
        }

        @Override
        protected void onBind(String item) {
            textView.setText(item);
        }
    }
}
