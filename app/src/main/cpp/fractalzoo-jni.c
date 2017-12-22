#include <jni.h>
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_draabek_fractal_canvas_NativeLib_redrawMandelbrot
  (JNIEnv * env, jclass clazz, jintArray bitmapArray, jint width, jint height, jdouble left, jdouble top, jdouble right, jdouble bottom, jint maxiter) {
		double cr,ci;
		double zr,zi;
		double crt;
		int i;
		int j;
		double orig_x_len;
		double orig_y_len;
		int iter;
		double factor;
		int color;
		jint *bitmap;
		bitmap = (*env)->GetIntArrayElements(env, bitmapArray, 0);
		orig_x_len = right - left;
		orig_y_len = bottom - top;
		for (i = 0;i < width;i++)
			for (j = 0;j < height;j++) {
				 cr = left + (double)i/width*orig_x_len;
				 ci = top + (double)j/width*orig_y_len;
				 /*if (portrait) {
					 double temp = cr;
					 cr = ci;
					 ci = temp;
				 }*/
				 zr = cr;
				 zi = ci;
			     for (iter = 0;iter < maxiter;iter++) {
			    	 crt = cr;
			    	 cr = cr*cr - ci*ci + zr;
			    	 ci = 2*crt*ci + zi;
			    	 if (cr*cr - ci*ci > 2*2) {
			    	 	break;
			    	 }
			     }
			     factor = (1 - (double)iter / maxiter);
				color = (int) (factor * 0xff);
				color = color | color << 8 | color << 16 | 0xff000000; 
			bitmap[i + j*width] = color;
			}
		(*env)->ReleaseIntArrayElements(env, bitmapArray, bitmap, 0);
}

JNIEXPORT void JNICALL Java_com_draabek_fractal_canvas_NativeLib_redrawMandelbrotPart
		(JNIEnv * env, jclass clazz, jintArray bitmapArray, jint width, jint height, jdouble left,
		 jdouble top, jdouble right, jdouble bottom, jint maxiter, jint fromX,
		jint fromY, jint toX, jint toY) {
	double cr,ci;
	double zr,zi;
	double crt;
	int i;
	int j;
	double orig_x_len;
	double orig_y_len;
	int iter;
	double factor;
	int color;
	jint *bitmap;
	bitmap = (*env)->GetIntArrayElements(env, bitmapArray, 0);
	orig_x_len = right - left;
	orig_y_len = bottom - top;
	for (i = fromX;i < toX;i++)
		for (j = fromY;j < toY;j++) {
			cr = left + (double)i/width*orig_x_len;
			ci = top + (double)j/width*orig_y_len;
			/*if (portrait) {
				double temp = cr;
				cr = ci;
				ci = temp;
			}*/
			zr = cr;
			zi = ci;
			for (iter = 0;iter < maxiter;iter++) {
				crt = cr;
				cr = cr*cr - ci*ci + zr;
				ci = 2*crt*ci + zi;
				if (cr*cr - ci*ci > 2*2) {
					break;
				}
			}
			factor = (1 - (double)iter / maxiter);
			color = (int) (factor * 0xff);
			color = color | color << 8 | color << 16 | 0xff000000;
			bitmap[i + j*width] = color;
		}
	(*env)->ReleaseIntArrayElements(env, bitmapArray, bitmap, 0);
}

#ifdef __cplusplus
}
#endif
 