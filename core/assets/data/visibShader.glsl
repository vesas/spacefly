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
uniform vec2 resolution; //resolution of screen
uniform LOWP vec4 ambientColor; //ambient RGB, alpha channel is intensity 

float random(vec2 coords) {
   return fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
	vec4 diffuseColor = texture2D(u_texture, vTexCoord);
	vec2 lighCoord = (gl_FragCoord.xy / resolution.xy);
	vec4 light = texture2D(u_lightmap, lighCoord);

	vec2 coordinates = gl_FragCoord.xy / resolution;
	
	vec3 ambient = ambientColor.rgb * ambientColor.a;
	
	vec2 center = vec2(resolution.x/2., resolution.y/2.);
	
	float bug = 0.0;
	vec2 aux=(gl_FragCoord.xy-center) / resolution.x;
    float dist=length(aux) * 500.0;
    
    // Replace the hard cutoff with smooth falloff
    float maxDist = 195.7;
    float startFade = 90.0;
    float adjust = 1.0 - smoothstep(startFade, maxDist, dist);
    
    // Add noise to the falloff
    adjust += mix(-NOISE_GRANULARITY, NOISE_GRANULARITY, random(coordinates));
    adjust = clamp(adjust, 0.0, 1.0);
    
    vec3 intensity = ambient + light.rgb * adjust;
	
 	vec3 finalColor = diffuseColor.rgb * intensity ;
 	
 	//finalColor.r += bug;
 	
	gl_FragColor = vColor * vec4(finalColor, diffuseColor.a);
}
