#include global.include
//#include raymarch.include

uniform sampler2D potato;

out vec4 C;


in GS_OUT {
    vec2 u;
    vec3 p;
    vec3 norm;
} gs_out;

in float vPointSz;
in float vPointID;
in vec2 vPointPosNDC;

void main(){
    vec2 uv = (U.xy / R.xy - 0.5)*2.;
    vec2 uvn = (U.xy - 0.5*R.xy )/ R.xy;

    float aspectRatio = R.x/R.y;
    float d = length((uv - vPointPosNDC)*vec2(aspectRatio,1.)) - vPointSz*0.5;
    vec3 col = vec3(0.9,1.,1)*sin(vec3(4,2,1) + Time + gs_out.p.xxx*2000. + vPointID) + vec3(0.6,1.,1.);
    C = mix(vec4(col,0.),vec4(col,1), smoothstep(fwidth(uv.y),0.,d));
//    C = vec4(1);
    if (C.a < 0.1)
        discard;
}
