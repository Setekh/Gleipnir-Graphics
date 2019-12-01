#version 330

out vec4 fragColor;
in vec3 pos;

uniform float inf;

void main() {
    fragColor = vec4(1f * inf);
}