package com.draabek.fractal.palette;

public class BWPalette extends ColorPalette {

	private static int[] intCache = new int[] {0xff000000, 0xffffffff };

	@Override
	public int getColorInt(float intensity) {
		return (intensity > 0.5) ? 0xffffffff: 0xff000000;
	}

	@Override
	public int[] getColorsInt() {
		return intCache;
	}

}
