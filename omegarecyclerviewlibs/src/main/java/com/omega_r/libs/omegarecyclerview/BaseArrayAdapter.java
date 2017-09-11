package com.omega_r.libs.omegarecyclerview;


import android.support.v7.widget.RecyclerView;

import java.lang.reflect.Array;
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
        concat(mArray, array);
        notifyItemRangeInserted(mArray.length, array.length);
    }

    private static <T> T concat(T[] first, T[]... rest) {
        int totalLength = first.length + rest.length;

        T result = (T) Arrays.copyOf(first, totalLength);

        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(rest, 0, result, first.length, rest.length);

        return result;
    }
}