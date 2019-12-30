#version 330

out vec4 fragColor;
in vec3 pos;
in vec3 norm;
in vec3 peye;

uniform float inf;

vec3 matcap(vec3 eye, vec3 normal) {
    vec3 reflected = reflect(eye, normal);
    float m = 2.8284271247461903 * sqrt( reflected.z+1.0 );
    return reflected.xyz / m + 0.5; // before merge into master remove z, i made it vec3 for prezentation
}

void main() {
    fragColor = vec4(matcap(peye, norm), 1.0);
}