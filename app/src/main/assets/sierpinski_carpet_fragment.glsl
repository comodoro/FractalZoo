#ifdef GL_ES
precision highp float;
#endif

uniform float centerX;
uniform float centerY;
uniform float scale;
uniform float iterations;
uniform vec2 resolution;
#define max_iterations 100

void main( void ) {
	float color=1.;
	int x = int(gl_FragCoord.x-centerX*resolution.x);
	int y = int(gl_FragCoord.y-centerY*resolution.y);
	for (int i = 0;i < max_iterations;i++) {
    if(x==0 || y==0 || float(i) > iterations) break;
    {
	    if(int(mod(float(x), 3.))==1 && int(mod(float(y),3.))==1) {
		    color = 0.;
		    break;
	    }
        x /= 3;
	    y /= 3;
    }
}
	gl_FragColor = vec4( vec3(color),1.0);

}