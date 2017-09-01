/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.draabek.fractal;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;

import java.io.OutputStream;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView implements FractalViewHandler {

    private MyGLRenderer mRenderer;

    public Fractal getFractal() {
        return fractal;
    }

    public void setFractal(Fractal fractal) {
        this.fractal = fractal;
    }

    private Fractal fractal;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8 , 8, 8, 8, 8, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        this.invalidate();
    }

    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float TOUCH_SCALE_FACTOR = 1.0f/Math.min(getWidth(), getHeight());

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (e.getPointerCount() == 1) {
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    Float fractalX = FractalRegistry.getInstance().getCurrent()
                            .getSettings().get("centerX");
                    Float fractalY = FractalRegistry.getInstance().getCurrent()
                            .getSettings().get("centerY");
                    if ((fractalX == null) && (fractalY == null)) {
                        Log.i(this.getClass().getName(), "Fractal has no movable center");
                    } else {
                        if (fractalX != null) {
                            FractalRegistry.getInstance().getCurrent()
                                    .getSettings().put("centerX", fractalX + dx * TOUCH_SCALE_FACTOR);
                        }
                        if (fractalY != null) {
                            //- instead of + because OpenGL has y axis upside down
                            FractalRegistry.getInstance().getCurrent()
                                    .getSettings().put("centerY", fractalY - dy * TOUCH_SCALE_FACTOR);
                        }
                    }
                } else if (e.getPointerCount() == 2) {

                }
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    @Override
    public boolean saveBitmap(OutputStream os) {
        return false;
    }
}
