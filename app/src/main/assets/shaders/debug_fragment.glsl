precision mediump float;

//uniform sampler1D tex;
uniform vec2 u_center;
//const vec2 u_center = vec2(3,2);
uniform float u_scale;
//const float u_scale = 0.01;
//#define u_scale 1.0
uniform int u_iter;
//const int u_iter = 1000;
#define maxiter 1024
void main() {
    vec2 z, c;


    c.x = (gl_FragCoord.x - u_center.x) * u_scale;
    c.y = (gl_FragCoord.y - u_center.y) * u_scale;

    int j = 0;
    z = c;
    for(int i = 0; i<maxiter; i++) {
	    if (i >= u_iter) break;
	    j++;
        float x = (z.x * z.x - z.y * z.y) + c.x;
        float y = (z.y * z.x + z.x * z.y) + c.y;

        if((x * x + y * y) > 4.0) break;
        z.x = x;
        z.y = y;
    }

    //gl_FragColor = texture1D(tex, (i == u_iter ? 0.0 : float(i)) / 100.0);
    vec3 color = vec3(float(j)/float(u_iter));
    gl_FragColor = vec4(color, 1.0);
}