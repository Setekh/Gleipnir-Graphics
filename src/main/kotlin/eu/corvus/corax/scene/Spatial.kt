
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

    var alwaysCompute = false

    val worldMatrix = Matrix4f()

    private var shouldCompute = true

    fun forceUpdate() {
        children.forEach { (it as? Spatial)?.forceUpdate() }
        shouldCompute = true
    }

    protected open fun computeWorldTransform() {
        val parent = this.parent
        worldTransform.set(transform)

        if (parent is Spatial) { // For each child, set the update flag to shouldUpdate = true
            worldTransform.mergeParentTransform(parent.worldTransform)
        }

        worldMatrix.identity()
            .translate(worldTransform.translation)
            .rotate(worldTransform.rotation)
            .scale(worldTransform.scale)

        shouldCompute = false
    }

    open fun onUpdate(tpf: Float): Boolean = false

    override fun appendChild(child: Node) {
        super.appendChild(child)
        shouldCompute = true
    }

    override fun removeChild(child: Node) {
        super.removeChild(child)
        shouldCompute = true
    }

    fun update(tpf: Float) {
        val shouldUpdate = onUpdate(tpf)
        if (alwaysCompute || shouldCompute || shouldUpdate) {
            computeWorldTransform()
        }
    }
}