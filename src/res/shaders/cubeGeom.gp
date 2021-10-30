#version 430

#include global.include
//layout(triangles) in vec3 fish;
//layout(triangle_strip, max_vertices = 3) out vec3 fish;

layout(triangles) in;
layout(triangle_strip, max_vertices = 3) out;


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


void main() {
    for(int i = 0; i < 3; i++) {
        gl_Position = vec4(gs_in[i].p,1);
//        float vid = float(gl_PrimitiveIDIn);
//        gl_Position = vec4(
//            sin(i + Time + vid),
//            sin(float(i)*1.5 + Time + vid),
//            sin(float(i)*2.5 + Time + vid) + 6.,
//        1.
//        );
        gs_out.p = gl_Position.xyz;

//        gl_Position.z -= 10.;
        mat4 mvp = P * V * mat4(1);
        gl_Position = mvp * gl_Position;
        gl_Position.x /= R.x / R.y;

        gs_out.u = gl_Position.xy;
        gs_out.norm = gs_in[i].norm;

        EmitVertex();
    }
    EndPrimitive();
}