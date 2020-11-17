import eu.corvus.corax.Koin
import eu.corvus.corax.app.Device
import eu.corvus.corax.app.Input
import eu.corvus.corax.app.InputEvent
import eu.corvus.corax.scene.Spatial
import eu.corvus.corax.scene.graph.SceneGraph
import eu.corvus.corax.scripts.Ready
import eu.corvus.corax.scripts.Script
import eu.corvus.corax.scripts.ScriptManager
import eu.corvus.corax.scripts.Update
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW


// This is here only till the engine picks up

var moving = Vector2f()
val speed = 20
val amount = 10.0

val sceneGraph by Koin.inject<SceneGraph>()
val input by Koin.inject<Input>()
val scriptManager by Koin.inject<ScriptManager>()

sceneGraph.loadScene("test-models/standford_buns.dae")

val action = { mapping: String, event: InputEvent ->
    val isReleased = event == InputEvent.Released
    when (mapping) {
        "turn left" -> moving.x = if (isReleased) 0f else -1f
        "turn up" -> moving.y =  if (isReleased) 0f else 1f
        "turn right" -> moving.x =  if (isReleased) 0f else 1f
        "turn down" -> moving.y =  if (isReleased) 0f else -1f
    }
}

input.map(Device.Keyboard, GLFW.GLFW_KEY_LEFT, "turn left", action)
input.map(Device.Keyboard, GLFW.GLFW_KEY_UP, "turn up", action)
input.map(Device.Keyboard, GLFW.GLFW_KEY_RIGHT, "turn right", action)
input.map(Device.Keyboard, GLFW.GLFW_KEY_DOWN, "turn down", action)

lateinit var spatial: Spatial

val onReady = Ready {
    spatial = sceneGraph.sceneTree.children[0] as Spatial
    MainScope().launch { // TODO Look into scriptContext DSL
        val script = scriptManager.loadScript("scripts/SpatialScript.kts") as Script<*>
        spatial.script = script
    }
}

val onUpdate = Update { tpf ->
    spatial.transform.rotation.rotateY(Math.toRadians(amount * moving.x.toDouble()).toFloat() * speed * tpf)
    spatial.transform.rotation.rotateX(Math.toRadians(amount * moving.y.toDouble()).toFloat() * speed * tpf)
    spatial.forceUpdate()
}

listOf(onReady, onUpdate)
