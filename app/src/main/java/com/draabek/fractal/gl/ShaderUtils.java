package com.draabek.fractal.gl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.draabek.fractal.Utils;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Vojtech Drabek on 2018-03-10.
 */
public class ShaderUtils {
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
        if ((s != null) && (!s.equals(""))) Log.d(ShaderUtils.class.getName(), s);

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
    public static void checkGlError(String glOperation) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(ShaderUtils.class.getName(), glOperation + ": glError " + error);
            if (Utils.DEBUG) {
                throw new RuntimeException(String.format(Locale.US, "%s: glError %d", glOperation, error));
            }
        }
    }

    /**
     * Pass fractal settings as uniforms to the shader
     * @param settings Map<String, Float> of fractal float settings
     * @param shaderProgram Handle to the compiled shader program to attach the uniforms to
     */
    public static void applyFloatUniforms(Map<String, Float> settings, int shaderProgram) {
        for (String setting : settings.keySet()) {
            int uniformHandle = GLES20.glGetUniformLocation(shaderProgram, setting);
            if (uniformHandle == -1) {
                Log.w(ShaderUtils.class.getName(), "Unable to find uniform for " + setting);
                if (Utils.DEBUG) {
                    throw new RuntimeException("glGetUniformLocation " + setting + " error");
                }
            }
            // For now only support single float uniforms
            Object o = settings.get(setting);
            float f = (float) o;
            GLES20.glUniform1f(uniformHandle, f);
            ShaderUtils.checkGlError("glUniform1f");
        }
    }

    /**
     * Pass current width and height as uniform to the shader
     * @param width Current screen width
     * @param height Current screen height
     * @param shaderProgram Handle to the compiled shader program to attach the uniforms to
     */
    public static void applyResolutionUniform(int width, int height, int shaderProgram) {
        int resolutionHandle = GLES20.glGetUniformLocation(shaderProgram, "resolution");
        if (resolutionHandle == -1) {
            Log.w(ShaderUtils.class.getName(), "Unable to find uniform for resolution");
            if (Utils.DEBUG) {
                throw new RuntimeException("glGetUniformLocation resolution error");
            }
        }
        GLES20.glUniform2f(resolutionHandle, width, height);
        ShaderUtils.checkGlError("glUniform2f");
    }

    /**
     * Loads a texture bitmap into OpenGL. Used for palettes
     * @param bitmap The bitmap to load
     * @return The loaded texture ID
     */
    public static int loadTexture(Bitmap bitmap)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error generating texture name.");
        }

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();

        return textureHandle[0];
    }
}
