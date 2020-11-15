package eu.corvus.corax.graphics

import eu.corvus.corax.scene.Camera
import eu.corvus.corax.scene.geometry.Geometry
import eu.corvus.corax.utils.ItemBuffer

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
interface Renderer {
    fun onCreate()

    fun onDestroy()

    fun onResize(width: Int, height: Int) {}

    fun render(camera: Camera, renderBuffer: ItemBuffer<Geometry>)
}