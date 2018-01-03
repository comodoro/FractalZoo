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

import android.opengl.GLES20;
import android.util.Log;

import com.draabek.fractal.Utils;
import com.draabek.fractal.fractal.Fractal;
import com.draabek.fractal.fractal.FractalRegistry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Map;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Square {

    static final String LOG_KEY = Square.class.getName();
    private final int[] extraBufferId = new int[1];
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private int mProgram;
    private GLSLFractal currentFractal;

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static float squareCoords[] = {
            -1.0f,  1.0f, 0.0f,   // top right
            -1.0f, -1.0f, 0.0f,   // bottom right
            1.0f, -1.0f, 0.0f,   // bottom left
            1.0f,  1.0f, 0.0f }; // top left

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private static final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Square() {

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        updateCurrentFractal();
     }

    public void updateCurrentFractal () {
        Fractal f = FractalRegistry.getInstance().getCurrent();
        if (!(f instanceof GLSLFractal)) {
            throw new IllegalStateException("Current fractal not instance of " + GLSLFractal.class.getName());
        }
        currentFractal = (GLSLFractal)f;
        updateShaders();
    }

    private void updateShaders() {
        // prepare shaders and OpenGL program
        int vertexShader = MyGLSurfaceView.loadShader(
                GLES20.GL_VERTEX_SHADER,
                currentFractal.getShaders()[0]);
        int fragmentShader = MyGLSurfaceView.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                currentFractal.getShaders()[1]);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(this.getClass().getName(), "Could not link program");
            String infoLog = GLES20.glGetShaderInfoLog(mProgram);
            GLES20.glDeleteProgram(mProgram);
            GLES20.glFlush();
            mProgram = 0;
            String msg = String.format("Failed to compile shader for %s\n%s",
                    FractalRegistry.getInstance().getCurrent().getName(), infoLog);
            Log.e(LOG_KEY,  msg);
            //this sequence is strange, hopefully there will not be infinite loop
            FractalRegistry.getInstance().setCurrent(
                    FractalRegistry.getInstance().get("Mandelbrot")
            );
            updateCurrentFractal();
        }
        if (FractalRegistry.getInstance().getCurrent().getParameters().get("glBuffer") != null) {
            GLES20.glGenFramebuffers( 1, extraBufferId, 0 );
            GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, extraBufferId[0]);
        }
    }
    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     */
    public void draw(int width, int height) {
        if (currentFractal != FractalRegistry.getInstance().getCurrent()) {
            updateCurrentFractal();
        }
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the square vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the square coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Get rendering parameters and apply as uniforms
        Map<String, Float> settings = currentFractal.getParameters();
        for (String setting : settings.keySet()) {
            int uniformHandle = GLES20.glGetUniformLocation(mProgram, setting);
            if (uniformHandle == -1) {
                Log.w(this.getClass().getName(), "Unable to find uniform for " + setting);
                if (Utils.DEBUG) {
                    throw new RuntimeException("glGetUniformLocation " + setting + " error");
                }
            }
            // For now only support single float uniforms
            Object o = settings.get(setting);
            float f = (float)o;
            GLES20.glUniform1f(uniformHandle, f);
            MyGLSurfaceView.checkGlError("glUniform1f");
        }

        int resolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");
        if (resolutionHandle == -1) {
            Log.w(this.getClass().getName(), "Unable to find uniform for resolution");
            if (Utils.DEBUG) {
                throw new RuntimeException("glGetUniformLocation resolution error");
            }
        }
        GLES20.glUniform2f(resolutionHandle, width, height);
        MyGLSurfaceView.checkGlError("glUniform2f");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}