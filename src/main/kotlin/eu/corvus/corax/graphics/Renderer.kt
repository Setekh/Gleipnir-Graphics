package eu.corvus.corax.graphics

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
interface Renderer {
    fun onCreate()

    fun onPreRender()
    fun onRender()
    fun onDestroy()

    fun onResize(width: Int, height: Int) {}
}