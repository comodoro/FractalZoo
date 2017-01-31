package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

public class ForestFireFractal extends Fractal {
	public static final int FOREST_COLOR = 0xff005500;
	public static final int FIRE_COLOR = 0xffff8800;
	public static final int BURNT_COLOR = 0xff222222;
	public static double IGNITE_PARAM = 0.00001;
	public static double SPREAD_PARAM = 0.2;
	public static double FIRE_DURATION = 0.5;
	public static double BURNT_DURATION = 0.01;
	public static int ITERATIONS = 100;
	private static final String KEY = ForestFireFractal.class.getName();
	private int[][] forest;
	private int[][] forest2;
	
	private Random random;
	
	public ForestFireFractal() {
		super("Forest Fire");
		random = new Random();
	}
	
	public void iteration() {
			for (int i = 0;i < forest.length;i++)
				for (int j = 0;j < forest[0].length;j++) {
					int value = forest[i][j];
					double rand = random.nextDouble();
					if (value == FOREST_COLOR) {
						if (rand < IGNITE_PARAM) {
							forest2[i][j] = FIRE_COLOR;
						} else {
							//find fire in the vicinity
							search_loop:
							for (int k = -1;k <= 1;k++) {
								for (int l = -1;l <= 1;l++) {
									if ((i + k >= 0) && (j + l >= 0) && (i + k < forest.length) && (j + l < forest[0].length)) {
										if (forest[i+k][j+l] == FIRE_COLOR) {
											if (rand < SPREAD_PARAM) {
												forest2[i][j] = FIRE_COLOR;
												break search_loop;
											}
										}
									}
								}
							}
						}
					} else if (value == FIRE_COLOR) {
						if (rand < FIRE_DURATION) {
							forest2[i][j] = BURNT_COLOR;
						}
					} else if (value == BURNT_COLOR) {
						if (rand < BURNT_DURATION) {
							forest2[i][j] = FOREST_COLOR;
						}
					}
				}
			for (int i = 0;i < forest.length;i++)
				for (int j = 0;j < forest[0].length;j++) {
					forest[i][j] = forest2[i][j];
				}
		}

	@Override
	public Bitmap redrawBitmap(Bitmap bitmap, RectF rect, boolean portrait) {
		if ((forest == null) || (forest.length != bitmap.getWidth()) || 
				(forest[0].length != bitmap.getHeight())) {
			forest = new int[bitmap.getWidth()][bitmap.getHeight()];
			forest2 = new int[bitmap.getWidth()][bitmap.getHeight()];
			for (int i = 0;i < bitmap.getWidth();i++)
				for (int j = 0;j < bitmap.getHeight();j++) {
					forest[i][j] = FOREST_COLOR;
					forest2[i][j] = FOREST_COLOR;	
				}
		}
		for (int i = 0;i < ITERATIONS;i++) {
			iteration();
		}
		//slow and dirty for starters
		for (int i = 0;i < bitmap.getWidth();i++)
			for (int j = 0;j < bitmap.getHeight();j++) {
				bitmap.setPixel(i, j, forest[i][j]);
			}
		return bitmap;
	}

	@Override
	public Bitmap redrawBitmapPart(Bitmap bitmap, RectF rect, boolean portrait,
			Rect part) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
