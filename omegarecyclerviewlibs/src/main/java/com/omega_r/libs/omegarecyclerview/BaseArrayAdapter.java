package com.omega_r.libs.omegarecyclerview;


import android.support.v7.widget.RecyclerView;

import java.util.Arrays;

public abstract class BaseArrayAdapter<M, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

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
        notifyDataSetChanged();
    }

    public void add(M... array) {
        concatAll(mArray, array);
        notifyItemRangeInserted(mArray.length, array.length);
    }

    private static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;

        for (T[] array : rest) {
            totalLength += array.length;
        }

        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;

        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }
}