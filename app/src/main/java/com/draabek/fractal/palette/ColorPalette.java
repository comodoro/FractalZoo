package com.draabek.fractal.palette;

import android.graphics.Bitmap;

/** A color palette (sequence of colors from 0 to getSize())
 * @author Vojtech Drabek
 *
 */
public interface ColorPalette {
	public int getColorInt(float intensity);
	public int[] getColorsInt();
	public Bitmap getColorsBitmap();
}
