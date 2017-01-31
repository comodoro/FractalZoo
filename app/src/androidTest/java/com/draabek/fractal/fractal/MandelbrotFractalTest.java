package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vojta on 28.01.2017.
 */
public class MandelbrotFractalTest {
    @Test
    public void redrawBitmap() throws Exception {
        MandelbrotFractal mf = new MandelbrotFractal();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
        Bitmap mfbmp = mf.redrawBitmap(bmp, new RectF(0.0f, 0.0f, 1.0f, 1.0f), true);
        assertEquals(100, bmp.getHeight());
        assertEquals(100, bmp.getWidth());
        assertEquals(100, mfbmp.getHeight());
        assertEquals(-197380, mfbmp.getPixel(2, 56));
    }

    @Test
    public void redrawBitmapPart() throws Exception {
        MandelbrotFractal mf = new MandelbrotFractal();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
        Bitmap mfbmp = mf.redrawBitmapPart(bmp, new RectF(0.0f, 0.0f, 1.0f, 1.0f), true, new Rect(0, 0, 100, 100));
        assertEquals(100, bmp.getHeight());
        assertEquals(100, bmp.getWidth());
        assertEquals(100, mfbmp.getHeight());
        assertEquals(-197380, mfbmp.getPixel(2, 56));
    }

}