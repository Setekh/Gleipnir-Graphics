#version 330

out vec4 fragColor;
in vec3 pos;

void main() {
    //fragColor = vec4(pos + .5 * 0.5, 1.0);
    fragColor = vec4(vec3(0.5f) + .5 * 0.5, 1.0);
}