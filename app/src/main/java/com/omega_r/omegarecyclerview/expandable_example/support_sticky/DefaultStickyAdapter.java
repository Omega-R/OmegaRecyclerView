package com.omega_r.omegarecyclerview.expandable_example.support_sticky;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview.sticky_header.StickyHeaderAdapter;
import com.omega_r.omegarecyclerview.R;
import com.omega_r.omegarecyclerview.expandable_example.core.QuoteGlobalInfo;

public class DefaultStickyAdapter extends OmegaExpandableRecyclerView.Adapter<QuoteGlobalInfo, String> implements StickyHeaderAdapter<DefaultStickyAdapter.StickyVH> {

    @Override
    protected ExGroupViewHolder provideGroupViewHolder(@NonNull ViewGroup viewGroup) {
        return new ExGroupViewHolder(viewGroup);
    }

    @Override
    protected ExChildViewHolder provideChildViewHolder(@NonNull ViewGroup viewGroup) {
        return new ExChildViewHolder(viewGroup);
    }

    @Override
    public long getHeaderId(int position) {
        Integer providedId = getDataAtPosition(position).getStickyId();
        return providedId == null ? Integer.MIN_VALUE : providedId;
    }

    @Override
    public StickyVH onCreateHeaderViewHolder(ViewGroup parent) {
        return new StickyVH(parent);
    }

    @Override
    public void onBindHeaderViewHolder(StickyVH viewHolder, int position) {
        viewHolder.bind(getDataAtPosition(position).getGroup().getYear());
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
        protected void onBind(QuoteGlobalInfo item) {
            textView.setText(item.getTitle());
        }
    }

    class ExChildViewHolder extends ChildViewHolder {

        private TextView textView;

        ExChildViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_exp_child);
            textView = findViewById(R.id.textview_child_content);
        }

        @Override
        protected void onBind(String item) {
            textView.setText(item);
        }
    }

    class StickyVH extends OmegaRecyclerView.ViewHolder {
        StickyVH(ViewGroup parent) {
            super(parent, R.layout.sticky_header_test);
        }

        void bind(int year) {
            ((TextView)itemView).setText(String.valueOf(year));
        }
    }
}
