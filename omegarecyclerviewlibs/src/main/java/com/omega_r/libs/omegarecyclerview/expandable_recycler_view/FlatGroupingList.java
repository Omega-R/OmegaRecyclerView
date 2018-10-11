package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.List;

public class FlatGroupingList<P, CH> {

    public static final int POSITION_NOT_FOUND = -1;

    private final List<Item> mFlatList = new ArrayList<>();
    private final List<Group<P, CH>> mUnflatList;
    private final List<Integer> mIndexMapping = new ArrayList<>(); // represents mapping {visibleIndex : flatIndex}

    private final boolean[] mExpandStates;

    public FlatGroupingList(@NonNull List<Group<P, CH>> unflat) {
        mUnflatList = unflat;

        for (Group<P, CH> group : unflat) {
            P parent = group.getParent();

            mFlatList.add(new Item(parent, ExpandableType.GROUP, null));

            for (CH child : group.getChilds()) {
                mFlatList.add(new Item(child, ExpandableType.CHILD, parent));
            }
        }

        mExpandStates = new boolean[unflat.size()];
        for (int i = 0; i < mExpandStates.length; i++) {
            mExpandStates[i] = false;
        }

        updateIndexes();
    }

    public int getVisibleItemsCount() {
        int count = 0;

        for (int i = 0; i < mUnflatList.size(); i++) {
            count++;
            if (mExpandStates[i]) {
                count += mUnflatList.get(i).getChilds().size();
            }
        }
        return count;
    }

    public void onExpandStateChanged(P parent, boolean isExpanded) {
        int pos = getUnflattedPosition(parent);
        if (pos == POSITION_NOT_FOUND) throw new IllegalStateException("Group not in FlatGroupingList");

        mExpandStates[pos] = isExpanded;
        updateIndexes();
    }

    public ExpandableType getType(int visiblePosition) {
        return getItem(visiblePosition).getType();
    }

    public int getVisiblePosition(Object obj) {
        int flatIndex = getFlatPosition(obj);
        if (flatIndex == POSITION_NOT_FOUND) throw new IllegalStateException("Object not in list");
        return mIndexMapping.indexOf(flatIndex);
    }

    public boolean isExpanded(P parent) {
        for (int i = 0; i < mUnflatList.size(); i++) {
            Group<P, CH> group = mUnflatList.get(i);
            if (group.getParent().equals(parent)) return mExpandStates[i];
        }
        return false;
    }

    public Object get(int visiblePosition) {
        return getItem(visiblePosition).get();
    }

    public int getChildsCount(P parent) {
        for (Group<P, CH> group : mUnflatList) {
            if (group.getParent().equals(parent)) return group.getChilds().size();
        }
        return POSITION_NOT_FOUND;
    }

    private int getUnflattedPosition(Object obj) {
        for (int i = 0; i < mUnflatList.size(); i++) {
            Group<P, CH> group = mUnflatList.get(i);
            if (group.getParent().equals(obj)) return i;

            for (CH child : group.getChilds()) {
                if (child.equals(obj)) return i;
            }
        }
        return POSITION_NOT_FOUND;
    }

    private Item getItem(int visiblePosition) {
        return mFlatList.get(mIndexMapping.get(visiblePosition));
    }

    private int getFlatPosition(Object obj) {
        for (int i = 0; i < mFlatList.size(); i++) {
            if (mFlatList.get(i).get().equals(obj)) return i;
        }
        return POSITION_NOT_FOUND;
    }

    private void updateIndexes() {
        mIndexMapping.clear();
        boolean expandedFlag = false;
        int visibleIndex = 0;
        for (int i = 0; i < mFlatList.size(); i++) {
            Item item = mFlatList.get(i);
            if (item.isParent()) {
                expandedFlag = isExpanded(item.asParent());
                mIndexMapping.add(i);
            } else if (expandedFlag) {
                mIndexMapping.add(i);
            }
        }
    }

    private class Item {
        private Object object;
        private ExpandableType type;

        @Nullable
        private P parent;

        public Item(Object object, ExpandableType type, @Nullable P parent) {
            this.object = object;
            this.type = type;
            this.parent = parent;
        }

        @SuppressWarnings("unchecked")
        P asParent() {
            try {
                return (P)object;
            } catch (ClassCastException ex) {
                throw new IllegalStateException("Item is not an instance of Parent");
            }
        }

        @SuppressWarnings("unchecked")
        CH asChild() {
            try {
                return (CH)object;
            } catch (ClassCastException ex) {
                throw new IllegalStateException("Item is not an instance of Child");
            }
        }

        Object get() {
            return object;
        }

        ExpandableType getType() {
            return type;
        }

        @Nullable
        public P getParent() {
            return parent;
        }

        public boolean isParent() {
            return parent == null;
        }
    }
}
