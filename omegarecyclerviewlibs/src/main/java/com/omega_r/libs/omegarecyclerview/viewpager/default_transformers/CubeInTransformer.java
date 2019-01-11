package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class CubeInTransformer implements ItemTransformer {

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        if (isHorizontal) {
            view.setPivotX(position > 0 ? 0 : view.getWidth());
            view.setPivotY(0);
        } else {
            view.setPivotY(position > 0 ? 0 : view.getHeight());
            view.setPivotX(view.getWidth());
        }
        view.setRotation(-90f * position);
    }

}