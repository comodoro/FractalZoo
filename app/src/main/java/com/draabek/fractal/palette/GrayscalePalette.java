package com.draabek.fractal.palette;

public class GrayscalePalette extends ColorPalette {

	private int[] intCache;

	public GrayscalePalette() {
		this(256);
	}
	public GrayscalePalette(int size) {
		intCache = new int[size];
		for (int i = 0;i < size;i++) {
			int rgbval = (int)((double)i/size * 0xff);
			intCache[i] = rgbval | rgbval << 8 | rgbval << 16 | 0xff000000;
		}
	}

	@Override
	public int getColorInt(float intensity) {
		return (int)(intensity * 0xffffff) | 0xff000000;
	}

	@Override
	public int[] getColorsInt() {
		return intCache;
	}

}
