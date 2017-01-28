package com.draabek.fractal.fractal;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class FractalRegistry {
	private static final String LOG_KEY = FractalRegistry.class.getName();
	private static FractalRegistry instance = null;
	private List<Fractal> fractals;
	
	private FractalRegistry() {
		fractals = new ArrayList<Fractal>();
	}
	
	public static FractalRegistry getInstance() {
		if (instance == null) instance = new FractalRegistry();
		return instance;
	}
	
	public void add(Fractal fractal) {
		fractals.add(fractal);
	}
	
	public void remove(Fractal fractal) {
		fractals.remove(fractal);
	}
	
	public List<Fractal> getFractals() {
		return fractals;
	}
	
	public void init(Properties props) {
		for (Object name : props.keySet()) {
			String clazz = (String) props.get(name);
			try {
				Object o = Class.forName(clazz).newInstance();
				Fractal f = (Fractal) o;
				add(f);
			} catch(ClassNotFoundException e) {
				Log.w(LOG_KEY, "Cannot find fractal class " + clazz);
			} catch(IllegalAccessException e) {
				Log.w(LOG_KEY, "Cannot access fractal class " + clazz);
			} catch(InstantiationException e) {
				Log.w(LOG_KEY, "Cannot instantiate fractal class " + clazz);
			}
		}
	}
	
	public Fractal get(int index) {
		return fractals.get(index);
	}
	
	public Fractal get(String name) {
		for (Fractal f : fractals) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
        Log.w(LOG_KEY, "Fractal not found in registry: " + name);
		return null;
	}

	public Fractal getByClass(String clazz) {
		for (Fractal f : fractals) {
			if (f.getClass().getName().equals(clazz)) {
				return f;
			}
		}
        Log.w(LOG_KEY, "Fractal class not found in registry: " + clazz);
		return null;
	}
}
