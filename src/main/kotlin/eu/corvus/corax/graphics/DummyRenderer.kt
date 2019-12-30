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

import eu.corvus.corax.app.Device
import eu.corvus.corax.app.Input
import eu.corvus.corax.app.KeyEvent
import eu.corvus.corax.graphics.buffers.isUploaded
import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.scene.Camera
import eu.corvus.corax.scene.assets.AssetManager
import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.scene.geometry.Mesh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joml.Math.toRadians
import org.joml.Vector3f
import org.koin.core.context.GlobalContext
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL30.*

/**
 * @author Vlad Ravenholm on 11/24/2019
 * Badly representing a desktop gl renderer
 */
class DummyRenderer(
    private val rendererContext: RendererContext,
    private val input: Input
) : Renderer {
    private var width: Int = 300
    private var height: Int = 300

    val geoms = arrayListOf<Geometry>()
    private val camera: Camera = Camera()
    private val viewPortColor = Color.of(0.13f, 0.13f, 0.13f)

    private val speed = 6f

    private var forward = false
    private var backward = false
    private var left = false
    private var right = false

    val shader: ShaderProgram by lazy {
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(
            Mesh::class.java.getResourceAsStream("/vertex.glsl").readBytes().toString(
                Charsets.UTF_8
            )
        )

        shaderProgram.createFragmentShader(
            Mesh::class.java.getResourceAsStream("/fragment.glsl").readBytes().toString(
                Charsets.UTF_8
            )
        )

        shaderProgram.link()
        shaderProgram
    }

    override fun onCreate() {
        val vertices = floatArrayOf(
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f
        )

        val indeces = intArrayOf(0, 1, 3, 3, 1, 2)

        //camera.transform.rotation.rotateX(toRadians(-90.0).toFloat())
        camera.transform.translation.set(0f, 0f, -3f)

        val mesh: Mesh
        geoms.add(Mesh("Quad").createSimple(vertices, indeces).apply {
            mesh = this
            transform.rotation.rotationY(toRadians(60.0).toFloat())
            transform.rotation.rotateX(toRadians(-90.0).toFloat())

            appendChild(Mesh("Quad-2").createSimple(vertices, indeces).also { it ->
                geoms.add(it)
                it.transform.translation.x = -1.2f
                it.transform.translation.z = 3.2f
            })
        })

        val koin = GlobalContext.get().koin
        val assetManager = koin.get<AssetManager>()

        GlobalScope.launch {
            val spatial = assetManager.loadSpatial("test-models//suz.dae")
            withContext(Dispatchers.Default) {
                geoms.add(spatial.children.first() as Geometry)
            }
        }

        geoms.reverse()

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

        input.map(Device.Keyboard, GLFW.GLFW_KEY_LEFT, "rotate-") { _, status ->
            if (status == KeyEvent.Released) {
                mesh.transform.rotation.rotateZ(toRadians(-10.0).toFloat())
                mesh.forceUpdate()
            }
        }

        input.map(Device.Keyboard, GLFW.GLFW_KEY_RIGHT, "rotate+") { _, status ->
            if (status == KeyEvent.Released) {
                mesh.transform.rotation.rotateZ(toRadians(10.0).toFloat())
                mesh.forceUpdate()
            }
        }


        input.map(Device.Keyboard, GLFW.GLFW_KEY_W, "forward") { _, status ->
            forward = status == KeyEvent.Pressed
        }

        input.map(Device.Keyboard, GLFW.GLFW_KEY_S, "forward-") { _, status ->
            backward = status == KeyEvent.Pressed
        }

        input.map(Device.Keyboard, GLFW.GLFW_KEY_A, "left-s") { _, status ->
            left = status == KeyEvent.Pressed
        }

        input.map(Device.Keyboard, GLFW.GLFW_KEY_D, "left-s-") { _, status ->
            right = status == KeyEvent.Pressed
        }
    }

    override fun onResize(width: Int, height: Int) {
        this.width = width
        this.height = height

        val aspectRatio = width.toFloat() / height.toFloat()
        camera.useAsProjection(toRadians(70.0), aspectRatio)
    }

    override fun onPreRender(tpf: Float) {
        val direction = Vector3f()

        val isMoving = forward || backward || left || right

        val viewMatrix = camera.viewMatrix
        if (forward) direction.add(viewMatrix.getColumn(2, Vector3f()))
        if (backward) direction.add(viewMatrix.getColumn(2, Vector3f()).negate())
        if (left) direction.add(viewMatrix.getColumn(0, Vector3f()))
        if (right) direction.add(viewMatrix.getColumn(0, Vector3f()).negate())

        if (isMoving) {
            camera.transform.translation.add(direction.mul(speed * tpf))
            camera.forceUpdate()
        }

        geoms.forEach {
            it.vertexArrayObject?.let { vao ->
                if (!vao.isUploaded())
                    rendererContext.createArrayBufferData(vao)
            }
            it.update(tpf)
        }
        camera.computeMatrices()
    }

    override fun onRender() {
        rendererContext.viewPort(0, 0, width, height)
        rendererContext.clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        shader.bind() // This should be in a material?

        repeat(geoms.size) { index ->
            val geometry = geoms[index]
            val vertexArrayObject = geometry.vertexArrayObject ?: return@repeat

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
        geoms.filterIsInstance<Mesh>().forEach { it.vertexArrayObject?.free() }
    }
}