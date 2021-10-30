#version 430
layout (local_size_x = 32, local_size_y = 32, local_size_z = 1) in;

struct Particle {
    vec3 position;
    vec3 velocity;
};

layout (std430, binding = 0) buffer SSBO {
    Particle data[];
} ssbo;


bool reset = false;
vec2 texRes;

void check(vec2 dir, inout Particle me, int arrayIdx,int idx, vec2 puv){
    vec2 uv = puv + 0.5;
    vec2 tuv = uv*texRes;
    bool found = false;
    for(float i = 0.; i < 10; i++){
        vec2 p = tuv + i*dir;
        float otherPtr = texelFetch(particlesTracking,ivec2(p),0).x;
        if(abs(float(arrayIdx) - otherPtr)>0.5){
            me.neighbors[idx] = otherPtr;
            break;
        }
    }

}



void main() {
    ivec2 posGlobal = ivec2(gl_GlobalInvocationID.xy);
    int arrayIdx = int(gl_WorkGroupSize.x) * (posGlobal.y) + (posGlobal.x);
    float arrayIdxf = float(arrayIdx);

    Particle currParticle = ssbo.data[arrayIdx];

    texRes = textureSize(particlesTracking,0);

    vec2 puv = currParticle.position.xy ;
    float minD = 10e4;

    check( vec2(0,1), currParticle, arrayIdx, 0, puv);
    check( vec2(0,-1), currParticle, arrayIdx, 1, puv);
    check( vec2(1,0), currParticle, arrayIdx, 2, puv);
    check( vec2(-1,0), currParticle, arrayIdx, 3, puv);

    if(T <1){
        currParticle.position = r13(arrayIdxf)*2. - 1.;
        currParticle.position.xy += vec2(sin(T + arrayIdxf), cos(T*sin(arrayIdxf) ))*0.1;
        currParticle.position *= 0.2;

        currParticle.velocity = r13(float(arrayIdxf + 24124))*2. - 1.;
        currParticle.velocity *= 2;

        currParticle.neighbors = vec4(0); // uh what about 0th particle
        currParticle.velocity *= 0.02;

        ssbo.data[arrayIdx] = currParticle;
    } else {
        vec3 vel = currParticle.velocity;
        vec3 newPos = currParticle.position;

        float speed = length(vel);

        for(int i = 0; i < 4; i++){
            Particle other = ssbo.data[int(currParticle.neighbors[i])];
            vec3 to = other.position - currParticle.position;
            float l = length(to);
            vec3 toNorm = normalize(to);
            if (l < 0.14)
            vel = mix(vel,-toNorm,0.5);
            //vel += to*smoothstep(0.04,0.,l)*dT*.1;
            //float l = length(other.position.xy - nuv);
            //if(arrayIdx != otherPtr){
        }
        vel = normalize(vel)*speed;

        newPos = newPos + vel*dT;
        const float bb = 0.2;
        for(int i = 0; i < 3; i++){
            if (abs(newPos[i]) > bb){
                vel[i] *= -1;
                newPos += vel[i]*2.*dT;
            }
        }


        currParticle.position = newPos;
        currParticle.velocity = vel;
        ssbo.data[arrayIdx] = currParticle;
    }
    // Particle previous = ssbo.particle[posGlobal.x];
    // Particle previous = Particle(ssbo.position[posGlobal.x], ssbo.velocity[posGlobal.x]);
    //Particle previous = Particle(ssbo.position[posGlobal.x], ssbo.velocity[posGlobal.x]);
    //Particle next = previous;
    //vec2 positionMouse = u_positionMouse ;
    //vec2 directionMouse = normalize(positionMouse - previous.position.xy );
    /*
    if (u_timeElapsed <=  20.) {
      // ssbo.position[posGlobal.x] = vec3(0.5,0.5,1);
      ssbo.position[posGlobal.x] = r13(float(posGlobal.x) + 1.);
      ssbo.position[posGlobal.x].z = 1.;
      // ssbo.velocity[posGlobal.x] = vec3(0);
    } else {
      // -- bounds -- //
      if (previous.position.x < 0.) {
        previous.velocity.x = -1.*abs(previous.velocity.x);
      }
      if (previous.position.x > 1.){
        previous.velocity.x = 1.*abs(previous.velocity.x);
      }
      if (previous.position.y < 0.) {
        previous.velocity.y = -1.*abs(previous.velocity.y);
      }
      if (previous.position.y > 1.) {
        previous.velocity.y = 1.*abs(previous.velocity.y);
      }

      // -- mouse -- //
      if (u_mousePressed == 1.) {
        previous.velocity.x -= directionMouse.x*0.0001;
        previous.velocity.y -= directionMouse.y*0.0003;
      }

      // -- avoidance -- //
      for (int i = 0; i < u_particleCount; i++) {
        vec3 otherPos = ssbo.position[int(i)];
        vec3 direction = normalize(otherPos - previous.position);
        float dis = length(otherPos - previous.position);
        previous.velocity += direction * exp(-dis*(200. - u_avoidance*190.))*0.0001;
      }
      // -- vortex -- //
      float frId = 10.*float(posGlobal.x)/float(u_particleCount);
      vec2 vortexPos = vec2(sin(frId + u_timeElapsed*0.001), cos(frId + u_timeElapsed*0.001))*0.5 + 0.5;
      vec2 vortexDir = normalize(vortexPos - previous.position.xy);
      // previous.velocity.xy -= vortexDir*0.0001;


      // -- damping -- //
      previous.velocity *= 0.99;
      // -- gravity -- //
      previous.velocity.xy -= u_gravity*0.0003;
      next.position -= previous.velocity;
      next.velocity = previous.velocity;
      // -- bounds -- //
      // if (next.position.y == 0.) {
      //   next.position.y = abs(next.position.y);
      // }
      // next.position.y = max(next.position.y, 0.);
      // next.position.xy = max(min(next.position.xy, vec2(1.)), vec2(0));
      ssbo.position[posGlobal.x] = next.position;
      ssbo.velocity[posGlobal.x] = next.velocity;
    }
    */
}