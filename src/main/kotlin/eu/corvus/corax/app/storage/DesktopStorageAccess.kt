package eu.corvus.corax.app.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files

class DesktopStorageAccess : StorageAccess {
    companion object {
        @JvmStatic val ROOT_DIR = File(".")
    }

    override suspend fun readFrom(path: String, block: (InputStream) -> Unit) = withContext(Dispatchers.IO) {
        val file = File(ROOT_DIR, path)

        if (file.exists()) {
            Files.newInputStream(file.toPath()).use(block)
        } else {
            readFromResource(path, block)
        }
    }

    private fun readFromResource(
        resourcePath: String,
        block: (InputStream) -> Unit
    ) {
        val resourceAsStream = javaClass.classLoader.getResourceAsStream(resourcePath)
        resourceAsStream.use(block)
    }

    override suspend fun writeTo(path: String, block: (OutputStream) -> Unit) = withContext(Dispatchers.IO)  {
        val file = File(ROOT_DIR, path)
        if (!file.exists()) {
            val parentFile = file.parentFile
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            file.createNewFile()
        }

        Files.newOutputStream(file.toPath()).use { block(it) }
    }

    override suspend fun delete(path: String) = withContext(Dispatchers.IO) {
        val file = File(ROOT_DIR, path)
        Files.deleteIfExists(file.toPath())
    }
}