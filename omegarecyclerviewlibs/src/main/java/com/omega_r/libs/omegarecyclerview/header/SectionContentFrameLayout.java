package com.omega_r.libs.omegarecyclerview.header;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Anton Knyazev on 21.08.18.
 */
final class SectionContentFrameLayout extends FrameLayout {

    public SectionContentFrameLayout(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            View child = getChildAt(0);
            child.measure(widthMeasureSpec, heightMeasureSpec);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            setMeasuredDimension(child.getMeasuredWidth(),
                    child.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin);
        }

    }
}
