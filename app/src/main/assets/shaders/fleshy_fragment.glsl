//from http://glslsandbox.com/e#42192.1
#ifdef GL_ES
precision mediump float;
#endif
#define time 10.
uniform float centerX;
uniform float centerY;
uniform vec2 resolution;

float t=time/65.*30.,C,D;

vec3 F(vec2 E)
{
vec2 e_=E;
float c=6.;
const int i_max=30;
for(int i=0; i<i_max; i++)
	{
		vec2 a = vec2(e_.x,abs(e_.y));
		vec2 aa = vec2(log(length(a)),atan(a.y,a.x)-6.3);
	e_=aa+vec2(.1*sin(t/3.)-.1,5.+.1*cos(t/5.));
	c += length(e_);
	}
float d = log2(log2(c*.05))*6.;
return vec3(.7+.7*cos(d),.5+.5*cos(d-.7),.7+.7*cos(d-.7));
}

void main(void)
{
gl_FragColor=vec4(F((gl_FragCoord.xy/resolution.x-vec2(.3+centerX,.4+centerY))*(9.1-9.*cos(t/9.))),1.);
}