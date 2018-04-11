package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

// Doesn't work
public class ForegroundToBackgroundTransformer implements ItemTransformer {
    @Override
    public void transformItem(View view, float position) {
        float height = view.getHeight();
        float width = view.getWidth();
        float scale = min(position > 0 ? 1f : Math.abs(1f + position), 0.5f);

        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setPivotX(width * 0.5f);
        view.setPivotY(height * 0.5f);
        view.setTranslationX(position > 0 ? width * position : -width * position * 0.25f);
    }

    private float min(float val, float min) {
        return val < min ? min : val;
    }
}
