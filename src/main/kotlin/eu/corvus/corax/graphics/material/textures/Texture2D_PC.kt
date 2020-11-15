package eu.corvus.corax.graphics.material.textures

import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer

class Texture2D_PC : Texture() {
    override fun freeData() {
        if (buffer != null) {
            STBImage.stbi_image_free(buffer as ByteBuffer)
            buffer = null
        }
    }
}