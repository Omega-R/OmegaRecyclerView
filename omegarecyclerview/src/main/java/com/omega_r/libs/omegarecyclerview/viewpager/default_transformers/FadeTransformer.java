package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class FadeTransformer implements ItemTransformer {

    private static final float DEFAULT_MIN_ALPHA = 0.6f;
    private float mMinAlpha;

    public FadeTransformer() {
        this(DEFAULT_MIN_ALPHA);
    }

    public FadeTransformer(float minAlpha) {
        mMinAlpha = minAlpha;
    }

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        if(position < -1 || position > 1) { // Page is not an immediate sibling, just make transparent
            view.setAlpha(mMinAlpha);
        } else if (position <= 0 || position <= 1) { // Page is sibling to left or right
            // Calculate alpha.  Position is decimal in [-1,0] or [0,1]
            float alpha = (position <= 0) ? position + 1 : 1 - position;
            view.setAlpha(alpha);
        } else if (position == 0) {
            view.setAlpha(1); // Page is active, make fully visible
        }
    }

}
