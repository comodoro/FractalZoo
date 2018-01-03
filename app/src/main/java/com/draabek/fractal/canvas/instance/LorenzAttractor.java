package com.draabek.fractal.canvas.instance;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.draabek.fractal.canvas.BitmapDrawFractal;
import com.draabek.fractal.canvas.NativeLib;

public class LorenzAttractor extends BitmapDrawFractal {
	private int[] buffer;

	/* Redraw bitmap via JNI */
	@Override
	public Bitmap redrawBitmap(Bitmap bitmap, RectF orig) {
		if ((buffer == null) || (buffer.length != bitmap.getWidth()*bitmap.getHeight())) {
			buffer = new int[bitmap.getWidth()*bitmap.getHeight()];
		}
		NativeLib.redrawLorenz(buffer, bitmap.getWidth(), bitmap.getHeight(),
				orig.left, orig.top, orig.right, orig.bottom, 200);
		bitmap.setPixels(buffer, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		return bitmap;
	}

	@Override
	public Bitmap redrawBitmapPart(Bitmap bitmap, RectF orig, Rect part) {
		if ((buffer == null) || (buffer.length != bitmap.getWidth()*bitmap.getHeight())) {
			buffer = new int[bitmap.getWidth()*bitmap.getHeight()];
		}
		int width = part.right - part.left;
		int height = part.bottom - part.top;
		NativeLib.redrawLorenzPart(buffer, bitmap.getWidth(), bitmap.getHeight(),
				orig.left, orig.top, orig.right, orig.bottom, 200, part.left, part.top,
				part.right, part.bottom);
		bitmap.setPixels(buffer, 0, bitmap.getWidth(), part.left, part.top, width, height);
		return bitmap;
	}

}
