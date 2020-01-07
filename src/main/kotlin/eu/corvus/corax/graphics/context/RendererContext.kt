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
package eu.corvus.corax.graphics.context

import eu.corvus.corax.graphics.Color
import eu.corvus.corax.graphics.buffers.VertexArrayObject
import eu.corvus.corax.graphics.buffers.VertexBufferObject
import eu.corvus.corax.scene.Object
import org.joml.Vector3fc
import java.nio.FloatBuffer

/**
 * @author Vlad Ravenholm on 12/21/2019
 */
interface RendererContext {

    /**
     * @param x the left viewport coordinate
     * @param y the bottom viewport coordinate
     * @param w the viewport width
     * @param h the viewport height
     *
     * @see <a target="_blank" href="http://docs.gl/gl4/glViewport">Reference Page</a>
     */
    fun viewPort(x: Int, y: Int, w: Int, h: Int)

    /**
     * Sets portions of every pixel in a particular buffer to the same value. The value to which each buffer is cleared depends on the setting of the clear
     * value for that buffer.
     *
     * @param mask Zero or the bitwise OR of one or more values indicating which buffers are to be cleared. One or more of:<br><table><tr><td>{@link GL11C#GL_COLOR_BUFFER_BIT COLOR_BUFFER_BIT}</td><td>{@link GL11C#GL_DEPTH_BUFFER_BIT DEPTH_BUFFER_BIT}</td><td>{@link GL11C#GL_STENCIL_BUFFER_BIT STENCIL_BUFFER_BIT}</td></tr></table>
     *
     * @see <a target="_blank" href="http://docs.gl/gl4/glClear">Reference Page</a>
     */
    fun clear(mask: Int)

    /**
     * Sets the clear value for fixed-point and floating-point color buffers in RGBA mode. The specified components are stored as floating-point values.
     *
     * @see <a target="_blank" href="http://docs.gl/gl4/glClearColor">Reference Page</a>
     */
    fun clearColor(color: Color)

    fun enable(mask: Int)

    fun bindBufferArray(vertexArrayObject: VertexArrayObject)
    fun unbindBufferArray(vertexArrayObject: VertexArrayObject)
    fun unbindBufferObject(vertexBufferObject: VertexBufferObject)
    fun bindBufferObject(vertexBufferObject: VertexBufferObject)
    fun createArrayBufferData(vertexArrayObject: VertexArrayObject)
    fun free(glObject: Object)
    fun draw(vertexArrayObject: VertexArrayObject)

    fun setUniformMatrix4fv(location: Int, transpose: Boolean, fb: FloatBuffer): Unit
    fun setUniform3f(location: Int, value: Vector3fc)
    fun setUniform1f(location: Int, value: Float)
    fun getUniformLocation(programId: Int, name: String): Int
    fun setUniform1i(location: Int, value: Int)
}