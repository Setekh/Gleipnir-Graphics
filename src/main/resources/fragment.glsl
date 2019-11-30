#version 330

out vec4 fragColor;
in vec3 pos;

void main() {
    fragColor = vec4(pos, 1.0);
}