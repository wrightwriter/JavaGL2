#version 430

uniform vec2 R;
uniform float T;

#define U gl_FragCoord
#define C gl_FragColor
out vec4 C;
in vec2 u;


void main(){
    vec2 uv = U.xy / R.xy;
    vec2 uvn = (U.xy - 0.5*R.xy )/ R.y;

    C.xyz = sin(U.xyx*50.)*0.5 + 0.5;
    C.a = 1;
}
