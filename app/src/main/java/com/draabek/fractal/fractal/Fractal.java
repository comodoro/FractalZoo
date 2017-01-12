package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class Fractal {
	protected String name = "";
	protected float width;
	protected float height;
	protected boolean portrait = false;
	
	public Fractal() {
		
	}
	
	public Fractal(String name) {
		this();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}

	public boolean isPortrait() {
		return portrait;
	}

	public abstract Bitmap redrawBitmap(Bitmap bitmap, RectF rect, boolean portrait); 
	public abstract Bitmap redrawBitmapPart(Bitmap bitmap, RectF rect, boolean portrait, Rect part);
	public String toString() {
		return name;
	}

}
