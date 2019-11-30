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
package eu.corvus.corax.scene

import org.joml.Quaternionf
import org.joml.Vector3f


/**
 * @author Vlad Ravenholm on 11/28/2019
 */
open class Transform {
    companion object {
        @JvmStatic
        val Identity = Transform()
    }

    val translation = Vector3f(0f, 0f, 0f)
    val rotation = Quaternionf().identity()
    val scale = Vector3f(1f, 1f, 1f)

    fun identity(): Transform {
        translation.set(Identity.translation)
        rotation.set(Identity.rotation)
        scale.set(Identity.scale)
        return this
    }

    fun set(transform: Transform): Transform {
        translation.set(transform.translation)
        rotation.set(transform.rotation)
        scale.set(transform.scale)
        return this
    }

    fun mergeParentTransform(parentTransform: Transform) {
        scale.mul(parentTransform.scale)
        parentTransform.rotation.mul(rotation, rotation)
        translation.mul(parentTransform.scale)

        //parentTransform.translation.rotate(parentTransform.rotation, translation).add(parentTransform.translation)
        //parentTransform.rotation.transform(parentTransform.translation, translation).add(parentTransform.translation)
        parentTransform.rotation.mult(translation).add(parentTransform.translation)
    }
}

fun Quaternionf.mult(v: Vector3f): Vector3f {
    val tempX: Float =
        w * w * v.x + 2 * y * w * v.z - 2 * z * w * v.y + x * x * v.x
        + 2 * y * x * v.y + 2 * z * x * v.z - z * z * v.x - y * y * v.x

    val tempY: Float =
        2 * x * y * v.x + y * y * v.y + 2 * z * y * v.z + 2 * w * z *
                v.x - z * z * v.y + w * w * v.y - 2 * x * w * v.z - x * x * v.y
    v.z =
        2 * x * z * v.x + 2 * y * z * v.y + z * z * v.z - 2 * w * y * v.x - y * y * v.z + 2 * w * x * v.y - x * x * v.z + w * w * v.z
    v.x = tempX
    v.y = tempY
    return v
}