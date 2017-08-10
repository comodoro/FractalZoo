precision mediump float;

//uniform sampler1D tex;
uniform float centerX;
uniform float centerY;
uniform float scale;
uniform float iterations;
#define maxiter 1024
void main() {
    vec2 z, c;


    c.x = (gl_FragCoord.x - centerX) * scale;
    c.y = (gl_FragCoord.y - centerY) * scale;

    int j = 0;
    z = c;
    for(int i = 0; i<maxiter; i++) {
	    if (float(i) >= iterations) break;
	    j++;
        float x = (z.x * z.x - z.y * z.y) + c.x;
        float y = (z.y * z.x + z.x * z.y) + c.y;

        if((x * x + y * y) > 4.0) break;
        z.x = x;
        z.y = y;
    }

    //gl_FragColor = texture1D(tex, (i == u_iter ? 0.0 : float(i)) / 100.0);
    vec3 color = vec3(float(j)/float(iterations));
    gl_FragColor = vec4(color, 1.0);
}