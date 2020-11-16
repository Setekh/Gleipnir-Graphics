#version 330

out vec4 fragColor;
in vec2 uv;

#if defined(albedoTexture)
uniform sampler2D albedoTexture;
#endif

uniform vec4 color;

void main() {
    #ifdef albedoTexture
    vec4 albedo = texture(albedoTexture, uv) * color;
    #else
    vec4 albedo = color;
    #endif

    fragColor = albedo;
}