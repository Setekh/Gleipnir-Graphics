#version 330

uniform mat4 viewProjectionMatrix;
uniform mat4 modelMatrix;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;

out vec2 uv;

void main() {
    gl_Position = viewProjectionMatrix * modelMatrix * vec4(position, 1.0);
    uv = texCoord;
}