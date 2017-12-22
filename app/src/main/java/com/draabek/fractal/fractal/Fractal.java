package com.draabek.fractal.fractal;

import android.support.annotation.NonNull;

import java.util.Hashtable;
import java.util.Map;

public abstract class Fractal {
	protected String name = "";
	protected String thumbPath;

	protected Map<String, Float> parameters;

	public Fractal() {
		parameters = new Hashtable<>();
	}

	public Fractal(String name) {
		this();
		this.name = name;
	}

	public void updateSettings(Map<String, Float> newSettings) {
		this.parameters.putAll(newSettings);
	}

	public @NonNull String getName() {
		return name;
	}

	public void setName(@NonNull String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public Map<String, Float> getParameters() {
		return parameters;
	}

	public String getThumbPath() { return thumbPath;}

	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}
}
