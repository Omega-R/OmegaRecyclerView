package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class RotateUpTransformer implements ItemTransformer {

    private static final float ROT_MOD = -15f;

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        float width = view.getWidth();
        int height = view.getHeight();
        float rotation;
        if (isHorizontal) {
            rotation = ROT_MOD * position;
            view.setPivotX(width * 0.5f);
            view.setPivotY(0f);
            view.setTranslationX(0f);
        } else {
            rotation = ROT_MOD * position * -1.25f;
            view.setPivotX(0);
            view.setPivotY(height * 0.5f);
        }
        view.setRotation(rotation);
    }
}
