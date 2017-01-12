package com.draabek.fractal.palette;

public class BWPalette implements ColorPalette {
	
	private int size;
	
	public BWPalette(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getColor(double intensity) {
		return (intensity > 0.5) ? 0xffffffff: 0xff000000;
	}

}
