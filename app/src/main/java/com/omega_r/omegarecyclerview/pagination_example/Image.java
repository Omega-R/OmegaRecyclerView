package com.omega_r.omegarecyclerview.pagination_example;

import android.support.annotation.DrawableRes;

import com.omega_r.omegarecyclerview.R;

import java.util.ArrayList;
import java.util.List;

public class Image {

    @DrawableRes
    private int mImage;

    public Image(@DrawableRes int image) {
        mImage = image;
    }

    @DrawableRes
    public int getImageRes() {
        return mImage;
    }

    public static List<Image> createImageList(int numImages) {
        List<Image> list = new ArrayList<>();

        for (int i = 0; i < numImages; i++) {
            Image image;
            if (i % 2 == 0) {
                image = new Image(R.drawable.image_1);
            } else if (i % 3 == 0){
                image = new Image(R.drawable.image_2);
            } else {
                image = new Image(R.drawable.image_3);
            }
            list.add(image);
        }
        return list;
    }

}
