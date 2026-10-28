#version 330

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;

uniform mat4 PositionMatrix;
uniform mat4 ProjectionMatrix;
uniform vec3 CameraPos;
uniform float Time;

out vec4 vertexColor;
out float depth;
out vec3 worldPos;

void main() {
   worldPos = Position + CameraPos;
   vec4 clipPos = ProjectionMatrix * PositionMatrix * vec4(worldPos, 1.0);
   
   gl_Position = clipPos;
   vertexColor = Color;
   depth = length(worldPos - CameraPos);
}
