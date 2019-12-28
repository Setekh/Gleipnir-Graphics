package eu.corvus.corax.scene.geometry

import eu.corvus.corax.graphics.buffers.VertexArrayObject

/**
 * @author Vlad Ravenholm on 11/24/2019
 *
 * @TODO set the Type { Static, Dynamic, Stream } based on scripts & other hints like controls
 * Should contain bound information
 *
 */
class Mesh(name: String = "Mesh") : Geometry(name) {
    override var vertexArrayObject: VertexArrayObject? = null

    fun createSimple(vertexArray: FloatArray, indices: IntArray, texCoords: FloatArray? = null): Mesh {
        vertexArrayObject = VertexArrayObject().apply {
            addIndexBuffer(indices)
            addVertexBuffer(vertexArray)

            if (texCoords != null)
                addTextureCoords(texCoords)
        }

        return this
    }
}
