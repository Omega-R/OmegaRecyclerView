package com.omega_r.omegarecyclerview.sticky_header_example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter;
import com.omega_r.omegarecyclerview.R;

public class StickyHeaderAdapter extends OmegaRecyclerView.Adapter<StickyHeaderAdapter.ViewHolder>
        implements StickyAdapter<StickyHeaderAdapter.HeaderHolder> {

    private LayoutInflater mInflater;
    private final Context mContext;

    public StickyHeaderAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public StickyHeaderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StickyHeaderAdapter.ViewHolder holder, int position) {
        holder.item.setText("Item " + position);
    }

    @Override
    public int getItemCount() {
        return 54;
    }

    @Override
    public long getStickyId(int position) {
        return (long) position / 6;
    }

    @Override
    public HeaderHolder onCreateStickyViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.sticky_header_test, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindStickyViewHolder(HeaderHolder viewHolder, int position) {
        viewHolder.header.setText("Header " + getStickyId(position));
    }

    @Override
    public void onClickStickyViewHolder(long id) {
        Toast.makeText(mContext, "onClickStickyViewHolder " + id, Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends OmegaRecyclerView.ViewHolder {
        public TextView item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    static class HeaderHolder extends OmegaRecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView;
        }
    }
}
