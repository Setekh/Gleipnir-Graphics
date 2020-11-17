package eu.corvus.corax.scene

import eu.corvus.corax.scene.pool.ObjectPool
import eu.corvus.corax.scripts.Script
import eu.corvus.corax.utils.Disposable
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
open class Object: KoinComponent, Disposable {
    private val objectPool by inject<ObjectPool>() // TODO mby use an objectId
    private var isDestroyed = false

    open var script: Script<*>? = null
        set(value) {
            field = value
            value?.assign(this)
        }

    override fun free() {
        if (isDestroyed) return

        isDestroyed = true
        script?.onDestroy()
        objectPool.free(this)
    }


    protected fun finalize() {
        free()
    }
}