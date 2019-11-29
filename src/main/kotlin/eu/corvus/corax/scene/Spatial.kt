
package eu.corvus.corax.scene

import org.joml.Matrix4f

/**
 * Class that represent a positional space
 * It can handle:
 *  - transforms
 *  - pre-render queue
 *
 * @author Vlad Ravenholm on 11/24/2019
 */
open class Spatial(name: String = "Spatial") : Node(name) {
    val transform: Transform =
        Transform()

    val worldTransform: Transform =
        Transform()

    val alwaysCompute = false

    val worldMatrix = Matrix4f()

    private var shouldCompute = true

    private fun computeWorldTransform() {
        //TODO get parent and compute with parent

        worldMatrix.identity()
            .translate(transform.translation)
            .rotate(transform.rotation)
            .scale(transform.scale)

        shouldCompute = false
    }

    open fun onUpdate(tpf: Float): Boolean = false

    fun update(tpf: Float) {
        val shouldUpdate = onUpdate(tpf)
        if (alwaysCompute || shouldCompute || shouldUpdate) {
            computeWorldTransform()
        }
    }
}