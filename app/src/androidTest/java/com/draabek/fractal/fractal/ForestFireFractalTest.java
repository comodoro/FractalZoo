package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.RectF;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vojta on 28.01.2017.
 */
public class ForestFireFractalTest {
    @Test
    public void redrawBitmap() throws Exception {
        ForestFireFractal mf = new ForestFireFractal();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(10, 10, conf);
        Bitmap mfbmp = mf.redrawBitmap(bmp, new RectF(0.0f, 0.0f, 1.0f, 1.0f), true);
        assertEquals(10, bmp.getHeight());
        assertEquals(10, bmp.getWidth());
        assertEquals(10, mfbmp.getHeight());
        //flaky:
        //assertNotEquals(mfbmp.getPixel(10, 10), mfbmp.getPixel(50, 50));
    }

}