package eu.corvus.corax.graphics.material

import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.utils.MATRIX4f_IDENTITY
import eu.corvus.corax.utils.VECTOR3f_ZERO
import org.joml.Matrix4fc
import org.joml.Vector3fc
import org.lwjgl.system.MemoryStack

abstract class Shader(private val renderContext: RendererContext) {
    var programId: Int = 0
        protected set

    private var vertexShaderId = 0
    private var fragmentShaderId = 0

    abstract val vertexSource: String
    abstract val fragmentSource: String

    protected abstract val uniforms: Array<out Uniform<out Any>>

    abstract class Uniform<T : Any>(val name: String, var value: T) {
        var uniformLocation: Int = 0
            internal set

        val isUploaded: Boolean
            get() = uniformLocation > 0

        override fun toString(): String = "Uniform[$name] value=$value"

        abstract fun setValue(rendererContext: RendererContext)

        fun update(value: T) {
            this.value = value
        }
    }

    class IntUniform(name: String) : Uniform<Int>(name, 0) {
        override fun setValue(rendererContext: RendererContext) = rendererContext.setUniform1i(uniformLocation, value)
    }

    class FloatUniform(name: String) : Uniform<Float>(name, 0f) {
        override fun setValue(rendererContext: RendererContext) = rendererContext.setUniform1f(uniformLocation, value)
    }

    class Vec3fUniform(name: String) : Uniform<Vector3fc>(name, VECTOR3f_ZERO) {
        override fun setValue(rendererContext: RendererContext) = rendererContext.setUniform3f(uniformLocation, value)
    }

    class Mat4fUniform(name: String) : Uniform<Matrix4fc>(name, MATRIX4f_IDENTITY) {
        override fun setValue(rendererContext: RendererContext) {
            MemoryStack.stackPush().use { stack ->
                val fb = stack.mallocFloat(16)
                value.get(fb)

                rendererContext.setUniformMatrix4fv(uniformLocation, false, fb)
            }
        }
    }


    open fun onReady() {
        createUniform(*uniforms)
    }

    open fun onBind() {
        repeat(uniforms.size) {
            val uniform = uniforms[it]
            uniform.setValue(renderContext)
        }
    }

    fun createUniform(vararg uniforms: Uniform<out Any>) {
        repeat(uniforms.size) {
            val uniform = uniforms[it]

            val uniformLocation = renderContext.getUniformLocation(programId, uniform.name)
            uniform.uniformLocation = uniformLocation
        }
    }

    fun findUniform(name: String): Uniform<out Any> {
        return uniforms.first { name == it.name }
    }
}

