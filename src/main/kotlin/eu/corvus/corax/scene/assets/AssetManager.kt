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
package eu.corvus.corax.scene.assets

import eu.corvus.corax.app.storage.StorageAccess
import eu.corvus.corax.graphics.material.textures.Texture
import eu.corvus.corax.scene.Object
import eu.corvus.corax.scene.Spatial
import java.io.File
import java.nio.file.WatchKey

/**
 * @author Vlad Ravenholm on 12/28/2019
 */
interface AssetManager {
    fun addLoader(suffix: String, assetLoader: AssetLoader)

    fun removeLoader(suffix: String)

    suspend fun loadSpatial(assetName: String): Spatial
    suspend fun loadTexture(assetName: String): Texture
    suspend fun loadRaw(assetPath: String): ByteArray
    fun watch(assetPath: File, callback: () -> Unit): WatchKey

    fun unload(assetName: String)

    interface AssetLoader {
        suspend fun load(assetManager: AssetManager, storageAccess: StorageAccess, path: String): Object
    }

}