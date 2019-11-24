package eu.corvus.corax.scene

import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.scene.geometry.Mesh

/**
 * This class:
 *  - is not rendable or spatial
 *  - is supposed to be a collection
 *  - is supposed to find/hold paths
 *
 * @author Vlad Ravenholm on 11/24/2019
 **/
open class Node(var name: String = "Node"): Object() {
    val children: List<Node> = mutableListOf()
    private val mChildren = children as MutableList

    fun addChild(child: Node) {
        mChildren.add(child)
    }

    fun removeChild(child: Node) {
        mChildren.remove(child)
    }
}
