package com.draabek.fractal.palette;

public class WBPalette extends ColorPalette {

	private static int[] intCache = new int[] {0xffffffff, 0xff000000 };

	@Override
	public int getColorInt(float intensity) {
		return (intensity > 0.5) ? 0xff000000: 0xffffffff;
	}

	@Override
	public int[] getColorsInt() {
		return intCache;
	}

}
