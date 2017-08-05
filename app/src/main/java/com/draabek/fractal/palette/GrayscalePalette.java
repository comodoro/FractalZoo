package com.draabek.fractal.palette;

import android.graphics.Bitmap;

public class GrayscalePalette implements ColorPalette {

	private int[] intCache;
	private Bitmap bitmapCache;

	public GrayscalePalette(int size) {
		intCache = new int[size];
		for (int i = 0;i < size;i++) {
			int rgbval = (int)((double)i/size * 0xff);
			intCache[i] = rgbval | rgbval << 8 | rgbval << 16 | 0xff000000;
		}
		bitmapCache = Bitmap.createBitmap(intCache,size,1,Bitmap.Config.ARGB_8888);
	}

	@Override
	public int getColorInt(float intensity) {
		return (int)(intensity * 0xffffff) | 0xff000000;
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
