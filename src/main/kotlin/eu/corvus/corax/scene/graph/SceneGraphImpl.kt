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
package eu.corvus.corax.scene.graph

import eu.corvus.corax.app.GleipnirApplication
import eu.corvus.corax.graphics.buffers.isUploaded
import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.scene.*
import eu.corvus.corax.scene.assets.AssetManager
import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.utils.ItemBuffer
import eu.corvus.corax.utils.Logger
import eu.corvus.corax.utils.toRadians
import kotlinx.coroutines.*
import org.koin.core.get

/**
 * @author Vlad Ravenholm on 12/2/2019
 */
class SceneGraphImpl(
    private val rendererContext: RendererContext,
    private val assetManager: AssetManager
) : SceneGraph, Object() {
    override val sceneTree: Node = Node("Scene Tree")
    override val renderBuffer: ItemBuffer<Geometry> = ItemBuffer()

    override var isRenderReady: Boolean = false
        private set

    override val cameras: List<Camera> = mutableListOf()

    private val job = SupervisorJob()
    private val sceneScope = CoroutineScope(Dispatchers.Main + job)
    private var loadJob: Job? = null

    private var lastLoadPath = ""

    override suspend fun processTree() {
        cameras as MutableList

        cameras.clear()
        cameras += sceneTree.findAllCameras()

        if (cameras.isEmpty()) { // add a camera to at least see something
            val app = get<GleipnirApplication>()
            cameras.add(Camera("Din oficiu").apply {
                val width = app.width
                val height = app.height

                useAsProjection(70f.toRadians(), width / height.toFloat())
                updateResize(width, height)

                transform.translation.set(0f, 0f, -5f)
                sceneTree.appendChild(this)
            })
        }
    }

    override fun loadScene(path: String) {
        lastLoadPath = path

        sceneTree.removeChildren()
        loadJob?.cancel()
        loadJob = sceneScope.launch {
            isRenderReady = false

            val spatial = assetManager.loadSpatial(path)
            sceneTree.appendChild(spatial)

            withContext(Dispatchers.IO) {
                processTree()
            }

            isRenderReady = cameras.isNotEmpty()

            if (!isRenderReady)
                Logger.warn("Nothing to render with!")
        }
    }

    override fun resizeViewPort(width: Int, height: Int) {
        cameras.forEach {
            it.updateResize(width, height)
        }
    }

    override fun saveScene(path: String) {
        if (!lastLoadPath.endsWith("coss")) {
            //TODO ask for conversion to corvus serialized scene
            Logger.warn("Please pick where to save")
        }

        TODO("Implement")
    }

    override fun prepareGraph(camera: Camera, tpf: Float) {
        // todo check cull hint for camera
        renderBuffer.clear()

        queueScene(sceneTree, tpf)

        renderBuffer.flip()
    }

    private fun queueScene(scene: Node, tpf: Float) {
        when (scene) {
            is Geometry -> { // TODO queue should implement a sorting method, for translucent and transparent objects
                scene.vertexArrayObject?.let { vao ->
                    if (!vao.isUploaded())
                        rendererContext.createArrayBufferData(vao)
                }

                scene.material.prepareUpload(assetManager, rendererContext)

                renderBuffer.put(scene)
            }
        }

        if (scene is Spatial)
            scene.update(tpf)

        repeat(scene.children.size) {
            val node = scene.child(it)
            queueScene(node, tpf)
        }
    }
}