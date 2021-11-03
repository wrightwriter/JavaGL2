#version 430
layout (local_size_x = 32, local_size_y = 32, local_size_z = 1) in;

struct Particle {
    vec3 position;
    vec3 velocity;
};

layout (std430, binding = 0) buffer SSBO {
    Particle data[];
} ssbo;

//uniform vec3[10] u_ssboRes;




void main() {
//    ivec2 posGlobal = ivec2(gl_GlobalInvocationID.xy);
    vec2 posGlobal = vec2(gl_GlobalInvocationID.xy);
    int arrayIdx = int(gl_GlobalInvocationID.x);
//    int arrayIdx = int(gl_WorkGroupSize.x) * (posGlobal.y) + (posGlobal.x));
    float arrayIdxf = float(posGlobal.x);

//    Particle currParticle = ssbo.data[arrayIdx];
    Particle currParticle;
    currParticle.velocity = vec3(0.1);


//    if (arrayIdx < int(u_ssboRes[0].x)){
    if (arrayIdx < 100){
        currParticle.position.xy = vec2(sin( arrayIdxf*1.4), cos(sin(arrayIdxf) ));
        currParticle.velocity.xy = vec2(sin( arrayIdxf*1.1), cos(sin(arrayIdxf) ));
        currParticle.position.z = 0.0;
        ssbo.data[arrayIdx] = currParticle;
    }


}