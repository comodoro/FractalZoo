package com.draabek.fractal.palette;

public class CopperPalette extends ColorPalette {

	private int[] intCache;

	public CopperPalette() {
		this(256);
	}
	public CopperPalette(int size) {
		intCache = new int[size];
		for (int i = 1;i < size;i++) {
			int gval = (int) ((double)i/size*3/4 * 0xff);
			int rval = (int)(Math.log(i)/Math.log(size) * 0xff);
			intCache[i] = rval << 16 | gval << 8 | 0xff000000;
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
