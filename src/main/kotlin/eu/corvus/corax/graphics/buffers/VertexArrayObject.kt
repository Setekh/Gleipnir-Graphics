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
package eu.corvus.corax.graphics.buffers

import eu.corvus.corax.graphics.buffers.types.*

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
class VertexArrayObject(
    val vertexBuffers: Array<VertexBufferObject?> = arrayOfNulls(BufferType.values().size)
): BufferObject() {
    override var id: Int = 0

    var size: Int = 0
        private set

    override fun onAssign(bufferId: Int) {
        id = bufferId

        val indexBuffer = vertexBuffers[BufferType.Indices.ordinal] as IndexBuffer
        size = indexBuffer.size
    }

    fun addIndexBuffer(indices: IntArray) {
        val indexBuffer = IndexBuffer(indices.size)
        indexBuffer.data()!!.put(indices).flip()
        addBuffer(indexBuffer)
    }

    fun addVertexBuffer(vertexArray: FloatArray) {
        val vertexBuffer = VertexBuffer(vertexArray.size)
        vertexBuffer.data()!!.put(vertexArray).flip()
        addBuffer(vertexBuffer)
    }

    fun addTextureCoordsBuffer(texCoords: FloatArray) {
        addBuffer(TextureCoordsBuffer(texCoords.size).apply { data()!!.put(texCoords).flip() })
    }

    fun addNormalsBuffer(normals: FloatArray) {
        addBuffer(NormalBuffer(normals.size).apply { data()!!.put(normals).flip() })
    }

    fun addTangentsBuffer(tangents: FloatArray) {
        addBuffer(NormalBuffer(tangents.size).apply { data()!!.put(tangents).flip() })
    }

    fun addBiTangentsBuffer(biTangents: FloatArray) {
        addBuffer(NormalBuffer(biTangents.size).apply { data()!!.put(biTangents).flip() })
    }

    private fun addBuffer(vertexBuffer: VertexBufferObject) {
        vertexBuffers[vertexBuffer.type.ordinal] = vertexBuffer
    }

    override fun free() {
        super.free()
        vertexBuffers.forEach { it?.free() }
    }
}