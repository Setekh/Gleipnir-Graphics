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
    val children: List<Node> = arrayListOf()

    var parent: Node? = null

    open fun appendChild(child: Node) {
        children as MutableList
        children.add(child)
        child.parent = this
    }

    open fun removeChild(child: Node) {
        children as MutableList
        children.remove(child)
        child.parent = null
    }

    open fun child(index: Int): Node {
        return children[index]
    }

    fun removeChildren() {
        children as MutableList
        children.clear()
    }
}
