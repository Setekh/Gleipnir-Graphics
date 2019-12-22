package eu.corvus.corax.graphics

/**
 * @author Vlad Ravenholm on 11/24/2019
 */
interface Renderer { // TODO add render state and context
    fun onCreate()

    fun onPreRender(tpf: Float)
    fun onRender()
    fun onDestroy()

    fun onResize(width: Int, height: Int) {}
}