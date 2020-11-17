import eu.corvus.corax.scene.Spatial
import eu.corvus.corax.scripts.Script
import org.joml.Math

class SpatialClassScript : Script<Spatial>() {
    private val speed = 30

    override fun onUpdate(tpf: Float) {
        actor.transform.rotation.rotateY(Math.toRadians(speed.toDouble()).toFloat() * tpf)
    }
}

SpatialClassScript()