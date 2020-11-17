#version 330

uniform mat4 viewProjectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 normalMatrix;

uniform vec3 eye;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 tCoord;
layout (location = 2) in vec3 normal;

out vec3 norm;
out vec3 peye;
out vec3 pos;

void main() {
    gl_Position = viewProjectionMatrix * modelMatrix * vec4(position, 1.0);

    peye = eye;

    norm = normalize((normalMatrix * vec4(normal, 0.0)).xyz);
}