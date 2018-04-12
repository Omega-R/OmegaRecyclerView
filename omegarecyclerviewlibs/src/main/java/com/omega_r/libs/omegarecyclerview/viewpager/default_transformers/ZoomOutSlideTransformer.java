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
    public void transformItem(View view, float position) {
        if (position >= -1 || position <= 1) {
            // Modify the default slide transition to shrink the page as well
            final float height = view.getHeight();
            final float scaleFactor = Math.max(mMinScale, 1 - Math.abs(position));
            final float vertMargin = height * (1 - scaleFactor) / 2;
            final float horzMargin = view.getWidth() * (1 - scaleFactor) / 2;

            // Center vertically
            view.setPivotY(0.5f * height);

            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(mMinScale + (scaleFactor - mMinScale) / (1 - mMinScale) * (1 - mMinScale));
        }
    }
}