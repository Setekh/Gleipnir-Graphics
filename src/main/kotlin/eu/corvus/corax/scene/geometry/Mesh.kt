package eu.corvus.corax.scene.geometry

import eu.corvus.corax.graphics.buffers.types.BufferType
import eu.corvus.corax.graphics.buffers.VertexArrayObject
import eu.corvus.corax.graphics.buffers.VertexBufferObject
import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * @author Vlad Ravenholm on 11/24/2019
 *
 * @TODO set the Type { Static, Dynamic, Stream } based on scripts & other hints like controls
 * Should contain bound information
 *
 */
class Mesh(name: String = "Mesh") : Geometry(name) {
    override var vertexArrayObject: VertexArrayObject? = null

    fun createSimple(vertexArray: FloatArray, indices: IntArray, textCoord: FloatArray = floatArrayOf(0f, 1f)): Mesh {
        vertexArrayObject = VertexArrayObject().apply {
            addIndexBuffer(indices)
            addVertexBuffer(vertexArray)
        }

        return this
    }
}
