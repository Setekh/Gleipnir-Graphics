package eu.corvus.corax.scripts

import eu.corvus.corax.scene.Object
import org.koin.core.KoinComponent
import java.lang.RuntimeException

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
abstract class Script<T: Object> : Scriptable, KoinComponent {
    private var _actor: T? = null

    val actor by lazy {
        _actor ?: throw RuntimeException("Script not attached!")
    }

    @Suppress("UNCHECKED_CAST")
    fun assign(t: Object) {
        _actor = t as T
        onReady()
    }

    open fun onReady() {}

    //TODO make input
    //TODO make physicsUpdate

    open fun onUpdate(tpf: Float) {}
    open fun onDestroy() {}

}