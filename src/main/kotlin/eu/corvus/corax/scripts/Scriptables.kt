package eu.corvus.corax.scripts

interface Scriptable
interface SceneGraphScript : Scriptable

/**
 * Main use is for scene graph scriptables
 *
 * They are pretty dirty, but it's intended for development purposes
 */
fun interface Update: SceneGraphScript {
    fun invoke(tpf: Float)
}

/**
 * Main use is for scene graph scriptables
 *
 * They are pretty dirty, but it's intended for development purposes
 */
fun interface Ready: SceneGraphScript {
    fun invoke()
}



