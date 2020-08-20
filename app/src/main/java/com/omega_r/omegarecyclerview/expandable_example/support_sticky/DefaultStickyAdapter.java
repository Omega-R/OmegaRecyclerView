package com.omega_r.omegarecyclerview.expandable_example.support_sticky;

import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.HeaderStickyDecoration;
import com.omega_r.libs.omegarecyclerview_expandable.OmegaExpandableRecyclerView;
import com.omega_r.libs.omegarecyclerview_expandable.data.ExpandableViewData;
import com.omega_r.omegarecyclerview.R;
import com.omega_r.omegarecyclerview.expandable_example.core.Quote;
import com.omega_r.omegarecyclerview.expandable_example.core.QuoteGlobalInfo;

public class DefaultStickyAdapter extends OmegaExpandableRecyclerView.Adapter<QuoteGlobalInfo, Quote> implements StickyAdapter<DefaultStickyAdapter.StickyViewHolder> {

    @Override
    protected ExGroupViewHolder provideGroupViewHolder(@NonNull ViewGroup viewGroup) {
        return new ExGroupViewHolder(viewGroup);
    }

    @Override
    protected ExChildViewHolder provideChildViewHolder(@NonNull ViewGroup viewGroup) {
        return new ExChildViewHolder(viewGroup);
    }

    @Override
    public long getStickyId(int position) {
        ExpandableViewData<QuoteGlobalInfo, Quote> item = getItem(position);
        if (item == null) return HeaderStickyDecoration.NO_STICKY_ID;
        Integer providedId = item.getStickyId();
        return providedId == null ? HeaderStickyDecoration.NO_STICKY_ID : providedId;
    }

    @Override
    public StickyViewHolder onCreateStickyViewHolder(ViewGroup parent) {
        return new StickyViewHolder(parent);
    }

    @Override
    public void onBindStickyViewHolder(StickyViewHolder viewHolder, int position) {
        viewHolder.bind(getItem(position).getGroup().getYear());
    }

    @Override
    public void onClickStickyViewHolder(long id) {
        // nothing
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
        protected void onBind(Quote item) {
            textView.setText(item.getQuote());
        }
    }

    class StickyViewHolder extends OmegaRecyclerView.ViewHolder {
        StickyViewHolder(ViewGroup parent) {
            super(parent, R.layout.sticky_header_test);
        }

        void bind(int year) {
            ((TextView)itemView).setText(String.valueOf(year));
        }
    }
}
