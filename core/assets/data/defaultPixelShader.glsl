#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 vColor;
varying vec2 vTexCoord;

//texture samplers
uniform sampler2D u_texture; //diffuse map
uniform sampler2D u_lightmap;   //light map

//additional parameters for the shader
uniform vec2 resolution; //resolution of screen
uniform LOWP vec4 ambientColor; //ambient RGB, alpha channel is intensity 


void main() {
	vec4 diffuseColor = texture2D(u_texture, vTexCoord);
	vec2 lighCoord = (gl_FragCoord.xy / resolution.xy);
	vec4 light = texture2D(u_lightmap, lighCoord);
	
	vec3 ambient = ambientColor.rgb * ambientColor.a;
	
	vec2 center = vec2(resolution.x/2., resolution.y/2.);
	
	float bug = 0.0f;
	vec2 aux=(gl_FragCoord.xy-center);
    float dist=length(aux);
    
    if( dist > 250.1 )
    	bug = 1.0;
    	
    float adjust = 1.0;
    
    if( dist > 1.9f )
    {
    	adjust = adjust - ( dist * 0.003f - 1.9f );
    }
    
    if( adjust > 1.0f )
    	adjust = 1.0f;
    	
    if( adjust < 0.0f )
    	adjust = 0.0f;
    
	vec3 intensity = ambient + light.rgb * adjust;
	
 	vec3 finalColor = diffuseColor.rgb * intensity ;
 	
 	//finalColor.r += bug;
 	
	gl_FragColor = vColor * vec4(finalColor, diffuseColor.a);
}
