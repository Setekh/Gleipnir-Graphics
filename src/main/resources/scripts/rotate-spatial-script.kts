import eu.corvus.corax.scene.Spatial
import eu.corvus.corax.scripts.script
import org.joml.Math

script<Spatial> {
    val speed = 30

    onUpdate { tpf ->
        actor.transform.rotation.rotateY(Math.toRadians(speed.toDouble()).toFloat() * tpf)
    }
}

