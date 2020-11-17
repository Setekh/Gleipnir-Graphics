package eu.corvus.corax.graphics.material.shaders

class MatcapShader : Shader() {
    val viewProjection = Mat4fUniform("viewProjectionMatrix")
    val normalMatrix = Mat4fUniform("normalMatrix")
    val modelMatrix = Mat4fUniform("modelMatrix")
    val eye = Vec3fUniform("eye")
    val texture = IntUniform("m_texture")

    override val uniforms = arrayOf(viewProjection, modelMatrix, normalMatrix, eye, texture)

    override val vertexResource: String = "shaders/matcap/vertex.glsl"
    override val fragmentResource: String = "shaders/matcap/fragment.glsl"

}