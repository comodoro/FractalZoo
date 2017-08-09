package com.draabek.fractal.fractal;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Hashtable;
import java.util.Map;

public abstract class Fractal {
	protected String name = "";
	protected String[] shaders = null;
	protected Map<String, Object> settings;

	public Fractal() {
		settings = new Hashtable<String, Object>();
	}

	public Fractal(String name) {
		this();
		this.name = name;
	}

	public Fractal(String name, String vertexShader, String fragmentShader) {
		this(name);
		this.shaders = new String[] {vertexShader, fragmentShader};
	}

	public void updateSettings(Map<String, Object> newSettings) {
		this.settings.putAll(newSettings);
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
