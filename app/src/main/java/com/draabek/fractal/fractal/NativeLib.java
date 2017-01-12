package com.draabek.fractal.fractal;

public class NativeLib {

	static {
		System.loadLibrary("fractalzoo-jni");
	}

	public static native void redrawMandelbrot(int[] array, 
			int width, int height, 
			double left, double top, double right, double bottom, 
			boolean portrait,
			int maxiter);
	public static native void redrawMandelbrotPart(int[] array, 
			int width, int height,
			double left, double top, double right, double bottom, 
			boolean portrait,
			int maxiter, 
			int x, int y, int x2, int y2);

	public static native void redrawForestFire(int[] array, 
			int width, int height, 
			int forest_color, int burnt_color, int fire_color,
			double ignite_param, double spread_param, double burnt_param, double fire_duration, double burnt_duration, 
			boolean portrait);
	public static native void redrawForestFirePart(int[] array, 
			int width, int height, 
			int forest_color, int burnt_color, int fire_color,
			double ignite_param, double spread_param, double burnt_param, double fire_duration, double burnt_duration, 
			boolean portrait,
			int x, int y, int x2, int y2);
}
