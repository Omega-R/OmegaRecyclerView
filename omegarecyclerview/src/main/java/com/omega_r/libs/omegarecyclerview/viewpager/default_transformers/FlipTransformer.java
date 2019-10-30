package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class FlipTransformer implements ItemTransformer {

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
//        if (-1 > position || position > 1) return;
        float rotation = 180f * position;
        if (isHorizontal) {
            view.setRotationY(rotation);
            view.setTranslationX(-view.getWidth() * position);
        } else {
            rotation = -rotation;
            view.setRotationX(rotation);
            view.setTranslationY(-view.getHeight() * position);
        }
        view.setAlpha(rotation > 90f || rotation < -90f ? 0 : 1);
        view.setPivotX(view.getWidth() * 0.5f);
        view.setPivotY(view.getHeight() * 0.5f);
    }
}
