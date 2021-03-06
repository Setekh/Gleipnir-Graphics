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
package eu.corvus.corax.app

import eu.corvus.corax.graphics.Renderer
import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.scene.graph.SceneGraph
import eu.corvus.corax.utils.Logger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import java.util.*

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
abstract class GleipnirApplication(
    val title: String
): KoinComponent {
    protected val appScope = MainScope()

    var hardwareMouse = HardwareMouseState.Normal
        set(value) {
            field = value
            onChangeHardwareMouseState(value)
        }

    enum class HardwareMouseState {
        Normal, Hidden, Disabled
    }

    val renderer: Renderer by inject()
    val rendererContext: RendererContext by inject()
    val sceneGraph: SceneGraph by inject()

    val timer: Timer by inject()

    private val enqueuedTasks = LinkedList<Runnable>()

    var width: Int = 640
        private set
    var height: Int = 480
        private set

    var speed = 1f
    var paused = false

    private var nextNotification: Long = 0

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height

        onResize(width, height)
    }

    open fun onResize(width: Int, height: Int) {
        sceneGraph.resizeViewPort(width, height)
        renderer.onResize(width, height)
    }

    abstract fun onCreate()

    open fun onReady() {}

    abstract fun live()

    fun update() {
        if (nextNotification < timer.timeInSeconds) {
            nextNotification = timer.timeInSeconds + 1
            Logger.info("FPS: ${timer.framePerSecond}")
        }

        processEnqueuedTasks()


        if(speed == 0f || paused)
            return

        timer.tick()

        val tpf = timer.timePerFrame * speed
        onUpdate(tpf)
    }

    private fun processEnqueuedTasks() {
        while (enqueuedTasks.isNotEmpty()) {
            val task = enqueuedTasks.pop()
            val result = kotlin.runCatching { task.run() }

            if (result.isFailure) {
                Logger.error(result.exceptionOrNull(), "Failed enqueued task!")
            }
        }
    }

    open fun onUpdate(tpf: Float) {
        rendererContext.viewPort(0, 0, width, height)
        rendererContext.clear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT or GL11.GL_STENCIL_BUFFER_BIT)

        repeat(sceneGraph.cameras.size) {
            val camera = sceneGraph.cameras[it]
            sceneGraph.prepareGraph(camera, tpf)

            renderer.render(camera, sceneGraph.renderBuffer)
        }
    }

    abstract fun onDestroy()

    fun startLifeCycle() {
        onCreate()
        try {
            renderer.onCreate()
            onReady()
            live()
        } catch (e: Exception) {
            Logger.error(e, "Fatal crash!")
        } finally {
            onDestroy()
            appScope.cancel()
        }
    }

    fun dispatch(block: Runnable) {
        enqueuedTasks.addLast(block)
    }

    open fun onChangeHardwareMouseState(mouseState: HardwareMouseState) {}
}