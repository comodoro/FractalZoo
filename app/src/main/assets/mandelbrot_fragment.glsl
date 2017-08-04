precision mediump float;

//uniform sampler1D tex;
uniform vec2 u_center;
uniform float u_scale;
uniform float u_iter;


void main() {
    vec2 z, c;


    c.x = 1.3333 * (gl_FragCoord.x - 0.5) * u_scale - u_center.x;
    c.y = (gl_FragCoord.y - 0.5) * u_scale - u_center.y;

    int i;
    z = c;
    for(i=0; i<u_iter; i++) {
        float x = (z.x * z.x - z.y * z.y) + c.x;
        float y = (z.y * z.x + z.x * z.y) + c.y;

        if((x * x + y * y) > 4.0) break;
        z.x = x;
        z.y = y;
    }

    //gl_FragColor = texture1D(tex, (i == u_iter ? 0.0 : float(i)) / 100.0);
    vec3 color = vec3(float(i)/u_iter);
    gl_FragColor = vec4(color, 1.0);
}