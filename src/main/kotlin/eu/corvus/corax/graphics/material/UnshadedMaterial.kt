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
package eu.corvus.corax.graphics.material

import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.graphics.material.shaders.MatcapShader
import eu.corvus.corax.graphics.material.shaders.UnshadedShader
import eu.corvus.corax.graphics.material.textures.Texture
import eu.corvus.corax.scene.Camera
import eu.corvus.corax.scene.assets.AssetManager
import eu.corvus.corax.scene.geometry.Geometry
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.joml.Vector4f
import org.joml.Vector4fc
import org.koin.core.KoinComponent

/**
 * @author Vlad Ravenholm on 1/6/2020
 */
class UnshadedMaterial(private var textureAsset: String? = null, val color: Vector4fc = Vector4f(1f)): Material(), KoinComponent {
    override val shader = UnshadedShader()

    private var texture: Texture? = null
    private var loadingTexture = ""

    override fun applyParams(
        renderContext: RendererContext,
        camera: Camera,
        geometry: Geometry
    ) {
        shader.setUniformValue(shader.viewProjection, camera.viewProjectionMatrix)
        shader.setUniformValue(shader.modelMatrix, geometry.worldMatrix)
        shader.setUniformValue(shader.color, color)

        val texture = texture ?: return
        shader.setUniformValue(shader.texture, 0)
        renderContext.useTexture(texture, 0)
    }

    override fun prepareUpload(assetManager: AssetManager, rendererContext: RendererContext) {
        super.prepareUpload(assetManager, rendererContext)

        val textureAsset = textureAsset ?: return
        val texture = texture

        if (texture == null && loadingTexture.isEmpty() || loadingTexture != textureAsset) {
            loadingTexture = textureAsset

            texture?.free() //TODO track inside it how many refs it has
            this.texture = null

            scope.launch {
                this@UnshadedMaterial.texture = assetManager.loadTexture(loadingTexture)
            }
        }

        if (texture != null && !texture.isUploaded) {
            rendererContext.createTexture(texture)
        }
    }

    override fun free() {
        super.free()

        texture?.free()
    }
}