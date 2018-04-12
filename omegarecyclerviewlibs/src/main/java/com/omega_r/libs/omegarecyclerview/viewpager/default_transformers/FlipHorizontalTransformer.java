package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class FlipHorizontalTransformer implements ItemTransformer {

    @Override
    public void transformItem(View view, float position) {
        float rotation = 180f * position;
        view.setAlpha(rotation > 90f || rotation < -90f ? 0 : 1);
        view.setPivotY(view.getHeight() * 0.5f);
        view.setPivotX(view.getWidth() * 0.5f);
        view.setRotationY(rotation);
    }

}
