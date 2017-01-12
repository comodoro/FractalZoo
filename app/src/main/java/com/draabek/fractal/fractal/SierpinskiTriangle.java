package com.draabek.fractal.fractal;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class SierpinskiTriangle extends Fractal {
	@Override
	public Bitmap redrawBitmap(Bitmap bitmap, RectF rect, boolean portrait) {
		int min = Math.min(bitmap.getHeight(), bitmap.getWidth());
		int xoffset = (bitmap.getHeight() < bitmap.getWidth()) ? (bitmap.getWidth() - bitmap.getHeight())/2 : 0;
		int yoffset = (bitmap.getHeight() > bitmap.getWidth()) ? (bitmap.getHeight() - bitmap.getWidth())/2 : 0;
		throw new UnsupportedOperationException("Not supported yet");
		
	}

	@Override
	public String toString() {
		return "Sierpiński triangle";
	}

	@Override
	public Bitmap redrawBitmapPart(Bitmap bitmap, RectF rect, boolean portrait,
			Rect part) {
		// TODO Auto-generated method stub
		return null;
	}

}
