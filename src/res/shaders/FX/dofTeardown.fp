#version 430

#include global.include
#include utils.include

//uniform sampler2D composite;
uniform sampler2D s_InputFB[];
uniform sampler2D s_InputFBDepth;

uniform sampler2D s_InputColTex;
uniform sampler2D s_InputDepthTex;

uniform bool uRunningOnFB;


uniform float uFocusPoint;
uniform float uFocusScale;
uniform float uMaxBlurSize = 40.0;
uniform float uRadScale = .5;

in vec2 uVar;
out vec4 C;


const float GOLDEN_ANGLE = 2.39996323;

float getBlurSize(float depth, float focusPoint, float focusScale) {
    float coc = clamp( (1.0 / focusPoint - 1.0 / depth) * focusScale, -1.0, 1.0);
    return abs(coc) * uMaxBlurSize;
}

float getLinearDepth(vec2 uv){
    if(uRunningOnFB)
        return depthToLinear(texture(s_InputFBDepth, uv).x, zNear, zFar);
    else
        return depthToLinear(texture(s_InputDepthTex, uv).x, zNear, zFar);
}


vec3 depthOfField(vec2 uv, float focusPoint, float focusScale) {
    float centerDepth = getLinearDepth(uv) ;
    float centerSize = getBlurSize(centerDepth, focusPoint, focusScale);
    vec3 color = uRunningOnFB ? texture(s_InputFB[0], uv).rgb : texture(s_InputColTex, uv).rgb;
    float tot = 1.0;

    float radius = uRadScale;
    for (float ang = 0.0; radius<uMaxBlurSize; ang += GOLDEN_ANGLE) {
        vec2 tc = uv + vec2(cos(ang), sin(ang)) * 1./max(R.x,R.y) * radius * vec2(1.,R.x/R.y);

        vec3 sampleColor = uRunningOnFB ? texture(s_InputFB[0], tc).rgb : texture(s_InputColTex, tc).rgb;
        float sampleDepth = getLinearDepth(tc) ;
        float sampleSize = getBlurSize(sampleDepth, focusPoint, focusScale);
        if (sampleDepth > centerDepth)
            sampleSize = clamp(sampleSize, 0.0, centerSize*2.0);

        float m = smoothstep(radius-0.5, radius+0.5, sampleSize);
        color += mix(color/tot, sampleColor, m);
        tot += 1.0;
        radius += uRadScale/radius;
    }
    return color /= tot;
}



void main() {
    vec2 uv = U/R;

    C = depthOfField(uv, uFocusPoint, uFocusScale).xyzz;

    C.a = 1;
}
