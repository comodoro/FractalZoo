package com.draabek.fractal.palette;

/** A color palette (sequence of colors from 0 to getSize())
 * @author Vojtech Drabek
 *
 */

public abstract class ColorPalette {
	public ColorPalette() {}
	public abstract int getColorInt(float intensity);
	public abstract int[] getColorsInt();
}
