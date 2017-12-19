package com.omega_r.libs.omegarecyclerview;


import android.support.v7.widget.RecyclerView;

import java.util.Arrays;

public abstract class BaseArrayAdapter<M, VH extends RecyclerView.ViewHolder> extends OmegaRecyclerView.Adapter<VH> {

    private M[] mArray;
    private RecyclerView mRecyclerView;

    public BaseArrayAdapter(M[] array) {
        mArray = array;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return mArray.length;
    }

    public M getItem(int position) {
        return mArray[position];
    }

    public void set(M... array) {
        mArray = array;
        notifyDataSetChangedSafe();
    }

    public void add(M... array) {
        int length = mArray.length;
        mArray = concat(mArray, array);
        notifyItemRangeInsertedSafe(length, array.length);
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }


    protected void notifyDataSetChangedSafe() {
        if (!mRecyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        } else {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChangedSafe();
                }
            });
        }
    }


    protected void notifyItemRangeInsertedSafe(final int positionStart, final int itemCount) {
        if (!mRecyclerView.isComputingLayout()) {
            notifyItemRangeInserted(positionStart, itemCount);
        } else {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRangeInserted(positionStart, itemCount);
                }
            });
        }
    }

}