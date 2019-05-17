package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlatGroupingList<G, CH> {

    public static final int POSITION_NOT_FOUND = PositionData.POSITION_NOT_FOUND;
    public static final String KEY_STATES = "FlatGroupingList.KEY_STATES";

    private final List<ExpandableViewData<G, CH>> mItems;
    private final List<PositionData> mPositions = new ArrayList<>(); // represents mapping {visibleIndex : realPositionData}

    private boolean[] mExpandStates;

    public FlatGroupingList(@NonNull List<ExpandableViewData<G, CH>> items) {
        mItems = items;

        mExpandStates = new boolean[mItems.size()];
        Arrays.fill(mExpandStates, false);

        updateIndexes();
    }

    public int getVisibleItemsCount() {
        return mPositions.size();
    }

    public void onExpandStateChanged(G group, boolean isExpanded) {
        int pos = getUnflattedPosition(group);
        if (pos == POSITION_NOT_FOUND) throw new IllegalStateException("ExpandableViewData not in FlatGroupingList");

        mExpandStates[pos] = isExpanded;
        updateIndexes();
    }

    public ExpandableType getType(int visiblePosition) {
        PositionData positionData = mPositions.get(visiblePosition);
        return positionData.isGroup ? ExpandableType.GROUP : ExpandableType.CHILD;
    }

    public int getVisiblePosition(Object obj) {
        for (int i = 0; i < mPositions.size(); i++) {
            PositionData position = mPositions.get(i);
            ExpandableViewData<G, CH> data = mItems.get(position.groupIndex);
            if (data.get(position).equals(obj)) {
                return i;
            }
        }
        return POSITION_NOT_FOUND;
    }

    public boolean isExpanded(G group) {
        for (int i = 0; i < mItems.size(); i++) {
            ExpandableViewData<G, CH> expandableViewData = mItems.get(i);
            if (expandableViewData.is(group)) return mExpandStates[i];
        }
        return false;
    }

    public Object get(int visiblePosition) {
        PositionData positionData = mPositions.get(visiblePosition);
        ExpandableViewData<G, CH> expandableViewData = mItems.get(positionData.groupIndex);
        return expandableViewData.get(positionData);
    }

    public G getGroupOfPosition(int visiblePosition) {
        PositionData positionData = mPositions.get(visiblePosition);
        ExpandableViewData<G, CH> expandableViewData = mItems.get(positionData.groupIndex);
        return expandableViewData.getGroup();
    }

    public int getGroupIndex(G group) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getGroup().equals(group)) return i;
        }
        return POSITION_NOT_FOUND;
    }

    public int getChildsCount(G group) {
        for (ExpandableViewData<G, CH> expandableViewData : mItems) {
            if (expandableViewData.is(group)) return expandableViewData.getChilds().size();
        }
        return POSITION_NOT_FOUND;
    }

    public int getPositionInGroup(int visiblePosition) {
        return mPositions.get(visiblePosition).childIndex;
    }

    public int getChildsInGroup(int visiblePosition) {
        return mItems.get(mPositions.get(visiblePosition).groupIndex).getChilds().size();
    }

    public List<G> getExpandedGroups() {
        List<G> expandedGroups = new ArrayList<>();
        for (int i = 0; i < mExpandStates.length; i++) {
            if (mExpandStates[i]) expandedGroups.add(mItems.get(i).getGroup());
        }
        return expandedGroups;
    }

    private int getUnflattedPosition(G group) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).is(group)) return i;
        }
        return POSITION_NOT_FOUND;
    }

    private void updateIndexes() {
        mPositions.clear();
        for (int i = 0; i < mItems.size(); i++) {
            ExpandableViewData<G, CH> item = mItems.get(i);
            mPositions.add(new PositionData(i));
            if (mExpandStates[i]) {
                for (int j = 0; j < item.getChilds().size(); j++) {
                    mPositions.add(new PositionData(i, j));
                }
            }
        }
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putBooleanArray(KEY_STATES, mExpandStates);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_STATES)) {
            return;
        }
        mExpandStates = savedInstanceState.getBooleanArray(KEY_STATES);
        updateIndexes();
    }

    public List<ExpandableViewData<G, CH>> getItems() {
        return mItems;
    }

    @Nullable
    public ExpandableViewData<G, CH> getDataAtVisiblePosition(int pos) {
        if (0 > pos || pos > mPositions.size() - 1) return null;
        return mItems.get(mPositions.get(pos).groupIndex);
    }

    public ChildPosition getChildPositionAt(int visiblePosition) {
        PositionData positionData = mPositions.get(visiblePosition);
        ExpandableViewData<G, CH> data = mItems.get(positionData.groupIndex);
        if (positionData.childIndex == data.getChilds().size() - 1) {
            return ChildPosition.LAST;
        } else if (positionData.childIndex == 0) {
            return ChildPosition.FIRST;
        } else {
            return ChildPosition.OTHER;
        }
    }

    public enum ChildPosition {
        FIRST, LAST, OTHER
    }
}
