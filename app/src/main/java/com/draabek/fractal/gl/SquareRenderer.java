package com.draabek.fractal.gl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Simple Renderer for PixelBuffer
 * Created by Vojtech Drabek on 2018-01-13.
 */

public class SquareRenderer implements Renderer {

    private Square mSquare;

    private int width;
    private int height;
    private boolean renderInProgress;

    public SquareRenderer() {}
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mSquare = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        renderInProgress = true;
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSquare.draw(width, height);
        renderInProgress = false;
    }

    public boolean isRenderInProgress() {
        return renderInProgress;
    }
}
