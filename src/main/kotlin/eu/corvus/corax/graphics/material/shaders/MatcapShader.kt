package eu.corvus.corax.graphics.material.shaders

class MatcapShader : Shader() {
    val viewProjection = Mat4fUniform("viewProjection")
    val viewMatrix = Mat4fUniform("viewMatrix")
    val modelMatrix = Mat4fUniform("modelMatrix")

    override val uniforms = arrayOf(viewProjection, viewMatrix, modelMatrix)

    override val vertexSource: String = "shaders/matcap/vertex.glsl"
    override val fragmentSource: String = "shaders/matcap/fragment.glsl"

}