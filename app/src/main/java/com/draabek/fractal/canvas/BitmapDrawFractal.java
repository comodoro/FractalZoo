package com.draabek.fractal.canvas;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Vojta on 24.08.2017.
 */

public abstract class BitmapDrawFractal extends CpuFractal {
    protected RectF centerScaleToRect(float centerX, float centerY, float scale) {
        //// TODO: 07.09.2017
        throw new UnsupportedOperationException();
    }

    public abstract Bitmap redrawBitmap(Bitmap bitmap, RectF rect);
    @Deprecated
    public abstract Bitmap redrawBitmapPart(Bitmap bitmap, RectF rect, Rect part);
}
