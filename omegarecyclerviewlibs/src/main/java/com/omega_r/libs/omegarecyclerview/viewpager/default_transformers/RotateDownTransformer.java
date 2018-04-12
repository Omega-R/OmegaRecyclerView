package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class RotateDownTransformer implements ItemTransformer {

    private static final float ROT_MOD = -15f;

    @Override
    public void transformItem(View view, float position) {
        float width = view.getWidth();
        float height = view.getHeight();
        float rotation = ROT_MOD * position * -1.25f;
        view.setPivotX(width * 0.5f);
        view.setPivotY(height);
        view.setRotation(rotation);
    }
}
