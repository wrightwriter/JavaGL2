#version 430

#include global.include
#include utils.include

uniform sampler2D s_InputColTex;
uniform float uInnerEdge;
uniform float uOuterEdge;
uniform float uIntensity;
uniform float uSteps;

in vec2 uVar;
out vec4 C;

// https://www.shadertoy.com/view/wt2GDW

void main() {
    const vec2 uv = (uVar + 1.)/2.;
//    vec2 nuv = (U - 0.5*R)/R.y;

    vec2 lens_uv = uv;
    vec2 lens_pos = vec2(0.5);

    vec2 lens_delta = (lens_uv - lens_pos);
    float lens_dist = length(lens_delta);

    // Knobs to control the size and the "zoom" amount of the lens
    float lens_radius = 1.;
    float lens_zoom =2.0;

    // pretend that the lens is spherical
    // For the z component, see https://www.desmos.com/calculator/5p0apo0bqm
    // Fudge the z component for stylistic control
    float lens_radius_fudge = 1.;
    vec3 lens_normal = normalize(vec3(lens_delta.xy, lens_zoom * sqrt(lens_radius_fudge * lens_radius - lens_dist*lens_dist)));
    // the incoming light direction
    vec3 incident = normalize(vec3(0.0, 0., -1.0));

    // ior ratios of (medium A)/(medium B).
    // medium A is outside the lens, medium B is inside the lens
    // - Use an ior of 1.0, corresponding to air, for medium A
    // - Use a slightly higher ior for medium B. Tune to taste!
    // See
    // - https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/refract.xhtml
    // - https://pixelandpoly.com/ior.html
    float eta_r = 1.0 / 1.15;
    float eta_y = 1.0 / 1.17;
    float eta_g = 1.0 / 1.19;
    float eta_c = 1.0 / 1.21;
    float eta_b = 1.0 / 1.23;
    float eta_v = 1.0 / 1.25;


    // Calculate different refraction vectors for each color channel
    vec2 refract_r = refract(incident, lens_normal, eta_r).xy;
    vec2 refract_y = refract(incident, lens_normal, eta_y).xy;
    vec2 refract_g = refract(incident, lens_normal, eta_g).xy;
    vec2 refract_c = refract(incident, lens_normal, eta_c).xy;
    vec2 refract_b = refract(incident, lens_normal, eta_b).xy;
    vec2 refract_v = refract(incident, lens_normal, eta_v).xy;

    vec3 tex = texture(s_InputColTex, uv).rgb;
    vec3 tex_r = texture(s_InputColTex, refract_r + uv).rgb;
    vec3 tex_y = texture(s_InputColTex, refract_y + uv).rgb;
    vec3 tex_g = texture(s_InputColTex, refract_g + uv).rgb;
    vec3 tex_c = texture(s_InputColTex, refract_c + uv).rgb;
    vec3 tex_b = texture(s_InputColTex, refract_b + uv).rgb;
    vec3 tex_v = texture(s_InputColTex, refract_v + uv).rgb;

    float r = tex_r.r * 0.5;
    float g = tex_g.g * 0.5;
    float b = tex_b.b * 0.5;
    float y = dot(vec3(2.0, 2.0, -1.0), tex_y)/6.0;
    float c = dot(vec3(-1.0, 2.0, 2.0), tex_c)/6.0;
    float v = dot(vec3(2.0, -1.0, 2.0), tex_v)/6.0;

    float R = r + (2.0 * v + 2.0 * y - c)/3.0;
    float G = g + (2.0 * y + 2.0 * c - v)/3.0;
    float B = b + (2.0 * c + 2.0 * v - y)/3.0;

    vec3 color = mix(tex, vec3(R, G, B), step(lens_dist, lens_radius));

    C = vec4(color, 1.0);

//    const float amount = intensity * 0.001 * smoothstep(innerEdge, outerEdge, length(nuv));
//
//    const vec2 direction = normalize(nuv);
//
//    vec2 offset = vec2(0);
//    vec3 col = vec3(0);
//
//    for(float i = 0.; i < uSteps; i++){
//        const vec2 chromAbUv = uv + offset;
//        offset += direction * amount / uSteps;
//
//        col.r += texture(s_InputColTex, chromabUv + offset).r;
//        col.g += texture(s_InputColTex, chromabUv ).g;
//        col.b += texture(s_InputColTex, chromabUv - offset).b;
//    }
//    col /= uSteps;
//
//    C.xyz = col;
}
