package com.draabek.fractal.gl;

import com.draabek.fractal.fractal.Fractal;

public class GLSLFractal extends Fractal {
	protected String[] shaders = null;

	public GLSLFractal() {super();}
	public GLSLFractal(String name, String vertexShader, String fragmentShader) {
		super(name);
		this.shaders = new String[] {vertexShader, fragmentShader};
	}

	public String[] getShaders() {
		return shaders;
	}

	public void setShaders(String[] shaders) {
		this.shaders = shaders;
	}


}
