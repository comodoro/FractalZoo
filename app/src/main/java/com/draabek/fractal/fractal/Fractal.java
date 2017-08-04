package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class Fractal {
	protected String name = "";
	protected String[] shaders = null;

	public Fractal() {
		
	}

	public Fractal(String name) {
		this();
		this.name = name;
	}

	public Fractal(String name, String vertexShader, String fragmentShader) {
		this(name);
		this.shaders = new String[] {vertexShader, fragmentShader};
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public abstract Bitmap redrawBitmap(Bitmap bitmap, RectF rect, boolean portrait); 
	public abstract Bitmap redrawBitmapPart(Bitmap bitmap, RectF rect, boolean portrait, Rect part);

	public String[] getShaders() {
		return shaders;
	}

	public void setShaders(String[] shaders) {
		this.shaders = shaders;
	}

	public String toString() {
		return name;
	}

}
