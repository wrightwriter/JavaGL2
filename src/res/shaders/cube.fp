#version 430

uniform vec2 R;

#define U gl_FragCoord
//#define C gl_FragColor
out vec4 C;


in GS_OUT {
    vec2 u;
    vec3 p;
    vec3 norm;
} gs_out;

void main(){
    vec2 uv = U.xy / R.xy;
    vec2 uvn = (U.xy - 0.5*R.xy )/ R.y;


    C.xyz = sin(gs_out.norm*2. + gs_out.p*10.)*0.5 + 0.5;
//    C.xyz = vec3(1,1,0);
    C.a = 1;
}
