package com.draabek.fractal.palette;

public class CopperPalette implements ColorPalette {
	
	private int size;
	
	public CopperPalette(int size) {
		this.size = size;
	}
	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getColor(double intensity) {
		int rval = (int)(intensity * 0xff);
		int gval = (int) (intensity/2*0xff);
		return rval << 16 | gval << 8 | 0xff000000;
	}
}
