package com.draabek.fractal.palette;

import android.graphics.Bitmap;

public class BWPalette implements ColorPalette {

	private static int[] intCache = new int[] {0xff000000, 0xffffffff };
	private static Bitmap bitmapCache = Bitmap.createBitmap(intCache,2,1,Bitmap.Config.ARGB_8888);

	@Override
	public int getColorInt(float intensity) {
		return (intensity > 0.5) ? 0xffffffff: 0xff000000;
	}

	@Override
	public int[] getColorsInt() {
		return intCache;
	}

	@Override
	public Bitmap getColorsBitmap() {
		return bitmapCache;
	}
}
