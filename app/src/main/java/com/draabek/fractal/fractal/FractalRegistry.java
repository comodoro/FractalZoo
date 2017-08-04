package com.draabek.fractal.fractal;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class FractalRegistry {
	private static final String LOG_KEY = FractalRegistry.class.getName();
	private static FractalRegistry instance = null;
	private Map<String, Fractal>  fractals;
	
	private FractalRegistry() {
		fractals = new HashMap<String, Fractal>();
	}
	
	public static FractalRegistry getInstance() {
		if (instance == null) instance = new FractalRegistry();
		return instance;
	}
	
	public void add(Fractal fractal) {
		fractals.put(fractal.getName(), fractal);
	}
	
	public void remove(Fractal fractal) {
		fractals.remove(fractal);
	}
	
	public Map<String, Fractal> getFractals() {
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

	public void init(Context ctx, JsonArray props) {
		for (JsonElement element : props) {
			JsonObject jsonObject = element.getAsJsonObject();
			String clazz = jsonObject.get("class").getAsString();
            String shaders = jsonObject.get("shaders") != null ? jsonObject.get("shaders").getAsString() : null;
			String name = jsonObject.get("name").getAsString();
			String[] loadedShaders = null;
			Class cls = null;
			try {
                cls = Class.forName(clazz);
				//this is frontal lobotomy
                if (shaders != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							ctx.getAssets().open(shaders + "_fragment.glsl")
					));
					StringBuffer fragmentShader = new StringBuffer();
					String line = null;
					while ((line = br.readLine()) != null) {
						fragmentShader.append(line).append("\n");
					}
					br = new BufferedReader(new InputStreamReader(
							ctx.getAssets().open(shaders + "_vertex.glsl")
					));
					StringBuffer vertexShader = new StringBuffer();
					while ((line = br.readLine()) != null) {
						vertexShader.append(line).append("\n");
					}
					loadedShaders = new String[] {vertexShader.toString(), fragmentShader.toString()};
                }
                Fractal fractal = (Fractal)cls.newInstance();
				fractal.setName(name);
				fractal.setShaders(loadedShaders);
				add(fractal);
			} catch(ClassNotFoundException e) {
				Log.w(LOG_KEY, "Cannot find fractal class " + clazz);
			} catch(IllegalAccessException e) {
				Log.w(LOG_KEY, "Cannot access fractal class " + clazz);
			} catch(InstantiationException e) {
				Log.w(LOG_KEY, "Cannot instantiate fractal class " + clazz);
			} catch(IOException e) {
				Log.w(LOG_KEY, "IOException loading fractal " + clazz);
			}
		}
	}

	public Fractal get(String name) {
		Fractal f = fractals.get(name);
		if (f != null) return f;
        Log.w(LOG_KEY, "Fractal not found in registry: " + name);
		return null;
	}

}
