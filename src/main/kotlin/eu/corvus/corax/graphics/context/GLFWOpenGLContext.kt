package eu.corvus.corax.graphics.context

import eu.corvus.corax.graphics.buffers.BufferObject
import eu.corvus.corax.graphics.buffers.VertexArrayObject
import eu.corvus.corax.graphics.buffers.VertexBufferObject
import eu.corvus.corax.graphics.buffers.data
import eu.corvus.corax.graphics.buffers.types.IndexBuffer
import eu.corvus.corax.graphics.buffers.types.VertexBuffer
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class GLFWOpenGLContext : RendererContext {
    override fun bindBufferArray(vertexArrayObject: VertexArrayObject) {
        glBindVertexArray(vertexArrayObject.id)
    }

    override fun bindBufferObject(vertexBufferObject: VertexBufferObject) {
        val target = targetBufferType(vertexBufferObject)
        glBindBuffer(target, vertexBufferObject.id)
    }

    override fun unbindBufferArray(vertexArrayObject: VertexArrayObject) {
        glBindVertexArray(0)
    }

    override fun unbindBufferObject(vertexBufferObject: VertexBufferObject) {
        val target = targetBufferType(vertexBufferObject)
        glBindBuffer(target, 0)
    }

    override fun createArrayBufferData(vertexArrayObject: VertexArrayObject) {
        val vaoId = glGenVertexArrays()
        vertexArrayObject.onAssign(vaoId)

        bindBufferArray(vertexArrayObject)

        repeat(vertexArrayObject.vertexBuffers.count()) {
            val bufferObject = vertexArrayObject.vertexBuffers[it] ?: return@repeat
            val id = glGenBuffers()

            bufferObject.onAssign(id)
            when (bufferObject) {
                is VertexBuffer -> createVertexBufferData(bufferObject)
                is IndexBuffer -> createIndexBufferData(bufferObject)
            }
            bufferObject.clearData()
        }

        unbindBufferArray(vertexArrayObject)
    }

    private fun createIndexBufferData(bufferObject: IndexBuffer) {
        val data = bufferObject.data()
        bindBufferObject(bufferObject)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data as IntBuffer, GL_STATIC_DRAW)
    }

    private fun createVertexBufferData(bufferObject: VertexBuffer) {
        val data = bufferObject.data()

        bindBufferObject(bufferObject)
        glBufferData(GL_ARRAY_BUFFER, data as FloatBuffer, GL_STATIC_DRAW)
        glVertexAttribPointer(0, bufferObject.type.size, GL_FLOAT, false, 0, 0)
    }

    override fun draw(vertexArrayObject: VertexArrayObject) {
        // Render the vertex buffer
        glEnableVertexAttribArray(0) // TODO this should be in a material state
        //glEnableVertexAttribArray(1)

        glDrawElements(GL_TRIANGLES, vertexArrayObject.vertexSize, GL_UNSIGNED_INT, 0)

        glDisableVertexAttribArray(0)
        //glDisableVertexAttribArray(1)
    }

    override fun free(bufferObject: BufferObject) {
        when (bufferObject) {
            is VertexArrayObject -> glDeleteVertexArrays(bufferObject.id)
            is VertexBufferObject -> glDeleteBuffers(bufferObject.id)
        }
    }

    private fun targetBufferType(vertexBufferObject: VertexBufferObject): Int {
        return when (vertexBufferObject) {
            is VertexBuffer -> GL15.GL_ARRAY_BUFFER
            is IndexBuffer -> GL15.GL_ELEMENT_ARRAY_BUFFER
            else -> GL15.GL_ARRAY_BUFFER
        }
    }
}