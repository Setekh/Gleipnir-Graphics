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

import eu.corvus.corax.scene.Camera
import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.scene.geometry.Mesh
import org.joml.Math
import org.joml.Matrix4f
import org.lwjgl.opengl.GL30.*

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
class DummyRenderer : Renderer {
    private var width: Int = 300
    private var height: Int = 300

    val geoms = arrayListOf<Geometry>()
    private val camera: Camera = Camera()

    private val worldMatrix = Matrix4f()

    val shader: ShaderProgram by lazy {
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(Mesh::class.java.getResourceAsStream("/vertex.glsl").readBytes().toString(
            Charsets.UTF_8))

        shaderProgram.createFragmentShader(Mesh::class.java.getResourceAsStream("/fragment.glsl").readBytes().toString(
            Charsets.UTF_8))

        shaderProgram.link()
        shaderProgram
    }

    override fun onCreate() {
        val vertices = floatArrayOf(
            -0.5f,  0.5f, -1.05f,
            -0.5f, -0.5f, -1.05f,
            0.5f, -0.5f, -1.05f,
            0.5f,  0.5f, -1.05f
        )


        val indeces = intArrayOf(0, 1, 3, 3, 1, 2)

        geoms.add(Mesh("Quad").createSimple(vertices, indeces).apply { transform.translation.z = -2f })


        glEnable(GL_DEPTH_TEST)
        // Set the clear color
        glClearColor(0.13f, 0.13f, 0.13f, 0.13f)

        shader.createUniform("worldViewProjectionMatrix")
    }

    override fun onResize(width: Int, height: Int) {
        this.width = width
        this.height = height

        val aspectRatio = width.toFloat() / height.toFloat()
        camera.useAsProjection(Math.toRadians(60.0), aspectRatio)
    }

    override fun onPreRender() {

        val removedSome = geoms.removeAll { it !is Mesh || it.glObject == null }

        if (removedSome)
            println("Removed some dangling geometries!")

        geoms.forEach { it.update(0f) }
        camera.computeMatrices()
    }

    override fun onRender() {
        glViewport(0, 0, width, height)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        shader.bind() // This should be an instruction for the renderer

        geoms.forEach {
            shader.setUniform("worldViewProjectionMatrix", worldMatrix.set(camera.viewProjectionMatrix).mul(it.worldMatrix))
            it.render()
        }

        shader.unbind()
    }

    override fun onDestroy() {
        geoms.filterIsInstance<Mesh>().forEach { it.glObject?.free() }
    }

}
