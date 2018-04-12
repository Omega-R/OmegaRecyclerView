package com.omega_r.libs.omegarecyclerview.viewpager.default_transformers;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;

import com.omega_r.libs.omegarecyclerview.viewpager.transform.ItemTransformer;

public class TabletTransformer implements ItemTransformer {

    private final Matrix mOffsetMatrix = new Matrix();
    private final Camera mOffsetCamera = new Camera();
    private final float[] mOffset = new float[2];

    @Override
    public void transformItem(View view, float position) {
        final float rotation = (position < 0 ? 30f : -30f) * Math.abs(position);

        view.setTranslationX(getOffsetXForRotation(rotation, view.getWidth(), view.getHeight()));
        view.setPivotX(view.getWidth() * 0.5f);
        view.setPivotY(0);
        view.setRotationY(rotation);
    }

    private float getOffsetXForRotation(float degrees, int width, int height) {
        mOffsetMatrix.reset();
        mOffsetCamera.save();
        mOffsetCamera.rotateY(Math.abs(degrees));
        mOffsetCamera.getMatrix(mOffsetMatrix);
        mOffsetCamera.restore();

        mOffsetMatrix.preTranslate(-width * 0.5f, -height * 0.5f);
        mOffsetMatrix.postTranslate(width * 0.5f, height * 0.5f);
        mOffset[0] = width;
        mOffset[1] = height;
        mOffsetMatrix.mapPoints(mOffset);
        return (width - mOffset[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
    }
}
