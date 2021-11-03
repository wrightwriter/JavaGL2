#include global.include

uniform sampler2D potato;

//#define C gl_FragColor
out vec4 C;

in vec2 u;
in vec3 p;
in vec3 norm;

void main(){
    vec2 uv = U.xy / R.xy;
    vec2 uvn = (U.xy - 0.5*R.xy )/ R.y;


//    vec3 pot = texture(potato,abs(sin(gs_out.p.xy*10.)),0).xyx;
//    vec4 pot = texture(potato,abs(sin(gs_out.p.xy*10.)),0);
//    vec4 pot = texture(potato,uv);
//    vec4 pot = vec4(1);


    C.xyz = sin(norm.xyx*1.)*0.5 + 0.5;
//    C.xyz = sin(norm*2. + p.xyx*1110.
//         + Time)*0.5 + 0.5;
//    C.xyz = vec3(1);
    //    C.xyz = vec3(1,1,0);
    C.a = 1;
}
