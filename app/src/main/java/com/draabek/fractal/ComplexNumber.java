package com.draabek.fractal;

public class ComplexNumber {
	private float re;
	private float im;
	public static int instances = 0;
	
	public ComplexNumber(float re, float im) {
		this.re = re;
		this.im = im;
		instances++;
	}
	
	public ComplexNumber pow(int n) {
		return new ComplexNumber((float)Math.pow(re, n)-(float)Math.pow(im, n), n*re*im);
	}
	
	public ComplexNumber add(ComplexNumber c) {
		return new ComplexNumber(re + c.re, im + c.im);
	}
	
	public float abs() {
		return (float) Math.sqrt(re*re + im*im);
	}
	
	public String toString() {
		return "[" + re + ";" + im + "]";
	}
}
