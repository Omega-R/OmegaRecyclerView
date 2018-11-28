package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class StackTransformer implements ItemTransformer {

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        if (isHorizontal) {
            view.setTranslationX((position < 0) ? 0f : -view.getWidth() * position);
        } else {
            view.setTranslationY((position < 0) ? 0f : -view.getHeight() * position);
        }
    }

}
