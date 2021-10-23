#version 430

uniform vec2 R;
uniform float Time;

uniform sampler2D composite;

in vec2 vIn;

out vec4 C;

#define U gl_FragCoord.xy

void main() {
    vec2 uv = U/R;
    C = texture(composite, uv);
    C.a = 1;
}
