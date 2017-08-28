package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import java.nio.IntBuffer;
import java.util.Random;

public class ForestFireFractal extends BitmapDrawFractal {
	public static final int FOREST_COLOR = 0xff005500;
	public static final int FIRE_COLOR = 0xffff8800;
	public static final int BURNT_COLOR = 0xff222222;
	public static double IGNITE_PARAM = 0.00001;
	public static double SPREAD_PARAM = 0.2;
	public static double FIRE_DURATION = 0.5;
	public static double BURNT_DURATION = 0.01;
	public static int ITERATIONS = 30;
	private static final String KEY = ForestFireFractal.class.getName();
	private IntBuffer forest;
	private IntBuffer forest2;

	private int bitmap_width;
	private int bitmap_height;

	private Random random;
	private IntBuffer buffer;

	public ForestFireFractal() {
		random = new Random();
	}
	
	public void iteration() {
			for (int i = 0; i < bitmap_width; i++)
				for (int j = 0; j < bitmap_height; j++) {
					int value = forest.get(i* bitmap_height +j);
					double rand = random.nextDouble();
					if (value == FOREST_COLOR) {
						if (rand < IGNITE_PARAM) {
							forest2.put(i* bitmap_height +j, FIRE_COLOR);
						} else {
							//find fire in the vicinity
							search_loop:
							for (int k = -1;k <= 1;k++) {
								for (int l = -1;l <= 1;l++) {
									int idx = (i+k)* bitmap_height + j+l;
									if ((idx > 0) && (idx < bitmap_width * bitmap_height)) {
										if (forest.get((i+k)* bitmap_height + j+l) == FIRE_COLOR) {
											if (rand < SPREAD_PARAM) {
												forest2.put(i* bitmap_height +j, FIRE_COLOR);
												break search_loop;
											}
										}
									}
								}
							}
						}
					} else if (value == FIRE_COLOR) {
						if (rand < FIRE_DURATION) {
							forest2.put(i* bitmap_height +j, BURNT_COLOR);
						}
					} else if (value == BURNT_COLOR) {
						if (rand < BURNT_DURATION) {
							forest2.put(i* bitmap_height +j, FOREST_COLOR);
						}
					}
				}
			forest = forest2;
		}

	@Override
	public Bitmap redrawBitmap(Bitmap bitmap, RectF rect, boolean portrait) {
		bitmap_width = bitmap.getWidth();
		bitmap_height = bitmap.getHeight();
		if ((forest == null) || (forest.limit() != bitmap.getWidth()*bitmap.getHeight())) {
			forest = IntBuffer.allocate(bitmap_width * bitmap_height);
			forest2 = IntBuffer.allocate(bitmap_width * bitmap_height);
			for (int i = 0; i < bitmap_width; i++)
				for (int j = 0; j < bitmap_height; j++) {
					forest.put(i* bitmap_height +j, FOREST_COLOR);
					forest2.put(i* bitmap_height +j, FOREST_COLOR);
				}
		}
		for (int i = 0;i < ITERATIONS;i++) {
			iteration();
		}
		bitmap.copyPixelsFromBuffer(forest);
		return bitmap;
	}

	@Override
	public Bitmap redrawBitmapPart(Bitmap bitmap, RectF rect, boolean portrait,
			Rect part) {
		// TODO Auto-generated method stub
		return null;
	}
}
