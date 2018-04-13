package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class DepthPageTransformer implements ItemTransformer {

    private static final float DEFAULT_MIN_SCALE = 0.75f;
    private float mMinScale;

    public DepthPageTransformer() {
        this(DEFAULT_MIN_SCALE);
    }

    public DepthPageTransformer(float minScale) {
        mMinScale = minScale;
    }

    @Override
    public void transformItem(View view, float position, boolean isHorizontal) {
        if (position < 0f) {
            view.setTranslationX(0f);
            view.setTranslationY(0f);
            view.setScaleX(1f);
            view.setScaleY(1f);
            view.setAlpha(1f);
            return;
        }

        float scaleFactor = mMinScale + (1 - mMinScale) * (1 - Math.abs(position));
        view.setAlpha(1 - position);
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
        if (isHorizontal) {
            view.setPivotY(0.5f * view.getHeight());
            view.setTranslationX(view.getWidth() * -position);
        } else {
            view.setPivotX(0.5f * view.getWidth());
            view.setTranslationY(view.getHeight() * -position);
        }
    }
}
