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
package eu.corvus.corax.graphics.material.textures

import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.scene.Object
import eu.corvus.corax.utils.height
import eu.corvus.corax.utils.width
import org.joml.Vector2i
import org.koin.core.get
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.system.MemoryUtil
import java.lang.RuntimeException
import java.nio.Buffer
import java.nio.IntBuffer

/**
 * @author Vlad Ravenholm on 12/28/2019
 */
abstract class Texture: Object() {
    open var id: Int = 0
        protected set

    var buffer: Buffer? = null
        protected set

    val isUploaded: Boolean
        get() = id > 0

    val dimensions: Vector2i = Vector2i()

    val width: Int = dimensions.width
    val height: Int = dimensions.height

    var generateMipMaps = true

    open fun onAssign(bufferId: Int) {
        id = bufferId
    }

    fun setData(buffer: Buffer, width: Int, height: Int) {
        if (this.buffer != null || isUploaded)
            throw RuntimeException("Texture already has data")

        this.buffer = buffer
        dimensions.set(width, height)
    }

    open fun freeData() {}

    override fun free() {
        super.free()

        val renderContext = get<RendererContext>()
        renderContext.free(this)

        freeData()

        if (buffer != null) {
            MemoryUtil.memFree(buffer)
            buffer = null
        }
    }
}