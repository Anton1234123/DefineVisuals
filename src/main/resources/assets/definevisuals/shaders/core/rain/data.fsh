#version 330

in vec4 vertexColor;
in float depth;

uniform float Opacity;

out vec4 fragColor;

void main() {
   float fade = 1.0 - smoothstep(0.0, 50.0, depth);
   fragColor = vertexColor * Opacity * fade;
}
