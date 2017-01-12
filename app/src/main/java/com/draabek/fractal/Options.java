package com.draabek.fractal;

import android.graphics.RectF;

import com.draabek.fractal.palette.ColorPalette;

import java.util.Hashtable;
import java.util.Map;

public class Options {
	private ColorPalette colorPalette = null;
	private RectF floatRect = null;
	private Map<String, Object> fractalOptions = null;
	
	public Options() {
		fractalOptions = new Hashtable<String, Object>();
	}
}
