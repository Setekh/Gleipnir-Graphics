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

import eu.corvus.corax.app.GleipnirApplication
import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.scene.geometry.Mesh
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
class DummyRenderer : Renderer {

    private var width: Int = 300
    private var height: Int = 300

    private val geoms = arrayListOf<Geometry>()

    override fun onCreate() {
        val vertices = floatArrayOf(
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
        )

        val indeces = intArrayOf(0, 1, 3, 3, 1, 2)

        geoms.add(Mesh("Quad").createSimple(vertices, indeces))

        glEnable(GL_DEPTH_TEST)

        // Set the clear color
        glClearColor(0.13f, 0.13f, 0.13f, 0.13f)
    }

    override fun onResize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun onPreRender() {
        val removedSome = geoms.removeAll { it !is Mesh || it.glObject == null }

        if (removedSome)
            println("Removed some dangling geometries!")
    }

    override fun onRender() {
        glViewport(0, 0, width, height)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        geoms.forEach {
            it.render()
        }
    }

    override fun onDestroy() {
        geoms.filterIsInstance<Mesh>().forEach { it.glObject?.free() }
    }

}