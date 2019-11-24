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

import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.scene.geometry.Mesh
import org.lwjgl.opengl.GL30.*

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
class DummyRenderer : Renderer {
    private val geoms = arrayListOf<Geometry>()

    override fun onCreate() {
        val vertices = floatArrayOf(
            // Left bottom triangle
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,

            // Right top triangle
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, 0.5f, 0f
        )

        geoms.add(Mesh("Quad").createSimple(vertices, IntArray(0), FloatArray(0)))

        glEnable(GL_DEPTH_TEST)

        // Set the clear color
        glClearColor(0.13f, 0.13f, 0.13f, 0.13f)
    }

    override fun onPreRender() {
        val removedSome = geoms.removeAll { it !is Mesh || it.glObject == null }

        if (removedSome)
            println("Removed some dangling geometries!")
    }

    override fun onRender() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        geoms.forEach {
            it.render()
        }
    }

    override fun onDestroy() {
        geoms.filterIsInstance<Mesh>().forEach { it.glObject?.free() }
    }

}