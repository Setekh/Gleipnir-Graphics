package eu.corvus.corax.platforms.desktop.scripts

import eu.corvus.corax.scene.Object
import eu.corvus.corax.scene.assets.AssetManager
import eu.corvus.corax.scripts.*
import eu.corvus.corax.utils.Logger
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import kotlin.reflect.KClass

/**
 * @author Vlad Cazacu
 */
class KtScriptManager(val assetManager: AssetManager): ScriptManager {
    private var scriptEngine: ScriptEngine
    private val scriptables = mutableListOf<Scriptable>()

    private val scripts = ConcurrentHashMap<String, Script<*>>()

    init {
        val t1 = System.currentTimeMillis()

        setIdeaIoUseFallback()

        val manager = ScriptEngineManager()
        scriptEngine = manager.getEngineByExtension("kts")

        runBlocking { loadScript("scripts/init.kts") }
        Logger.info("Initialized scripting manager in ${System.currentTimeMillis() - t1} ms")
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun loadScript(path: String): Any? {
        val bytes = assetManager.loadRaw(path)

        val t1 = System.currentTimeMillis()
        val result = scriptEngine.eval(String(bytes, Charset.defaultCharset()))
        Logger.info("Loaded script[$path] in ${System.currentTimeMillis() - t1} ms")

        if (result is Scriptable) {
            scriptables.add(result)
        }

        val scriptables = result as? List<Scriptable>
        if (scriptables != null) {
            this.scriptables.addAll(scriptables)
        }

        return result
    }

    override fun onGraphReady() {
        scriptables.forEach {
            if (it is Ready) it.invoke()
        }
    }

    override fun onGraphUpdate(tpf: Float) {
        scriptables.forEach {
            if (it is Update) it.invoke(tpf)
        }
    }

    override fun onGraphDestroy() {
        scriptables.clear()
    }
}