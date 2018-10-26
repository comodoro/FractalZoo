precision mediump float;

uniform sampler2D palette;
uniform float centerX;
uniform float centerY;
uniform float scale;
uniform float iterations;
uniform vec2 resolution;
uniform float exponent;
#define maxiter 65535

vec2 cplx_polar(vec2 z) {
    return vec2(length(z), atan(z.y,z.x));
}

vec2 cplx_polar_add(vec2 z1, vec2 z2) {
    //https://math.stackexchange.com/a/1365938
    return vec2(sqrt(z1.x*z1.x + z2.x*z2.x + 2.*z1.x*z2.x*cos(z2.y-z1.y)),
    z1.y+atan(z2.x*sin(z2.y-z1.y),(z1.x+z2.x*cos(z2.y-z1.y))));
}

vec2 exponentiate(vec2 z) {
    return pow(z.x, exponent)* vec2(cos(z.y), sin(z.y));
}

void main() {
    vec2 center = vec2(centerX, centerY);
    vec2 coord = vec2(gl_FragCoord.x, gl_FragCoord.y) / resolution;
    vec2 c = cplx_polar((coord - center) / scale);
    int j = 0;
    vec2 z = c;
    for(int i = 0; i<maxiter; i++) {
	    if (float(i) >= iterations) break;
	    j++;
	    vec2 znew = cplx_polar_add(exponentiate(z), c);
        if(znew.x > 2.0) break;
        z = znew;
    }
    gl_FragColor = texture2D(palette, vec2((j == int(iterations) ? 0.0 : float(j)) / iterations, 0.5));
}