//http://androidblog.reindustries.com/opengl-es-2-0-2d-shaders-series-001-basic-shaders/
package com.draabek.fractal;


import android.opengl.GLES20;

public class ShaderLoader {

    // Program variables
    public static int sp_SolidColor;

    /* SHADER Solid
     *
     * This shader is for rendering a colored primitive.
     *
     */
    public static final String vs_SolidColor =
            "uniform    mat4        uMVPMatrix;" +
                    "attribute  vec4        vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    public static final String fs_SolidColor =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(0.5,0,0,1);" +
                    "}";

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // return the shader
        return shader;
    }
}