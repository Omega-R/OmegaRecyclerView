package com.omega_r.libs.omegarecyclerview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

public abstract class BaseListAdapter<T> extends OmegaRecyclerView.Adapter<BaseListAdapter<T>.ViewHolder> {

    private static final int INDEX_NOT_FOUND = -1;

    private List<T> items;

    @Nullable
    private OnItemClickListener<T> clickListener = null;

    @Nullable
    private OnItemLongClickListener<T> longClickListener = null;


    protected abstract BaseListAdapter<T>.ViewHolder provideViewHolder(ViewGroup parent);

    public BaseListAdapter(@NonNull List<T> items,
                           @Nullable OnItemClickListener<T> clickListener,
                           @Nullable OnItemLongClickListener<T> longClickListener) {
        this.items = items;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public BaseListAdapter(@NonNull List<T> items, @Nullable OnItemClickListener<T> clickListener) {
        this(items, clickListener, null);
    }

    public BaseListAdapter(@NonNull List<T> items, @Nullable OnItemLongClickListener<T> longClickListener) {
        this(items, null, longClickListener);
    }

    public BaseListAdapter(@Nullable OnItemClickListener<T> clickListener, @Nullable OnItemLongClickListener<T> longClickListener) {
        this(Collections.<T>emptyList(), clickListener, longClickListener);
    }

    public BaseListAdapter(@NonNull List<T> items) {
        this(items, null, null);
    }

    public BaseListAdapter(@Nullable OnItemClickListener<T> clickListener) {
        this(Collections.<T>emptyList(), clickListener, null);
    }

    public BaseListAdapter(@Nullable OnItemLongClickListener<T> longClickListener) {
        this(Collections.<T>emptyList(), null, longClickListener);
    }

    public BaseListAdapter() {
        this(Collections.<T>emptyList(), null, null);
    }

    public final void setClickListener(@Nullable OnItemClickListener<T> clickListener) {
        this.clickListener = clickListener;
        tryNotifyDataSetChanged();
    }

    public final void setLongClickListener(@Nullable OnItemLongClickListener<T> longClickListener) {
        this.longClickListener = longClickListener;
        tryNotifyDataSetChanged();
    }

    public final void addItems(@NonNull List<T> items) {
        this.items.addAll(items);
        tryNotifyDataSetChanged();
    }

    public final void setItems(@NonNull List<T> items) {
        this.items = items;
        tryNotifyDataSetChanged();
    }

    protected final T getItem(int position) {
        return items.get(position);
    }

    protected final List<T> getItems() {
        return items;
    }

    private void onItemClick(T item) {
        if (clickListener != null) {
            clickListener.onItemClick(item);
        }
    }

    private void onLongItemClick(T item) {
        if (longClickListener != null) {
            longClickListener.onItemLongClick(item);
        }
    }

    @NonNull
    @Override
    public BaseListAdapter<T>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return provideViewHolder(parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull BaseListAdapter<T>.ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public abstract class ViewHolder extends OmegaRecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private T item;

        public ViewHolder(View v) {
            super(v);
        }

        public ViewHolder(ViewGroup parent, int res) {
            super(parent, res);
        }

        private void bind(T item) {
            this.item = item;

            if (clickListener != null) {
                itemView.setOnClickListener(this);
            }
            if (longClickListener != null) {
                itemView.setOnLongClickListener(this);
            }

            onBind(item);
        }

        protected abstract void onBind(T item);

        @NonNull
        protected T getItem() {
            return item;
        }

        @Override
        public void onClick(View v) {
            onItemClick(item);
        }

        @Override
        public boolean onLongClick(View v) {
            onLongItemClick(item);
            return true;
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(T item);
    }
}