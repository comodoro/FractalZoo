#ifdef GL_ES
  precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform float centerX;
uniform float centerY;
uniform vec2 resolution;

// roentgen.glsl
// Leon 05 / 07 / 2017
// using lines of code of IQ, Mercury, LJ, Koltes, Duke
// tweaked by psyreco

#define PI 3.1415926535
#define TAU PI*2.

#define DITHER
#define STEPS 35.
#define BIAS 0.005
#define DIST_MIN 0.01

const vec4 baseColor = vec4 (0.3, 0.4, 1.0, 1.0);

mat2 rot (float a)             { float c=cos(a), s=sin(a); return mat2(c,-s,s,c); }
float sphere (vec3 p, float r) { return length(p)-r; }
float cyl (vec2 p, float r)    { return length(p)-r; }
float torus( vec3 p, vec2 s )  { return length(vec2(length(p.xz)-s.x,p.y))-s.y; }

float smin (float a, float b, float r)
{
    float h = clamp(.5+.5*(b-a)/r,0.,1.);
    return mix(b,a,h)-r*h*(1.-h);
}

float rand(vec2 co)     { return fract(sin(dot(co*0.123,vec2(12.9898,78.233))) * 43758.5453); }

vec3 camera (vec3 p)
{
    p.xz *= rot(PI*(centerX / resolution.y-.5) * centerX);
    p.yz *= rot(PI*(centerY / resolution.y-.5) * centerY);
    return p;
}

float map (vec3 p)
{
  vec3 p1 = p;
  float geo = fract(2.5);
  float cy = 0.5;
  const float repeat = 9.;
  p1.xy *= rot(length(p)*.5);
  float t = time*0.001;
  for (float i = 0.; i < repeat; ++i)
  {
    p1.yz *= rot(0.3+t*0.5);
    p1.xy *= rot(0.2+t);
    p1.xz *= rot(.15+t*2.);
    p1.xy *= rot(p.x*.5+t);

    // gyroscope
    geo = abs(smin(geo, torus(p1,vec2(1.+i*.2,.01)), .5));

    // tentacles cylinders
    geo = abs(smin(geo, sin(cyl(p1.xz,.04)), fract(.5)));

    // torus along the cylinders
    vec3 p2 = p1;
    p2.y *= mod(p2.y,log2(cy)) - log2(cy)/2.0;
    geo =abs(smin(geo, torus(p2,vec2(abs(.4*fract(p2.y)),.01)), .2));
  }
  return geo;
}

void main( void )
{
  vec2 uv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
  vec3 eye = camera(vec3(uv,-3));
  vec3 ray = camera(normalize(vec3(uv,.5)));
  vec3 pos = eye;
  float shade = 0.0;
  #ifdef DITHER
     vec2 dpos = ( gl_FragCoord.xy / resolution.xy );
     vec2 seed = dpos;
  #endif
  for (float i = 0.0; i < STEPS; ++i)
  {
    float dist = map(pos);
    if (dist < BIAS)
      shade += 2.0;

    #ifdef DITHER
      dist=abs(dist)*(.8+0.2*rand(seed*vec2(i)));
    #endif
    dist = max(DIST_MIN,dist);
    pos += ray*dist;
  }
  vec4 color = vec4(shade * baseColor / STEPS);
  gl_FragColor = color;
}
