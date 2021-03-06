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
package eu.corvus.corax.platforms.desktop

import eu.corvus.corax.app.GleipnirApplication
import eu.corvus.corax.app.Input
import eu.corvus.corax.app.InputEvent
import eu.corvus.corax.scene.assets.AssetManager
import eu.corvus.corax.scripts.ScriptManager
import eu.corvus.corax.utils.Logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.get
import org.koin.core.inject
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.GL_TRUE
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.Platform
import java.io.File

/**
 * GLFW app for desktop uses
 */
class DesktopApp(
        title: String = "App Window",
        private val input: Input
) : GleipnirApplication(title) {
    // The window handle
    private var window: Long = 0

    init {
        get<ScriptManager>() // warmup scripting
        Logger.info("LWJGL ${Version.getVersion()} GLFW ${glfwGetVersionString()}!")
    }

    override fun onCreate() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3) // favor OGL3 on desktop

        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        }

        // Samples
        //glfwWindowHint(GLFW_SAMPLES, 16)

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL)
        if (window == NULL)
            throw RuntimeException("Failed to create the GLFW window")

        glfwSetJoystickCallback { jid, event -> Logger.info("jid = [%s], event = [%s]", jid, event) }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop

            appScope.launch {
                input.keyPress(key, when (action) {
                    GLFW_PRESS -> InputEvent.Pressed
                    GLFW_RELEASE -> InputEvent.Released
                    GLFW_REPEAT -> InputEvent.Repeat
                    else -> error("Unk action! $action")
                })
            }
        }

        glfwSetMouseButtonCallback(window) { window, button, action, mods ->
            appScope.launch {
                input.mousePress(button, if (action > 0) InputEvent.Pressed else InputEvent.Released)
            }
        }

        glfwSetCursorPosCallback(window) { window, xpos, ypos ->
            appScope.launch {
                input.mouseMotion(width, height, xpos.toFloat(), ypos.toFloat())
            }
        }

        glfwSetFramebufferSizeCallback(window) { window: Long, width: Int, height: Int ->
            resize(width, height)
        }

        // Get the thread stack and push a new frame
        stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode!!.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            )
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        resizeWithFrameBuffer()
    }

    private fun resizeWithFrameBuffer() {
        stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            glfwGetFramebufferSize(window, pWidth, pHeight)

            resize(pWidth.get(), pHeight.get())
        }
    }

    override fun onReady() {
        appScope.launch {
            val scriptManager by inject<ScriptManager>()
            scriptManager.loadScript("scripts/start-script.kts")
        }

        // TODO config this
        //glEnable(GL_MULTISAMPLE)
    }

    override fun live() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            input.clear()
            update()

            glfwSwapBuffers(window) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()
        }
    }

    override fun onChangeHardwareMouseState(mouseState: HardwareMouseState) {
        when (mouseState) {
            HardwareMouseState.Normal -> glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
            HardwareMouseState.Hidden -> glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)
            HardwareMouseState.Disabled -> glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        }
    }

    override fun onDestroy() {

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }
}