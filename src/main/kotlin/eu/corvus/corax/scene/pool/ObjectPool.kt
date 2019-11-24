package eu.corvus.corax.scene.pool

import eu.corvus.corax.scene.Object

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
interface ObjectPool {
    fun create()
    fun free(obj: Object) // is Geometry && find disposaable data like Mesh
}