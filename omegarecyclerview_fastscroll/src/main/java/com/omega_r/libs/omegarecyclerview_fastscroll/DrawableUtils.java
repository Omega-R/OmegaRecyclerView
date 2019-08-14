package com.omega_r.libs.omegarecyclerview_fastscroll;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

@SuppressWarnings("WeakerAccess")
public class DrawableUtils {

    public static Drawable getTintedDrawable(Context context, @NonNull Drawable drawable, @ColorRes int color) {
        return getTintedDrawable(drawable, ContextCompat.getColor(context, color));
    }

    public static Drawable getTintedDrawable(@NonNull Drawable drawable, @ColorInt int color) {
        Drawable mutate = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(mutate, color);
        return mutate;
    }

}