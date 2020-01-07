package eu.corvus.corax.graphics.material.shaders

import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.graphics.material.Shader

class MatcapShader(renderContext: RendererContext) : Shader(renderContext) {
    val viewProjection = Mat4fUniform("viewProjection")
    val viewMatrix = Mat4fUniform("viewMatrix")
    val modelMatrix = Mat4fUniform("modelMatrix")

    override val uniforms = arrayOf(viewProjection, viewMatrix, modelMatrix)

    override val vertexSource: String = "shaders/matcap/vertex.glsl"
    override val fragmentSource: String = "shaders/matcap/fragment.glsl"

}