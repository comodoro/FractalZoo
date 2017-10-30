package com.draabek.fractal.gl;

import com.draabek.fractal.fractal.Fractal;

public class GLSLFractal extends Fractal {
	private String[] shaders = null;
	public GLSLFractal() {super();}

	public String[] getShaders() {
		return shaders;
	}

	public void setShaders(String[] shaders) {
		this.shaders = shaders;
	}
}
