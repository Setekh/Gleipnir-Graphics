package eu.corvus.corax.graphics.material.shaders

class UnshadedShader : Shader() {
    val viewProjection = Mat4fUniform("viewProjectionMatrix")
    val modelMatrix = Mat4fUniform("modelMatrix")

    val color = Vec4fUniform("color")
    val texture = IntUniform("albedoTexture")

    override val uniforms = arrayOf(viewProjection, modelMatrix, color, texture)

    override val vertexResource: String = "shaders/unshaded/vertex.glsl"
    override val fragmentResource: String = "shaders/unshaded/fragment.glsl"

}