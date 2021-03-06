#version 330

out vec4 fragColor;
in vec3 norm;
in vec3 peye;

uniform sampler2D m_texture;

vec2 matcap(vec3 eye, vec3 normal) {
    vec3 reflected = reflect(eye, normal);
    float m = 2.8284271247461903 * sqrt( reflected.z+1.0 );
    return reflected.xy / m + 0.5;
}
void main() {
    vec2 uv = matcap(peye, norm);
    vec3 color = texture(m_texture, uv).rgb;
    fragColor = vec4(color, 1.0);
}