package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class CubeOutTransformer implements ItemTransformer {

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        if (isHorizontal) {
            view.setPivotX(position < 0f ? view.getWidth() : 0f);
            view.setPivotY(view.getHeight() * 0.5f);
            view.setRotationY(90f * position);
        } else {
            view.setPivotY(position < 0f ? view.getHeight() : 0f);
            view.setPivotX(view.getWidth() * 0.5f);
            view.setRotationX(-90f * position);
        }
    }


}
