#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 u_resolution;

void main() {
    vec2 uv = gl_FragCoord.xy/u_resolution.xy;
    
    // Create animated waves
    float waves = sin(uv.x * 10.0 + u_time) * 0.5 + 
                  cos(uv.y * 8.0 - u_time * 0.5) * 0.5;
                  sin(uv.x * 9.0 + u_time*0.99) * 0.23;
    
    // Dark blue base color with wave animation
    vec3 color = vec3(0.1, 0.1, 0.3) + waves * 0.1;
    
    gl_FragColor = vec4(color, 1.0);
} 