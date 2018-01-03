package com.draabek.fractal.gl;

import android.support.annotation.NonNull;

import com.draabek.fractal.FractalViewWrapper;
import com.draabek.fractal.fractal.Fractal;

public class GLSLFractal extends Fractal {
	private String[] shaders = null;
	public GLSLFractal() {super();}

	@Override
	public Class<? extends FractalViewWrapper> getViewWrapper() {
		return MyGLSurfaceView.class;
	}

	public String[] getShaders() {
		return shaders;
	}

	public void setShaders(@NonNull String[] shaders) {
		this.shaders = shaders;
	}
}
