package eu.corvus.corax.scene.assets

import eu.corvus.corax.app.storage.StorageAccess
import eu.corvus.corax.graphics.material.textures.Texture
import eu.corvus.corax.graphics.material.textures.Texture2D_PC
import eu.corvus.corax.scene.Object
import eu.corvus.corax.scene.Spatial
import eu.corvus.corax.platforms.desktop.assets.loaders.AssimpLoader
import eu.corvus.corax.platforms.desktop.assets.loaders.TextureLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.RuntimeException
import kotlin.properties.Delegates

class AssetManagerImpl(
    private val storageAccess: StorageAccess
) : Object(), AssetManager {
    private val loaders = mutableMapOf<String, AssetManager.AssetLoader>()

    init {
        addLoader("png", TextureLoader())
        addLoader("jpg", TextureLoader())
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

    override suspend fun loadTexture(assetName: String): Texture = withContext(Dispatchers.IO) {
        val suffix = assetName.substringAfterLast('.')
        val assetLoader = loaders[suffix] ?: throw RuntimeException("Cannot find asset loader for $assetName")

        assetLoader.load(this@AssetManagerImpl, storageAccess, assetName) as Texture2D_PC
    }

    override suspend fun loadRaw(assetPath: String): ByteArray = withContext(Dispatchers.IO) { // this is not cache-able
        var data: ByteArray by Delegates.notNull()
        storageAccess.readFrom(assetPath) {
             data = it.readBytes()
        }

        data
    }

    override fun unload(assetName: String) {

    }

    override fun free() {
        super.free()

        loaders.clear()
    }
}
