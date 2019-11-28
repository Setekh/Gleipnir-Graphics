
package eu.corvus.corax.scene

import eu.corvus.corax.graphics.Transform
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector3f

/**
 * Class that represent a positional space
 * It can handle:
 *  - transforms
 *  - pre-render queue
 *
 * @author Vlad Ravenholm on 11/24/2019
 */
open class Spatial(name: String = "Spatial") : Node(name) {
    val transform: Transform = Transform()
    val worldTransform: Transform = Transform()

    private val worldMatrix = Matrix4f()

    fun getWorldMatrix(): Matrix4f { //TODO cache this with the parent transforms
        return worldMatrix.identity()
            .translate(worldTransform.translation)
            .rotate(worldTransform.rotation)
            .scale(worldTransform.scale)
    }
}