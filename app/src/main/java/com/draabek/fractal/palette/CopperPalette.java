package com.draabek.fractal.palette;

public class CopperPalette implements ColorPalette {

	private int[] intCache;


	public CopperPalette(int size) {
		intCache = new int[size];
		for (int i = 0;i < size;i++) {
			int rval = (int)((double)i/size * 0xff);
			int gval = (int) ((double)i/size/2 * 0xff);
			intCache[i] = rval | gval << 8 | 0xff000000;
		}
	}

	@Override
	public int getColorInt(float intensity) {
		return (int)(intensity * 0xff) | (int)(intensity/2 * 0xff) << 8 | 0xff000000;
	}

	@Override
	public int[] getColorsInt() {
		return intCache;
	}

}
