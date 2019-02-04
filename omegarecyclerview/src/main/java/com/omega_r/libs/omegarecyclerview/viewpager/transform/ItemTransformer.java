package com.omega_r.libs.omegarecyclerview.viewpager.transform;

import android.view.View;

public interface ItemTransformer {

    void transformItem(View view, float position, boolean isHorizontal, int scrolled);

}
