package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class MandelbrotFractal extends BitmapDrawFractal {
	private int[] buffer;

	/* Redraw bitmap via JNI */
	@Override
	public Bitmap redrawBitmap(Bitmap bitmap, RectF orig, boolean portrait) {
		if ((buffer == null) || (buffer.length != bitmap.getWidth()*bitmap.getHeight())) {
			buffer = new int[bitmap.getWidth()*bitmap.getHeight()];
		}
		NativeLib.redrawMandelbrot(buffer, bitmap.getWidth(), bitmap.getHeight(), 
				orig.left, orig.top, orig.right, orig.bottom, portrait, 200);
		bitmap.setPixels(buffer, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		return bitmap;
	}

	@Override
	public Bitmap redrawBitmapPart(Bitmap bitmap, RectF orig, boolean portrait,
			Rect part) {
		if ((buffer == null) || (buffer.length != bitmap.getWidth()*bitmap.getHeight())) {
			buffer = new int[bitmap.getWidth()*bitmap.getHeight()];
		}
		int width = part.right - part.left;
		int height = part.bottom - part.top;
		int bwidth = bitmap.getWidth();
		NativeLib.redrawMandelbrotPart(buffer, bitmap.getWidth(), bitmap.getHeight(), 
				orig.left, orig.top, orig.right, orig.bottom, portrait, 200, part.left, part.top,
				part.right, part.bottom);
		bitmap.setPixels(buffer, 0, bitmap.getWidth(), part.left, part.top, width, height);
		return bitmap;
	}

}
