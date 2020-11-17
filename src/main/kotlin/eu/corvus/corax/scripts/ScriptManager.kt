package eu.corvus.corax.scripts

interface ScriptManager {
    suspend fun loadScript(path: String): Any?

    fun onGraphReady()
    fun onGraphUpdate(tpf: Float)
    fun onGraphDestroy()
}

/*
object ScriptContext : KoinComponent {
    val scriptManager by inject<ScriptManager>()

    // TODO We can also have here utils like main scope to launch jobs

}

fun scriptContext(body: ScriptContext.() -> Unit) = body(ScriptContext)*/
