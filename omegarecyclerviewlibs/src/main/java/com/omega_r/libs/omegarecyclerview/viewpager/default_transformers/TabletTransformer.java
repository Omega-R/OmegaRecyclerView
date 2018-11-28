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
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        float rotation = (position < 0 ? 30f : -30f) * Math.abs(position);
        int width = view.getWidth();
        int height = view.getHeight();

        if (isHorizontal) {
            view.setTranslationX(getOffsetForRotation(rotation, width, height, true));
            view.setPivotX(width * 0.5f);
            view.setPivotY(0);
            view.setRotationY(rotation);
        } else {
            view.setTranslationY(getOffsetForRotation(rotation, width, height, false));
            view.setPivotY(height * 0.5f);
            view.setPivotX(0);
            view.setRotationX(-rotation);
        }
    }

    private float getOffsetForRotation(float degrees, int width, int height, boolean isHorizontal) {
        mOffsetMatrix.reset();
        mOffsetCamera.save();
        if (isHorizontal) {
            mOffsetCamera.rotateY(Math.abs(degrees));
        } else {
            mOffsetCamera.rotateX(Math.abs(degrees));
        }
        mOffsetCamera.getMatrix(mOffsetMatrix);
        mOffsetCamera.restore();

        mOffsetMatrix.preTranslate(-width * 0.5f, -height * 0.5f);
        mOffsetMatrix.postTranslate(width * 0.5f, height * 0.5f);
        mOffset[0] = width;
        mOffset[1] = height;
        mOffsetMatrix.mapPoints(mOffset);
        if (isHorizontal) {
            return (width - mOffset[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
        } else {
            return (height - mOffset[1]) * (degrees > 0.0f ? 1.0f : -1.0f);
        }
    }
}
