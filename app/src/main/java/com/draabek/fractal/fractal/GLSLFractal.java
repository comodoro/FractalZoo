package com.draabek.fractal.fractal;

public class GLSLFractal extends Fractal {
	protected String[] shaders = null;

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
