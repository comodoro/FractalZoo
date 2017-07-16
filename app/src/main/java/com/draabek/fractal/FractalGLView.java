//http://androidblog.reindustries.com/opengl-es-2-0-2d-shaders-series-001-basic-shaders/
package com.draabek.fractal;


import android.content.Context;
import android.opengl.GLSurfaceView;

public class FractalGLView extends GLSurfaceView {

	private final GLRenderer mRenderer;

	public FractalGLView(Context context) {
		super(context);

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new GLRenderer(context);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mRenderer.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mRenderer.onResume();
	}

}