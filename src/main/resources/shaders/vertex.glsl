#version 330

uniform mat4 viewProjectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec3 eye;

layout (location = 0) in vec3 position;
layout (location = 2) in vec3 normal;

out vec3 norm;
out vec3 peye;
out vec3 pos;

void main() {
    vec4 modelProj = modelMatrix * vec4(position, 1.0);
    pos = modelProj.xyz;

    norm = normalize((modelMatrix * vec4(normal, 1.0)).xyz);
    norm = mat3(viewMatrix) * norm;

    peye = normalize(modelProj.xyz - eye);
    peye = mat3(viewMatrix) * peye;

    gl_Position = viewProjectionMatrix * modelProj;
}