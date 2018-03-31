//from https://www.shadertoy.com/view/Ms3XDn
#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D palette;
uniform float centerX;
uniform float centerY;
uniform float scale;
uniform float iterations;
uniform vec2 resolution;
#define maxiter 1024

// hyperbolic cosine
float cosh(float x) {
    return (exp(x) + exp(-x))/2.0;
}

// hyperbolic sine
float sinh(float x) {
    return (exp(x) - exp(-x))/2.0;
}

// complex multiplication
vec2 cmul(vec2 a, vec2 b) {
    return vec2(a.x * b.x - a.y * b.y, a.x * b.y + a.y * b.x);
}

// complex cosine
vec2 ccos(vec2 a) {
    return vec2(cos(a.x) * cosh(a.y), -sin(a.x) * sinh(a.y));
}

void main()
{
    // set viewing parameters
    vec2 center = vec2(centerX, centerY);
    float pi = 3.141592653;
 	vec2 z = (gl_FragCoord.xy / resolution.y - vec2(resolution.x * 0.5 / resolution.y, 0.5)) *scale - center;
 	int j = 0;
    for (int i = 0; i < maxiter; i++) {
        // bail out if z gets too big
        if ((length(z) > 16.0) ){
            j = i;
            break;
        }
        if (float(i) > iterations) break;
        // do one step of generalized Collatz function
        z = (vec2(1.0,0.0) + 4.0 * z - cmul(vec2(1.0,0.0) + 2.0 * z, ccos(pi * z))) / 4.0;
    }
    // colour pixel according to escape time
    float t = log(float(j + 1)) / log(1024.0);
    gl_FragColor = vec4(float(j != 0) * vec3(sqrt(t), t, 1.0 - sqrt(t)),1.0);
    //gl_FragColor = texture2D(palette, vec2((j == int(iterations) ? 0.0 : float(j)) / iterations, 0.5));
}
