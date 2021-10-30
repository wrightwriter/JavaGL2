#version 430

#include global.include

uniform sampler2D composite;

uniform sampler2D s_Input_A;
uniform sampler2D s_Input_B;
uniform sampler2D s_Input_C;
uniform sampler2D s_Input_D;

in vec2 uVar;
out vec4 C;


void main() {
    vec2 uv = U/R;
    C = texture(s_Input_A, uv);
    C = pow(C,vec4(0.45454));
    C.a = 1;
}
