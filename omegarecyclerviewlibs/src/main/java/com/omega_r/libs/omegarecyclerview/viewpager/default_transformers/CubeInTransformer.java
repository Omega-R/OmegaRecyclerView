package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class CubeInTransformer implements ItemTransformer {

    @Override
    public void transformItem(View view, float position) {
        view.setPivotX(position > 0 ? 0 : view.getWidth());
        view.setPivotY(0);
        view.setRotation(-90f * position);
    }

}
