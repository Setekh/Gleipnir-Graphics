package eu.corvus.corax.graphics.material.textures

import org.lwjgl.system.MemoryUtil
import java.nio.Buffer

class Texture2D : Texture() {
    fun withData(buffer: Buffer) {
        this.buffer = buffer
    }

    override fun free() {
        super.free()
    }
}