package com.omega_r.omegarecyclerview.pagination_example;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView;
import com.omega_r.libs.omegarecyclerview.pagination.PaginationViewCreator;
import com.omega_r.omegarecyclerview.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends OmegaRecyclerView.Adapter<ImageAdapter.ViewHolder> implements PaginationViewCreator {

    private List<Image> mList = new ArrayList<>();
    @Nullable
    private Callback mCallback;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageResource(mList.get(position).getImageRes());
    }

    public void addValues(List<Image> list) {
        mList.addAll(list);
        notifyItemInserted(mList.size() - list.size());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Nullable
    @Override
    public View createPaginationView(ViewGroup parent, LayoutInflater inflater) {
        return inflater.inflate(R.layout.item_progress, parent, false);
    }

    @Nullable
    @Override
    public View createPaginationErrorView(ViewGroup parent, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.item_error_loading, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onRetryClicked();
                }
            }
        });
        return view;
    }

    public void setCallback(@Nullable Callback callback) {
        mCallback = callback;
    }

    class ViewHolder extends OmegaRecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Clicked " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    interface Callback {
        void onRetryClicked();
    }
}
