package vn.com.call.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

/**
 * Created by ngson on 28/07/2017.
 */

public class ColorUtils {
    public static Bitmap getBitmapFromColor(int color, int width, int height) {
        int[] pixels = new int[width * height];
        int max = width * height;

        for (int i = 0;i < max;i++) pixels[i] = color;

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    public static int getDarkColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    public static Drawable setIconFilterColor(Context context, int iconRes, int color) {
        Drawable drawable = ContextCompat.getDrawable(context, iconRes);
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        return drawable;
    }
}
