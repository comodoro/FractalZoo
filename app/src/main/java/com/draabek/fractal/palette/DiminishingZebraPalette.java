package com.draabek.fractal.palette;

public class DiminishingZebraPalette extends ColorPalette {

	private int[] intCache;

	public DiminishingZebraPalette() {
		this(256, 32);
	}
	public DiminishingZebraPalette(int size, int step) {
		intCache = new int[size];
		for (int i = 1;i < size;i++) {
			int val = (i % 2*step < step) ? 0xff : 0;
			intCache[i] = val | val << 16 | val << 8 | 0xff000000;
			step = step / 2;
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
