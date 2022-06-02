package com.omega_r.libs.omegarecyclerview.header;

import java.util.HashMap;
import java.util.Map;

public enum SectionSize {

    CUSTOM(-1),
    FULL(0),
    DEFAULT(1);

    private final int value;
    private static final Map<Integer, SectionSize> map = new HashMap<>();

    SectionSize(int value) {
        this.value = value;
    }

    static {
        for (SectionSize sectionSize: SectionSize.values()) {
            map.put(sectionSize.value, sectionSize);
        }
    }

    public static SectionSize valueOf(int sectionSize) {
        return map.get(sectionSize);
    }

    public int getValue() {
        return value;
    }
}
