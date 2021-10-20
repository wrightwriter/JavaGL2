#version 430

uniform vec2 R;
uniform float Time;

uniform sampler2D gAlbedo;
uniform sampler2D gNormals;
uniform sampler2D gPosition;
uniform sampler2D gDepth;

in vec2 vIn;

out vec4 C;

#define U gl_FragCoord

void main() {
    vec2 uv = U/R;
    C = texture(gAlbedo, uv);
    C.a = 1;
}
