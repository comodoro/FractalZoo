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
package com.draabek.fractal.gl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.draabek.fractal.FractalViewWrapper;
import com.draabek.fractal.R;
import com.draabek.fractal.RenderListener;
import com.draabek.fractal.SaveBitmapActivity;
import com.draabek.fractal.Utils;
import com.draabek.fractal.fractal.FractalRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.graphics.Bitmap.createBitmap;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView implements FractalViewWrapper {

    private MyGLRenderer mRenderer;

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
        setEGLConfigChooser(8, 8, 8, 8, 8, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //this.invalidate();
    }

    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousX2;
    private float mPreviousY2;

    public boolean isRendering() {
        return mRenderer.renderInProgress;
    }

    @Override
    public void setRenderListener(RenderListener renderListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float TOUCH_SCALE_FACTOR = 1.5f/Math.min(getWidth(), getHeight());

        float x = e.getX();
        float y = e.getY();
        float x2 = 0;
        float y2 = 0;
        if (e.getPointerCount() > 1) {
            x2 = e.getX(1);
            y2 = e.getY(1);
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (Utils.DEBUG) {
                    Log.d(this.getClass().getName(), "GL MOVE");
                }
                if (e.getPointerCount() == 1) {
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    Float fractalX = FractalRegistry.getInstance().getCurrent()
                            .getParameters().get("centerX");
                    Float fractalY = FractalRegistry.getInstance().getCurrent()
                            .getParameters().get("centerY");
                    if ((fractalX == null) && (fractalY == null)) {
                        Log.i(this.getClass().getName(), "Fractal has no movable center");
                    } else {
                        if (fractalX != null) {
                            FractalRegistry.getInstance().getCurrent()
                                    .getParameters().put("centerX", fractalX + dx * TOUCH_SCALE_FACTOR);
                            Log.v(this.getClass().getName(), "X shift: " + dx * TOUCH_SCALE_FACTOR);
                        }
                        if (fractalY != null) {
                            //- instead of + because OpenGL has y axis upside down
                            FractalRegistry.getInstance().getCurrent()
                                    .getParameters().put("centerY", fractalY - dy * TOUCH_SCALE_FACTOR);
                            Log.v(this.getClass().getName(), "Y shift: " + dy * TOUCH_SCALE_FACTOR);
                        }
                    }
                } else if ((e.getPointerCount() == 2) && ((mPreviousY2 > 0) || (mPreviousX2 > 0))) {
                    Float scale = FractalRegistry.getInstance().getCurrent()
                            .getParameters().get("scale");
                    if (scale == null) {
                        Log.i(this.getClass().getName(), "Fractal is not scaleable");
                    } else {
                        // Probably abs() is sufficient, but this is better for clarity
                        float oldDist = (float) Math.sqrt((mPreviousX - mPreviousX2) * (mPreviousX - mPreviousX2) +
                                (mPreviousY - mPreviousY2) * (mPreviousY - mPreviousY2));
                        float newDist = (float) Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
                        if (oldDist > 0) {
                            FractalRegistry.getInstance().getCurrent().getParameters().put("scale",
                                    scale * newDist / oldDist);
                            Log.v(this.getClass().getName(), "Scale: " + scale * newDist / oldDist);
                        }
                    }
                }
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        mPreviousX2 = x2;
        mPreviousY2 = y2;
        return true;
    }

    private void captureBitmapCallback(Bitmap bitmap) {
        try {
            File tmpFile = File.createTempFile("bitmap", "jpg", getContext().getCacheDir());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(tmpFile));
            Intent intent = new Intent(this.getContext(), SaveBitmapActivity.class);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(getContext().getString(R.string.intent_extra_bitmap_file), tmpFile.getAbsolutePath());
            getContext().startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(this.getContext(), "Could not save current image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void saveBitmap() {
        mRenderer.captureSurface();
        requestRender();
    }

    /**
     * Utility method for compiling a OpenGL shader.
     * <p>
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        String s = GLES20.glGetShaderInfoLog(shader);
        if ((s != null) && (!s.equals(""))) Log.d(MyGLSurfaceView.class.getName(), s);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    static void checkGlError(String glOperation) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(MyGLSurfaceView.class.getName(), glOperation + ": glError " + error);
            if (Utils.DEBUG) {
                throw new RuntimeException(String.format(Locale.US, "%s: glError %d", glOperation, error));
            }
        }
    }

    private class MyGLRenderer implements GLSurfaceView.Renderer {

        private final String TAG = MyGLRenderer.class.getName();
        private Square mSquare;

        private int width;
        private int height;
        private boolean capturing;
        private boolean renderInProgress;

        @Override
        public void onSurfaceCreated(GL10 unused, EGLConfig config) {

            // Set the background frame color
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            mSquare   = new Square();
            capturing = false;
        }

        void captureSurface() {
            capturing = true;
        }

        private Bitmap saveCurrentSurface(int width, int height) {
            ByteBuffer bb = ByteBuffer.allocate(width * height*4);

            GLES20.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);

            Bitmap orig = createBitmap(width, height, Bitmap.Config.ARGB_8888);
            orig.copyPixelsFromBuffer(bb);

            Matrix matrix = new Matrix();
            matrix.postScale(1, -1, width / 2.0f, height / 2.0f);
            return createBitmap(orig, 0, 0, width, height, matrix, true);
        }

        @Override
        public void onDrawFrame(GL10 unused) {
            renderInProgress = true;
            // Draw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            // Draw square
            mSquare.draw(width, height);
            //MyGLSurfaceView.this.post(() -> MyGLSurfaceView.this.setVisibility(VISIBLE));
            if (capturing) {
                MyGLSurfaceView.this.captureBitmapCallback(saveCurrentSurface(width, height));
                capturing = false;

            }
            renderInProgress = false;
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            // Adjust the viewport based on geometry changes,
            // such as screen rotation
            GLES20.glViewport(0, 0, width, height);
            this.width = width;
            this.height = height;
        }



    }
}
