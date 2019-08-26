package com.omega_r.libs.omegarecyclerview_fastscroll;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.ScaleDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;

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

    public static int getAlpha(@NonNull Drawable drawable) {
        if (SDK_INT >= KITKAT) return drawable.getAlpha();

        if (drawable instanceof ColorDrawable) {
            return ((ColorDrawable) drawable).getColor() >>> 24;
        } else if( drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getPaint().getAlpha();
        } else if(drawable instanceof RotateDrawable) {
            Drawable rotateDrawable = ((RotateDrawable) drawable).getDrawable();
            if (rotateDrawable != null) return getAlpha(rotateDrawable);
        } else if( drawable instanceof ScaleDrawable) {
            Drawable scaleDrawable = ((ScaleDrawable) drawable).getDrawable();
            if (scaleDrawable != null) return getAlpha(scaleDrawable);
        }

        return DrawableCompat.getAlpha(drawable);
    }

}