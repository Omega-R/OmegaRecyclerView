package com.omega_r.libs.omegarecyclerview.expandable_recycler_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

public final class MaskView extends FrameLayout {

    @Nullable
    private Path mMaskPath;
    private Paint mPaint;

    MaskView(@NonNull Context context) {
        super(context);
    }

    public void setupMask() {
//        mMaskPath = new Path();
//        mMaskPath.addRect(0, 0, getWidth(), getHeight(), Path.Direction.CW);
//        mPaint = new Paint();
//        mPaint.setColor(Color.GREEN);
//        mPaint.setStyle(Paint.Style.FILL);
    }

    public void invalidateMask() {
//        mMaskPath = null;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mMaskPath != null) {
            canvas.drawPath(mMaskPath, mPaint);
            canvas.clipPath(mMaskPath);
        }
    }
}
