#version 330

in vec4 vertexColor;
in float depth;
in vec3 worldPos;

uniform vec3 FogColor;
uniform float Density;
uniform float Thickness;
uniform float Time;

out vec4 fragColor;

void main() {
   float fade = 1.0 - smoothstep(0.0, 60.0, depth);
   
   // Add subtle pulsing effect
   float pulse = 0.8 + 0.2 * sin(Time * 2.0 + worldPos.x * 0.1 + worldPos.z * 0.1);
   
   // Soft edge falloff
   float edge = 1.0 - smoothstep(Thickness * 0.5, Thickness, abs(worldPos.y - CameraPos.y));
   
   float alpha = vertexColor.a * fade * pulse * edge * Density;
   
   fragColor = vec4(FogColor, alpha);
}
