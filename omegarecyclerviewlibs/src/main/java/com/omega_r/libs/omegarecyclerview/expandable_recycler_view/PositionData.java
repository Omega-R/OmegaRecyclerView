package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;

public class PositionData {
    int groupIndex;
    int childIndex;
    boolean isGroup;

    public PositionData(int groupIndex, int childIndex) {
        this.groupIndex = groupIndex;
        this.childIndex = childIndex;
        isGroup = false;
    }

    public PositionData(int groupIndex) {
        this.groupIndex = groupIndex;
        isGroup = true;
    }
}
