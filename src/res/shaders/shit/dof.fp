#include global.include
#include utils.include

uniform sampler2D s_InputFB[];
uniform sampler2D s_InputFBDepth;

uniform sampler2D s_PrepassFB[];
uniform sampler2D s_FilterpassFB[];
uniform sampler2D s_BokehpassFB[];

#define s_Near s_BokehpassFB[0]
#define s_Far  s_BokehpassFB[1]

out vec4 C;


void main(void) {
    vec2 uv = U/R;


    C = vec4(
        texture(s_Far,uv).xyz
//        texture(s_Near,uv).xy
//            texture(s_FilterpassFB[0],uv).xyz +
//            texture(s_FilterpassFB[1],uv).xyz
        ,1.
    );
//    C = vec4(accFar + accNear + texture(s_InputFB[0],uv).xyz*max(0.,unCoc),1.);

    C = C/(1. + C)*1.;
    C.xyz = ACESFilm(C.xyz);
    C += abs(sin(
        sin(dot(U*124.,sin(U*100.)*100.))*10. +
        sin(dot(U*24.,sin(U*55.)*58.))*10. +
        + mod(Time*1.,10.)*10.
    ) )*0.008;
    C = pow(max(C,0.),vec4(0.45454));
    //    C = vec4(acc,CoC);
}
