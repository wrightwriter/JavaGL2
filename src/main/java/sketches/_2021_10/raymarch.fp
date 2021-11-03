
#include global.include
#include utils.include
#include raymarch.include

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

in vec3 vEye;
in vec3 vDir;
in vec3 vForward;

vec3 divideByW( vec4 v ) {
    return v.xyz / v.w;
}

float map(vec3 p){
    p = pmod(p, 3.);
    return length(p) - 0.2;
}
void main() {
    vec2 uv = fcToUvn(U);

//    vec3 ro = divideByW(inverse(P*V)*vec4(uv,0,1));
//    uv.y *= R.x/R.y;
//    vec3 rd = normalize(divideByW(inverse(P*V)*vec4(uv,1,1)) - ro);

    vec3 ro = vEye;
//    uv.y *= R.x/R.y;
    vec3 rd = vDir;


    vec3 p = ro + rd;
    float t = 0.;
    bool hit = false;
    float d;

    for( int i = 0; i < 150; i++){
        d = map(p);
        if (d < 0.001){
            hit = true;
            break;
        }
        p = ro + rd*(t += d*0.5);
    }
    if (!hit)
        discard;

    vec4 projPos = P * V * vec4( p, 1.0 ); // terrible
    float depth = projPos.z / projPos.w;
    gl_FragDepth = 0.5 + 0.5 * depth;

    gl_FragColor = vec4(sin(vec3(1,0,0))*smoothstep(20.,0.,t), 1.0);

}

























