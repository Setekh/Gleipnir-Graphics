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

import eu.corvus.corax.graphics.material.textures.Texture
import eu.corvus.corax.scene.Object
import eu.corvus.corax.utils.Logger
import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * @author Vlad Ravenholm on 1/4/2020
 */
abstract class Material: Object() {
    abstract val shader: Shader

    val params: Map<String, Any> = hashMapOf() //hashmap of String MaterialParam actually

    fun onReady() {
        shader.onReady()

        params.forEach { (name, value) ->
            val uniform = getParam<Shader.Uniform<Any>>(name)
            uniform?.update(value)
        }
    }

    /**
     * For direct access
     *
     * Warning: Will not affect serialized values, this may be used for real time processing
     */
    @Suppress("UNCHECKED_CAST")
    fun <T: Shader.Uniform<out Any>> getParam(name: String): T? { // This is bad, info like this should be unique to the material, shaders are shared.
        val uniform = shader.findUniform(name) as? T
        uniform ?: let { Logger.warn("Missing param $name") }
        return uniform
    }

    fun setParam(name: String, value: Matrix4f) {
        getParam<Shader.Mat4fUniform>(name)?.update(value)
        updateParam(name, value)
    }

    fun setParam(name: String, value: Vector3f) {
        getParam<Shader.Vec3fUniform>(name)?.update(value)
        updateParam(name, value)
    }

    fun setParam(name: String, value: Float) {
        getParam<Shader.FloatUniform>(name)?.update(value)
        updateParam(name, value)
    }

    fun setParam(name: String, value: Int) {
        getParam<Shader.IntUniform>(name)?.update(value)
        updateParam(name, value)
    }

    fun setParam(name: String, value: Texture) {
        getParam<Shader.IntUniform>(name)?.update(value.id)
    }

    private fun updateParam(name: String, value: Any) {
        params as HashMap
        params[name] = value
    }

}