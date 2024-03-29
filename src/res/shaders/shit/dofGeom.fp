
#include global.include

uniform sampler2D s_InputFB[];

out vec4 C;
	vec4 gl_Position;

in GS_OUT {
	vec2 u;
	float CoC;
	float vInstanceID;
} gs_out;


void main() {
	C = vec4(sin(gs_out.u.xyx*vec3(2.4,4.,5.)*gs_out.CoC + float(gs_out.vInstanceID)*200.)*0.5 + 0.5,0.5);
//	C += vec4(texture(s_InputFB[0], fract(gs_out.u/2. + 0.5)).xyz,0.5);
}