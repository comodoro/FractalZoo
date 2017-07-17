//http://androidblog.reindustries.com/opengl-es-2-0-2d-shaders-series-001-basic-shaders/
package com.draabek.fractal;


import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;

import static com.draabek.fractal.FractalView.FRACTALS_PREFERENCE;
import static com.draabek.fractal.FractalView.PREFS_CURRENT_FRACTAL_KEY;

public class FractalGLView extends GLSurfaceView {

	private GLRenderer mRenderer;
    private Fractal fractal;
    private SharedPreferences prefs;

    public FractalGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

	public FractalGLView(Context context) {
		super(context);
        init(context);
	}

	private void init(Context context) {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new GLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        prefs = context.getSharedPreferences(FRACTALS_PREFERENCE, Context.MODE_PRIVATE);
        String name = prefs.getString(PREFS_CURRENT_FRACTAL_KEY, "Forest Fire");//getResources().getString(R.string.mandelbrot));
        fractal = FractalRegistry.getInstance().get(name);   }

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

    public Fractal getFractal() {
        return fractal;
    }

    public void setFractal(Fractal fractal) {
        this.fractal = fractal;
    }

}