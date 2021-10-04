package com.draabek.fractal.gl;

import androidx.annotation.NonNull;

import com.draabek.fractal.fractal.FractalViewWrapper;
import com.draabek.fractal.fractal.Fractal;

public class GLSLFractal extends Fractal {
    /**
     * A fractal subclass rendered using the GLSL language
     * directly on the graphics card*/
	private String[] shaders = null;
	public GLSLFractal() {super();}

	@Override
	public Class<? extends FractalViewWrapper> getViewWrapper() {
		return RenderImageView.class;
	}

	public String[] getShaders() {
		return shaders;
	}

	public void setShaders(@NonNull String[] shaders) {
		this.shaders = shaders;
	}

}
