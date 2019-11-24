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
package eu.corvus.corax.scene.geometry.buffers

import eu.corvus.corax.utils.Disposable
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.lang.RuntimeException
import java.nio.Buffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
data class VertexBufferObject(val buffer: Buffer, val type: BufferType) : Disposable {
    val id: Int = glGenBuffers()

    var size: Int = 0
        private set

    fun initialize(free: Boolean = false) { // we may want to not free it, to make collisions shapes later
        // Idea: Free it on render in release, and in a debug/editing flag the buffer will persist, also free it if the scene is unloaded
        when (type) {
            BufferType.Vertex -> {
                glBindBuffer(GL_ARRAY_BUFFER, id)
                glBufferData(GL_ARRAY_BUFFER, buffer as FloatBuffer, GL_STATIC_DRAW)
                glVertexAttribPointer(0, type.size, GL_FLOAT, false, 0, 0)
                glBindBuffer(GL_ARRAY_BUFFER, 0)
            }
            BufferType.Indices -> {
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer as IntBuffer, GL_STATIC_DRAW)
            }
            else -> throw RuntimeException("No such vbo type! ${buffer.javaClass}")
        }

        size = buffer.limit()
        if (free)
            MemoryUtil.memFree(buffer)
    }

    override fun free() {
        glDeleteBuffers(id)
        MemoryUtil.memFree(buffer)
    }
}