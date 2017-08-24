package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Vojta on 24.08.2017.
 */

public abstract class BitmapDrawFractal extends Fractal {
    public abstract Bitmap redrawBitmap(Bitmap bitmap, RectF rect, boolean portrait);
    public abstract Bitmap redrawBitmapPart(Bitmap bitmap, RectF rect, boolean portrait, Rect part);
}
