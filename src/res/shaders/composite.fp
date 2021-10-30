#version 430


#include global.include

uniform sampler2D s_Input_A;
uniform sampler2D s_Input_B;
uniform sampler2D s_Input_C;
uniform sampler2D s_Input_D;

uniform sampler2D s_PrevFrame_A;

in vec2 uVar;

//out vec4 C;



void main() {
    vec2 uv = U/R;
    vec4 C = texture(s_Input_A, fract(uv));
    C = mix(C,texture(s_PrevFrame_A, fract(uv)),0.95);

//    C = 0.5 + 0.5*sin(uv.xyxy*20. + Time);
    C.a = 1;
    gl_FragColor = C;
}
