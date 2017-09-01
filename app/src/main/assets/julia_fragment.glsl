precision mediump float;

//uniform sampler1D tex;
uniform float centerX;
uniform float centerY;
uniform float scale;
uniform float iterations;
uniform vec2 resolution;
#define maxiter 1024
#define cX -0.7
#define cY 0.27015

void main() {
    vec2 center = vec2(centerX, centerY);
    vec2 coord = vec2(gl_FragCoord.x, gl_FragCoord.y) / resolution;
    vec2 c = (coord - center) / scale;
    int j = 0;
    vec2 z = c;
    for(int i = 0; i<maxiter; i++) {
	    if (float(i) >= iterations) break;
	    j++;
        float x = (z.x * z.x - z.y * z.y) + cX;
        float y = (z.y * z.x + z.x * z.y) + cY;

        if((x * x + y * y) > 4.0) break;
        z.x = x;
        z.y = y;
    }

    //gl_FragColor = texture1D(tex, (i == u_iter ? 0.0 : float(i)) / 100.0);
    vec3 color = vec3(float(j)/float(iterations));
    gl_FragColor = vec4(color, 1.0);
}