package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class GLSLFractal extends Fractal {

	@Override
	public Bitmap redrawBitmap(Bitmap bitmap, RectF orig, boolean portrait) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bitmap redrawBitmapPart(Bitmap bitmap, RectF orig, boolean portrait,
			Rect part) {
		throw new UnsupportedOperationException();
	}

}
