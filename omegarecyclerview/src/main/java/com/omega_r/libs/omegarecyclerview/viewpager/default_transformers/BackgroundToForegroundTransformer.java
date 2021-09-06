package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class BackgroundToForegroundTransformer implements ItemTransformer {

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        float height = view.getHeight();
        float width = view.getWidth();
        float coefficient = (position < 0) ? 1f : Math.abs(1f - position);
        float scale = (coefficient < 0.5f) ? 0.5f : coefficient;
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setPivotX(width * 0.5f);
        view.setPivotY(height * 0.5f);

        if (isHorizontal) {
            view.setTranslationX(position < 0 ? width * position : -width * position * 0.25f);
        } else {
            view.setTranslationY(position < 0 ? height * position : -height * position * 0.25f);
        }
    }

}
