package com.draabek.fractal.fractal;

import java.util.HashMap;
import java.util.Map;

public class FractalOptions {
	private Map<String,Object> options = new HashMap<String, Object>();

	Object get(String key) {
        return options.get(key);
    }

    void set(String key, Object value) {
        options.put(key, value);
    }
}
