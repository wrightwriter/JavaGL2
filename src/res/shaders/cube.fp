#version 430

uniform vec2 R;

#define U gl_FragCoord
//#define C gl_FragColor
out vec4 C;

in vec2 uVar;
in vec3 posVar;
in vec3 nVar;


void main(){
    vec2 uv = U.xy / R.xy;
    vec2 uvn = (U.xy - 0.5*R.xy )/ R.y;

    C.xyz = sin(nVar*2. + posVar*20.)*0.5 + 0.5;
//    C.xyz = vec3(1,1,0);
    C.a = 1;
}
