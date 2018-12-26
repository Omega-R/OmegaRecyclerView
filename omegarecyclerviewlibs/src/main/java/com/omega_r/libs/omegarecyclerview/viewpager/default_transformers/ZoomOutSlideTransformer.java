package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class ZoomOutSlideTransformer implements ItemTransformer {

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    private final float mMinScale;
    private final float mMinAlpha;

    public ZoomOutSlideTransformer() {
        this(MIN_SCALE, MIN_ALPHA);
    }

    public ZoomOutSlideTransformer(float minScale, float minAlpha) {
        mMinScale = minScale;
        mMinAlpha = minAlpha;
    }

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        float height = view.getHeight();
        float scaleFactor = Math.max(mMinScale, 1 - Math.abs(position));
        int width = view.getWidth();
        float vertMargin = height * (1 - scaleFactor) / 2;
        float horzMargin = width * (1 - scaleFactor) / 2;

        if (isHorizontal) {
            view.setPivotY(0.5f * height);
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }
        } else {
            view.setPivotX(0.5f * width);
            if (position < 0) {
                view.setTranslationY(vertMargin - horzMargin / 2);
            } else {
                view.setTranslationY(-vertMargin + horzMargin / 2);
            }
        }
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
        view.setAlpha(mMinAlpha + (scaleFactor - mMinScale) / (1 - mMinScale) * (1 - mMinAlpha));
    }
}