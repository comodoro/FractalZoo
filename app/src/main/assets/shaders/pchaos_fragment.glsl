#ifdef GL_ES
precision mediump float;
#endif
//tweaked by psyreco

#define time 100.
uniform float centerX;
uniform float centerY;
uniform vec2 resolution;

float t=time/65.*30.,C,D;

vec2 B(vec2 a)
{
return vec2(log(sqrt(length(a))),atan(a.y,a.x)-(6.73));
}

vec3 F(vec2 E)
{
vec2 e_=E;
float c=6.3;
const int i_max=30;
for(int i=0; i<i_max; i++)
	{
	e_=B(vec2(e_.x,abs(e_.y)))+vec2(.05*sin(t/2.2)-.125,5.+.25*cos(t/5.));
	c += (length(e_));
	}
float d = (log2(log2((c)*.05))*7.);
return vec3(.2+.1*sqrt(d),.01+.5*cos(d-.1),.45+.9*sin(d-.25));
}

void main(void)
{
gl_FragColor=vec4(F((gl_FragCoord.xy/resolution.x-vec2(.5+centerX,.25+centerY))*(1.1-12.*cos(t/5.5))),1.);
}

