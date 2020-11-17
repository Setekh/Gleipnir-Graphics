package eu.corvus.corax.scripts

interface Scriptable

/**
 * Main use is for scene graph scriptables
 *
 * They are pretty dirty, but it's intended for development purposes
 */
fun interface Update: Scriptable {
    fun invoke(tpf: Float)
}

/**
 * Main use is for scene graph scriptables
 *
 * They are pretty dirty, but it's intended for development purposes
 */
fun interface Ready: Scriptable {
    fun invoke()
}



