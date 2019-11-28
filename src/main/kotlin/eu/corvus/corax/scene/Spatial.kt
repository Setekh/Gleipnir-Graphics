
package eu.corvus.corax.scene

import org.joml.Matrix4f
import org.joml.Matrix4fc

/**
 * Class that represent a positional space
 * It can handle:
 *  - transforms
 *  - pre-render queue
 *
 * @author Vlad Ravenholm on 11/24/2019
 */
open class Spatial(name: String = "Spatial") : Node(name) {
    val transform: Matrix4fc = Matrix4f()


}