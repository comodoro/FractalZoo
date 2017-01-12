package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class MandelbrotFractal extends Fractal {
	private int[] buffer;
	
	public MandelbrotFractal() {
		super("Mandelbrot");
	}
		
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
				orig.left, orig.top, orig.right, orig.bottom, portrait, 200, part.left, part.top, part.right, part.bottom);
		bitmap.setPixels(buffer, bwidth*part.top + part.left, bwidth, part.left, part.right, width, height);
		return bitmap;
	}
	
	
	/*public Bitmap redrawBitmap(Bitmap bitmap, RectF orig, boolean portrait) {
		double cr,ci;
		double zr,zi;
		double crt;
		float x = orig.left;
		float y = orig.top;
		//portrait = (bitmap.getWidth() <= bitmap.getHeight());
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		//int width = 200;
		//int height = 200;
		logger.info("redrawing mandelbrot, rect: " + x + ", " + y + ", " + orig.width() + ", " + orig.height());
		long startTime = System.currentTimeMillis();
		for (int i = 0;i < width;i++)
			for (int j = 0;j < height;j++) {
				 cr = x + (double)i/width*orig.width();
				 ci = y + (double)j/width*orig.height();
				 if (portrait) {
					 double temp = cr;
					 cr = ci;
					 ci = temp;
				 }
				 zr = cr;
				 zi = ci;
			     int iter = 0;
			     for (iter = 0;iter < maxIterations;iter++) {
			    	 crt = cr;
			    	 cr = cr*cr - ci*ci + zr;
			    	 ci = 2*crt*ci + zi;
			    	 //logger.info(" C: " + cr + "," + ci);
			    	 if (cr*cr - ci*ci > 2*2) {
			    		 //logger.info("Outside the set, iter:" + iter);
			    		 break;
			    	 }
			     }
			     float factor = (1 - (float)iter / maxIterations);
//			     int rgb = (int) (factor * 0xff);
//			     int color = rgb | rgb << 8 | rgb << 16 | 0xff000000; 
//			     bitmap.setPixel(i, j, color);
			     int color = palette.getColor(factor);
			     bitmap.setPixel(i, j, color);
			}
		logger.info("finished redrawing Mandelbrot");
		long endTime = System.currentTimeMillis();
		logger.info("Time (ms): " + (endTime - startTime));
		return bitmap;
	}*/
}
