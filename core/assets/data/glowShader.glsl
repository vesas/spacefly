#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 vColor;
varying vec2 vTexCoord;

const float NOISE_GRANULARITY = 1.5/255.0;

//texture samplers
uniform sampler2D u_texture; //diffuse map
uniform sampler2D u_lightmap;   //light map

//additional parameters for the shader
uniform float glow_amount; // how much we want to glow
// uniform LOWP vec4 ambientColor; //ambient RGB, alpha channel is intensity 

float random(vec2 coords) {
   return fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453);
}

void main() {

   vec4 diffuseColor = texture2D(u_texture, vTexCoord);
	gl_FragColor = vec4(0.6,0.5,1.0,0.5) * 0.5 * glow_amount + diffuseColor * 0.5;
}
