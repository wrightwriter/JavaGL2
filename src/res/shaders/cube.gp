#version 430

//layout(triangles) in vec3 fish;
//layout(triangle_strip, max_vertices = 3) out vec3 fish;

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;

uniform float Time;

in VS_OUT {
    vec2 u;
    vec3 p;
    vec3 norm;
} gs_in[];

out GS_OUT {
    vec2 u;
    vec3 p;
    vec3 norm;
} gs_out;

uniform mat4 M;
uniform mat4 V;
uniform mat4 P;

void main() {
    for(int i = 0; i < 3; i++) {
        gl_Position = vec4(gs_in[i].p,1);

        gs_out.p = gl_Position.xyz;

        gl_Position = P * V * M * gl_Position;

        gs_out.u = gl_Position.xy;
        gs_out.norm = gs_in[i].norm;

        EmitVertex();
    }
    EndPrimitive();
}