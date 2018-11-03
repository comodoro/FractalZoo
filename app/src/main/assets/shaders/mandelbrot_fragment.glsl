precision mediump float;

uniform sampler2D palette;
uniform float centerX;
uniform float centerY;
uniform float scale;
uniform float iterations;
uniform vec2 resolution;
uniform float smoothing;
#define maxiter 65535
void main() {
    vec2 center = vec2(centerX, centerY);
    vec2 coord = vec2(gl_FragCoord.x, gl_FragCoord.y) / resolution;
    vec2 c = (coord - center) / scale;
    float j = 0.;
    vec2 z = c;
    float x;
    float y;
    for(int i = 0; i<maxiter; i++) {
	    if (float(i) >= iterations) break;
	    j++;
        x = (z.x * z.x - z.y * z.y) + c.x;
        y = (z.y * z.x + z.x * z.y) + c.y;

        if((x * x + y * y) > 4.0) break;
        z.x = x;
        z.y = y;
    }
    if (j == iterations) {
        j = 0.0;
    }
    if (smoothing == 1.0) {
        j = j + 2. - log(log(z.x*z.x + z.y*z.y)) / log(2.);
    }
    gl_FragColor = texture2D(palette, vec2(j/iterations, 0.5));
}