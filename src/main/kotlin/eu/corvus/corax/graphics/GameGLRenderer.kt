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
import eu.corvus.corax.graphics.material.Material
import eu.corvus.corax.scene.Camera
import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.utils.ItemBuffer
import eu.corvus.corax.utils.Logger
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*

/**
 * @author Vlad Ravenholm on 1/4/2020
 */
class GameGLRenderer(
    private val rendererContext: RendererContext
) : Renderer {
    private val forceMaterial: Material? = null
    private val viewPortColor = Color.of(0.13f, 0.13f, 0.13f)

    private var currentShaderId = -1

    override fun onCreate() {
        // Always on by default
        rendererContext.enable(GL_DEPTH_TEST)

        // Set the clear color
        rendererContext.clearColor(viewPortColor)

        //glEnable(GL_CULL_FACE)
        //glCullFace(GL_BACK)

    }

    override fun render(camera: Camera, renderBuffer: ItemBuffer<Geometry>) {
        rendererContext.viewPort(0, 0, camera.width, camera.height)
        rendererContext.clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL11.GL_STENCIL_BUFFER_BIT)

        repeat(renderBuffer.limit) {
            val geometry = renderBuffer.get()
            val vertexArrayObject = geometry.vertexArrayObject!!

            val material = forceMaterial ?: geometry.material
            val shader = material.shader
            if (currentShaderId != shader.programId) {
                if (currentShaderId != -1)
                    Logger.debug("Shader switched!")

                currentShaderId = shader.programId
                rendererContext.useProgram(shader)
            }

            material.applyParams(camera, geometry)

            rendererContext.bindBufferArray(vertexArrayObject)
            rendererContext.draw(vertexArrayObject)
        }
    }

    override fun onDestroy() {

    }
}