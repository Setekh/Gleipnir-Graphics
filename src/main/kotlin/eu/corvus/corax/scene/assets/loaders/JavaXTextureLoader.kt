/**
 * Copyright (c) 2013-2019 Corvus Corax Entertainment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of Corvus Corax Entertainment nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.corvus.corax.scene.assets.loaders

import eu.corvus.corax.app.storage.StorageAccess
import eu.corvus.corax.graphics.material.textures.Format
import eu.corvus.corax.scene.Object
import eu.corvus.corax.scene.assets.AssetManager
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferUShort
import javax.imageio.ImageIO


/**
 * @author Vlad Ravenholm on 1/6/2020
 */
class JavaXTextureLoader: AssetManager.AssetLoader {
    override suspend fun load(assetManager: AssetManager, storageAccess: StorageAccess, path: String): Object {
        storageAccess.readFrom(path) { stream ->
            val bufferedImage = ImageIO.read(stream)

            val format = when (bufferedImage.type) {
                BufferedImage.TYPE_4BYTE_ABGR -> Format.ABGR8
                BufferedImage.TYPE_3BYTE_BGR -> Format.BGR8
                else -> error("No such type ${bufferedImage.type}")
            }

            val pixelSize = bufferedImage.colorModel.pixelSize
            val buffer = MemoryUtil.memAlloc(pixelSize * bufferedImage.width * bufferedImage.height)

            val buf = bufferedImage.raster.dataBuffer
            when (buf.dataType) {
                DataBuffer.TYPE_BYTE -> {
                    val byteBuf = buf as DataBufferByte
                    buffer.put(byteBuf.data)
                }
                DataBuffer.TYPE_USHORT -> {
                    val shortBuf = buf as DataBufferUShort
                    val bytes = shortBuf.data.map { it.toByte() }.toByteArray()
                    buffer.put(bytes)
                }
            }

        }

        return Object()
    }
}
