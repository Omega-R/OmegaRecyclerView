package com.omega_r.libs.omegarecyclerview.viewpager.transform;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ItemTransformerWrapper implements ItemTransformer {

    private ViewPager.PageTransformer mPageTransformer;

    public ItemTransformerWrapper(ViewPager.PageTransformer pageTransformer) {
        mPageTransformer = pageTransformer;
    }

    @Override
    public void transformItem(View view, float position, boolean isHorizontal, int scrolled) {
        mPageTransformer.transformPage(view, position);
    }

}
