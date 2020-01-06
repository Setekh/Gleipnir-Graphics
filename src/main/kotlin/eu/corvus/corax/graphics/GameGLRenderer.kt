/**
 * Copyright (c) 2013-2019 Corvus Corax Entertainment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of Corvus Corax Entertainment nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.corvus.corax.graphics

import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.graphics.material.ShaderProgram
import eu.corvus.corax.scene.Camera
import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.scene.geometry.Mesh
import eu.corvus.corax.utils.ItemBuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*

/**
 * @author Vlad Ravenholm on 1/4/2020
 */
class GameGLRenderer(
    private val rendererContext: RendererContext
) : Renderer {
    private val viewPortColor = Color.of(0.13f, 0.13f, 0.13f)

    val shader: ShaderProgram by lazy {
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(
            Mesh::class.java.getResourceAsStream("#version 330\n\nuniform mat4 viewProjectionMatrix;\nuniform mat4 viewMatrix;\nuniform mat4 modelMatrix;\n\nuniform vec3 eye;\n\nlayout (location = 0) in vec3 position;\nlayout (location = 2) in vec3 normal;\n\nout vec3 norm;\nout vec3 peye;\nout vec3 pos;\n\nvoid main() {\n    vec4 modelProj = modelMatrix * vec4(position, 1.0);\n    pos = modelProj.xyz;\n\n    norm = normalize((modelMatrix * vec4(normal, 1.0)).xyz);\n    norm = mat3(viewMatrix) * norm;\n\n    peye = normalize(modelProj.xyz - eye);\n    peye = mat3(viewMatrix) * peye;\n\n    gl_Position = viewProjectionMatrix * modelProj;\n}").readBytes().toString(
                Charsets.UTF_8
            )
        )

        shaderProgram.createFragmentShader(
            Mesh::class.java.getResourceAsStream("#version 330\n\nout vec4 fragColor;\nin vec3 pos;\nin vec3 norm;\nin vec3 peye;\n\nuniform float inf;\n\nvec3 matcap(vec3 eye, vec3 normal) {\n    vec3 reflected = reflect(eye, normal);\n    float m = 2.8284271247461903 * sqrt( reflected.z+1.0 );\n    return reflected.xyz / m + 0.5; // before merge into master remove z, i made it vec3 for prezentation\n}\n\nvoid main() {\n    fragColor = vec4(matcap(peye, norm), 1.0);\n}").readBytes().toString(
                Charsets.UTF_8
            )
        )

        shaderProgram.link()
        shaderProgram
    }

    override fun onCreate() {
        // Always on by default
        rendererContext.enable(GL_DEPTH_TEST)

        // Set the clear color
        rendererContext.clearColor(viewPortColor)

        //glEnable(GL_CULL_FACE)
        //glCullFace(GL_BACK)

        shader.createUniform("viewProjectionMatrix")
        shader.createUniform("viewMatrix")
        shader.createUniform("modelMatrix")
        shader.createUniform("eye")
        shader.createUniform("inf")
    }

    override fun render(camera: Camera, renderBuffer: ItemBuffer<Geometry>) {
        rendererContext.viewPort(0, 0, camera.width, camera.height)
        rendererContext.clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL11.GL_STENCIL_BUFFER_BIT)

        shader.bind() // This should be in a material?

        repeat(renderBuffer.limit) {
            val geometry = renderBuffer.get()
            val vertexArrayObject = geometry.vertexArrayObject!!

            shader.setUniform("viewProjectionMatrix", camera.viewProjectionMatrix)
            shader.setUniform("viewMatrix", camera.viewMatrix)
            shader.setUniform("modelMatrix", geometry.worldMatrix)
            shader.setUniform("eye", camera.worldTransform.translation)
            shader.setUniform("inf", if (geometry.name != "Quad") 0.3f else 0.5f)

            rendererContext.bindBufferArray(vertexArrayObject)
            rendererContext.draw(vertexArrayObject)
        }

        shader.unbind()
    }

    override fun onDestroy() {

    }

}