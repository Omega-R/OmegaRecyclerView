package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class AccordionTransformer implements ItemTransformer {

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {

        if (isHorizontal) {
            view.setPivotX(position < 0 ? 0 : view.getWidth());
            view.setScaleX(position < 0 ? 1f + position : 1f - position);
        } else {
            view.setPivotY(position < 0 ? 0 : view.getHeight());
            view.setScaleY(position < 0 ? 1f + position : 1f - position);
        }
    }

}
