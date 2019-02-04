package com.omega_r.libs.omegarecyclerview;


import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public abstract class BaseArrayAdapter<M, VH extends RecyclerView.ViewHolder> extends OmegaRecyclerView.Adapter<VH> {

    private M[] mArray;

    public BaseArrayAdapter(M[] array) {
        mArray = array;
    }

    public int getItemCount() {
        return mArray.length;
    }

    public M getItem(int position) {
        return mArray[position];
    }

    public void set(M... array) {
        mArray = array;
        tryNotifyDataSetChanged();
    }

    public void add(M... array) {
        int length = mArray.length;
        mArray = concat(mArray, array);
        tryNotifyItemRangeInserted(length, array.length);
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}