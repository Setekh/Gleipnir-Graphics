package eu.corvus.corax.scene.geometry

import eu.corvus.corax.graphics.ShaderProgram
import eu.corvus.corax.scene.geometry.buffers.BufferType
import eu.corvus.corax.scene.geometry.buffers.VertexArrayObject
import eu.corvus.corax.scene.geometry.buffers.VertexBufferObject
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil

/**
 * @author Vlad Ravenholm on 11/24/2019
 *
 * @TODO set the Type { Static, Dynamic, Stream } based on scripts & other hints like controls
 * Should contain bound information
 *
 */
class Mesh(name: String = "Mesh"): Geometry(name) {
    var glObject: VertexArrayObject? = null
        private set

    var vertexCount: Int = 0
        private set

    fun createSimple(vertexArray: FloatArray, indices: IntArray, textCoord: FloatArray = floatArrayOf(0f, 1f)): Mesh {
        vertexCount = indices.size

        val vao = VertexArrayObject()
        vao.createBuffers {
            val buffer = MemoryUtil.memAllocFloat(vertexArray.size)
            buffer.put(vertexArray).flip()
            put(BufferType.Vertex, VertexBufferObject(buffer, BufferType.Vertex))

            val indexBuffer = MemoryUtil.memAllocInt(indices.size)
            indexBuffer.put(indices).flip()
            put(BufferType.Indices, VertexBufferObject(indexBuffer, BufferType.Indices))
        }
        glObject = vao

        return this
    }

    override fun render() {
        val vao = glObject!!

        glBindVertexArray(vao.id)

        // Render the vertex buffer
        glEnableVertexAttribArray(0)
        //glEnableVertexAttribArray(1)
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)

        glDisableVertexAttribArray(0)
        //glDisableVertexAttribArray(1)

        glBindVertexArray(0)
    }
}