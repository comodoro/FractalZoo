package com.draabek.fractal.palette;

public class PeasyPalette extends ColorPalette {

	private int[] intCache;

	public PeasyPalette() {
		this(256);
	}
	public PeasyPalette(int size) {
		intCache = new int[size];
		for (int i = 1;i < size;i++) {
            int val = (int)((Math.log(i)/Math.log(size)+Math.exp(-i)) * 0xff) & 0xff;
			intCache[i] = val/2 | val << 8 | 0xff000000;
		}
	}

	@Override
	public int getColorInt(float intensity) {
		return intCache[(int)(intensity*intCache.length)];
	}

	@Override
	public int[] getColorsInt() {
		return intCache;
	}

}
