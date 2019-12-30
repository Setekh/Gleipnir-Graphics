package eu.corvus.corax.scene.assets

import eu.corvus.corax.app.storage.StorageAccess
import eu.corvus.corax.graphics.textures.Texture
import eu.corvus.corax.scene.Object
import eu.corvus.corax.scene.Spatial
import eu.corvus.corax.scene.assets.loaders.AssimpLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetManagerImpl(
    private val storageAccess: StorageAccess
) : Object(), AssetManager {
    private val loaders = mutableMapOf<String, AssetManager.AssetLoader>()

    init {
        addLoader("*", AssimpLoader())
    }

    override fun addLoader(suffix: String, assetLoader: AssetManager.AssetLoader) {
        loaders[suffix] = assetLoader
    }

    override fun removeLoader(suffix: String) {
        loaders.remove(suffix)
    }

    override suspend fun loadSpatial(assetName: String): Spatial = withContext(Dispatchers.IO) {
        val suffix = assetName.substringAfterLast('.')
        val assetLoader = loaders[suffix] ?: loaders["*"]!!

        assetLoader.load(this@AssetManagerImpl, storageAccess, assetName) as Spatial
    }

    override suspend fun loadTexture(assetName: String): Texture {
        TODO()
    }

    override fun unload(assetName: String) {

    }

    override fun free() {
        super.free()

        loaders.clear()
    }
}
