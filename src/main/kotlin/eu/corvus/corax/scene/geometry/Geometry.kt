package eu.corvus.corax.scene.geometry

import eu.corvus.corax.graphics.buffers.VertexArrayObject
import eu.corvus.corax.scene.Spatial

/**
 * Responsible for:
 *  - shadow props
 *  - culling
 *
 * Should be implemented by rendable entities
 *
 * @author Vlad Ravenholm on 11/24/2019
 */
abstract class Geometry(name: String) : Spatial(name) {
    abstract var vertexArrayObject: VertexArrayObject?
        protected set
}