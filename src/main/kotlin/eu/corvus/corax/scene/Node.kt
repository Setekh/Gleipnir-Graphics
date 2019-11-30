package eu.corvus.corax.scene

/**
 * This class:
 *  - is not rendable or spatial
 *  - is supposed to be a collection
 *  - is supposed to find/hold paths
 *
 * @author Vlad Ravenholm on 11/24/2019
 */
open class Node(var name: String = "Node"): Object() {
    val children: List<Node> = mutableListOf()
    private val mChildren = children as MutableList

    var parent: Node? = null

    open fun appendChild(child: Node) {
        mChildren.add(child)
        child.parent = this
    }

    open fun removeChild(child: Node) {
        mChildren.remove(child)
        child.parent = null
    }
}
