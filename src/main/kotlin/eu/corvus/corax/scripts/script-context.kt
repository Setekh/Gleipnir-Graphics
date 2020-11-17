package eu.corvus.corax.scripts

import eu.corvus.corax.scene.Object
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class ScriptContext<T: Object>: Script<T>() {
    val scope = MainScope()

    private var ready: () -> Unit = {}
    private var update: (Float) -> Unit = {}
    private var destroy: () -> Unit = {}

    fun onReady(ready: () -> Unit) {
        this.ready = ready
    }

    fun onUpdate(update: (Float) -> Unit) {
        this.update = update
    }

    fun onDestroy(destroy: () -> Unit) {
        this.destroy = destroy
    }

    override fun onReady() = ready.invoke()
    override fun onUpdate(tpf: Float) = update.invoke(tpf)
    override fun onDestroy() {
        scope.cancel()
        destroy.invoke()
    }
}

fun <T: Object> script(body: ScriptContext<T>.() -> Unit): Script<*> =
        ScriptContext<T>().apply(body)