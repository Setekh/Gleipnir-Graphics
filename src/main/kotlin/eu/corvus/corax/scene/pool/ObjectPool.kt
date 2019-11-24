package eu.corvus.corax.scene.pool

import com.sun.media.jfxmediaimpl.MediaDisposer
import eu.corvus.corax.scene.Object

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
interface ObjectPool {
    fun create()
    fun free(obj: MediaDisposer.Disposable) // is Geometry && find disposaable data like Mesh
}