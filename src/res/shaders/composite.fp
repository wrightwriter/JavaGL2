#version 430

uniform vec2 R;
uniform float Time;

uniform sampler2D gA;
uniform sampler2D gB;
uniform sampler2D gC;
uniform sampler2D gD;

in vec2 uVar;

out vec4 C;

#define U gl_FragCoord.xy


void main() {
    vec2 uv = U/R;
    C = texture(gA, fract(uv));
    C = 0.5 + 0.5*sin(uv.xyxy*20. + Time);
    C.a = 1;
}
