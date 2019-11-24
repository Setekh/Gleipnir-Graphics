package eu.corvus.corax.scene

import eu.corvus.corax.scene.pool.ObjectPool
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
open class Object: KoinComponent {
    private val objectPool by inject<ObjectPool>() // TODO mby use an objectId

    fun initialize() {

    }

    fun destroy() {
        objectPool.free(this)
    }

    final override fun getKoin(): Koin {
        return super.getKoin()
    }
}