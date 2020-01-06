package eu.corvus.corax.graphics.material

import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.utils.MATRIX4f_IDENTITY
import eu.corvus.corax.utils.VECTOR3f_ZERO
import org.joml.Matrix4fc
import org.joml.Vector3fc

abstract class Shader(val renderContext: RendererContext) {
    var programId: Int = 0
        protected set

    private var vertexShaderId = 0
    private var fragmentShaderId = 0

    abstract val vertexSource: String
    abstract val fragmentSource: String

    abstract class Uniform<T: Any>(val name: String, var value: T) {
        var uniformId: Int = 0

        override fun toString(): String = "Uniform[$name] value=$value"
    }

    class FloatUniform(name: String) : Uniform<Float>(name, 0f)
    class Vec3fUniform(name: String) : Uniform<Vector3fc>(name, VECTOR3f_ZERO)
    class Mat4fUniform(name: String) : Uniform<Matrix4fc>(name, MATRIX4f_IDENTITY)
}

