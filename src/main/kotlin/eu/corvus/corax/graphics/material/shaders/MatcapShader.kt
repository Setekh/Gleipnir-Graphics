package eu.corvus.corax.graphics.material.shaders

class MatcapShader : Shader() {
    val viewProjection = Mat4fUniform("viewProjectionMatrix")
    val viewMatrix = Mat4fUniform("viewMatrix")
    val modelMatrix = Mat4fUniform("modelMatrix")
    val texture = IntUniform("texture")

    override val uniforms = arrayOf(viewProjection, viewMatrix, modelMatrix, texture)

    override val vertexResource: String = "shaders/matcap/vertex.glsl"
    override val fragmentResource: String = "shaders/matcap/fragment.glsl"

}