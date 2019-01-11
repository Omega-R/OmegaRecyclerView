package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Range implements Serializable {
    private List<Integer> mData = new ArrayList<>();

    @NonNull
    public static Range of(int from, int to) {
        return new Range(from, to);
    }

    @NonNull
    public static Range ofLength(int from, int length) {
        if (length == 0) {
            return Range.empty();
        }
        return new Range(from, from + length - 1);
    }

    @NonNull
    public static Range empty() {
        return new Range();
    }

    private Range() {
        // nothing
    }

    private Range(int from, int to) {
        if (to < from) {
            populateReversed(from, to);
        } else {
            populate(from, to);
        }
    }

    private void populate(int from, int to) {
        for (int i = from; i <= to; i++) {
            mData.add(i);
        }
    }

    private void populateReversed(int from, int to) {
        for (int i = from; i >= to; i--) {
            mData.add(i);
        }
    }

    public List<Integer> asList() {
        return mData;
    }

    public boolean contains(@NonNull Integer item) {
        return mData.contains(item);
    }

    public void clear() {
        mData.clear();
    }

    public void removeItem(Integer item) {
        mData.remove(item);
    }
}
