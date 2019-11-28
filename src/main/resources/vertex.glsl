#version 330

uniform mat4 worldViewProjectionMatrix;

layout (location = 0) in vec3 position;
out vec3 pos;

void main() {
    gl_Position = worldViewProjectionMatrix * vec4(position, 1.0);
    pos = gl_Position.xyz;
}