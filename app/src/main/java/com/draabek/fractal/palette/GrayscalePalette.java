package com.draabek.fractal.palette;

public class GrayscalePalette implements ColorPalette{
	private int size;
	
	public GrayscalePalette(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getColor(double intensity) {
		int rgbval = (int)(intensity * 0xff);
		return rgbval | rgbval << 8 | rgbval << 16 | 0xff000000;
	}
}
