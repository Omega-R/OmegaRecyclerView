package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.data;

public class PositionData {
    public static final int POSITION_NOT_FOUND = -1;
    public int groupIndex;
    public int childIndex;
    public boolean isGroup;

    public PositionData(int groupIndex, int childIndex) {
        this.groupIndex = groupIndex;
        this.childIndex = childIndex;
        isGroup = false;
    }

    public PositionData(int groupIndex) {
        this.groupIndex = groupIndex;
        childIndex = POSITION_NOT_FOUND;
        isGroup = true;
    }
}
