precision mediump float;

uniform sampler2D palette;
uniform float centerX;
uniform float centerY;
uniform float scale;
uniform float iterations;
uniform vec2 resolution;
#define maxiter 1024

vec2 cplx2(vec2 z) {
    return vec2(z.x * z.x - z.y * z.y, z.y * z.x + z.x * z.y);
}

void main() {
    vec2 center = vec2(centerX, centerY);
    vec2 coord = vec2(gl_FragCoord.x, gl_FragCoord.y) / resolution;
    vec2 c = (coord - center) / scale;
    int j = 0;
    vec2 z = c;
    vec2 zlast = c;
    for(int i = 0; i<maxiter; i++) {
	    if (float(i) >= iterations) break;
	    j++;
        vec2 znew = cplx2((cplx2(zlast) + c - 1.) / (length(z) + c - 2.));

        if((znew.x * znew.x + znew.y * znew.y) > 4.0) break;

        zlast = z;
        z = znew;
    }

    gl_FragColor = texture2D(palette, vec2((j == int(iterations) ? 0.0 : float(j)) / iterations, 0.5));
//    vec3 color = vec3(float(j)/float(iterations));
//    gl_FragColor = vec4(color, 1.0);
}