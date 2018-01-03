package com.draabek.fractal.canvas;

/**
 * Native rendering code. Superseded by GLSL where possible
 */

public class NativeLib {

	static {
		System.loadLibrary("fractalzoo-jni");
	}

	public static native void redrawMandelbrot(int[] array,
											   int width, int height,
											   double left, double top, double right, double bottom,
											   int maxiter);
	public static native void redrawMandelbrotPart(int[] array,
												   int width, int height,
												   double left, double top, double right, double bottom,
												   int maxiter,
												   int x, int y, int x2, int y2);
	public static native void redrawLorenz(int[] array,
											   int width, int height,
											   double left, double top, double right, double bottom,
											   int maxiter);
	public static native void redrawLorenzPart(int[] array,
											   int width, int height,
											   double left, double top, double right, double bottom,
											   int maxiter,
											   int x, int y, int x2, int y2);
}
