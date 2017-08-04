package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Arrays;

public class Debug extends Fractal {

	Debug() {
		super("Debug");
	}

	public Debug(String vertexShader, String fragmentShader) {
		this();
		this.shaders = new String[] {vertexShader, fragmentShader};
	}

	/* Redraw bitmap via JNI */
	@Override
	public Bitmap redrawBitmap(Bitmap bitmap, RectF orig, boolean portrait) {
		int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
		int red = 0xff0000ff;
		Arrays.fill(pixels, red);
		bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		return bitmap;
	}

	@Override
	public Bitmap redrawBitmapPart(Bitmap bitmap, RectF orig, boolean portrait,
			Rect part) {
		int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
		int red = 0xff0000ff;
		Arrays.fill(pixels, red);
		int width = part.right - part.left;
		int height = part.bottom - part.top;
		bitmap.setPixels(pixels, 0, bitmap.getWidth(), part.left, part.top, width, height);
		return bitmap;
	}

}
