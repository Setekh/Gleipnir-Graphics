package eu.corvus.corax.graphics

import eu.corvus.corax.scene.geometry.Geometry

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
interface Renderer {
    fun onCreate()

    fun onPreRender()
    fun onRender()
    fun onDestroy()
}