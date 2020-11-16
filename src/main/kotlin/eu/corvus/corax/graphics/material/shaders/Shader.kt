package eu.corvus.corax.graphics.material.shaders

import eu.corvus.corax.graphics.context.RendererContext
import eu.corvus.corax.scene.Object
import org.joml.Matrix4fc
import org.joml.Vector3fc
import org.joml.Vector4fc
import org.koin.core.inject
import org.lwjgl.system.MemoryStack

abstract class Shader: Object() {
    private val renderContext: RendererContext by inject()

    var programId: Int = 0
        protected set

    val isUploaded: Boolean
        get() = programId > 0

    abstract val vertexResource: String
    abstract val fragmentResource: String

    protected abstract val uniforms: Array<out Uniform<out Any>>

    fun onCreate(programId: Int) {
        this.programId = programId
    }

    abstract class Uniform<T : Any>(val name: String) {
        /**
         *  Unset yet: -3
         *  Unk location: -2
         *  Not defined in shader: -1
         */
        var uniformLocation: Int = -3
            internal set

        val isValid: Boolean
            get() = uniformLocation >= -2

        override fun toString(): String = "Uniform[$name]"

        abstract fun setValue(rendererContext: RendererContext, value: T)
    }

    class IntUniform(name: String) : Uniform<Int>(name) {
        override fun setValue(rendererContext: RendererContext, value: Int) = rendererContext.setUniform1i(uniformLocation, value)
    }

    class FloatUniform(name: String) : Uniform<Float>(name) {
        override fun setValue(rendererContext: RendererContext, value: Float) = rendererContext.setUniform1f(uniformLocation, value)
    }

    class Vec3fUniform(name: String) : Uniform<Vector3fc>(name) {
        override fun setValue(rendererContext: RendererContext, value: Vector3fc) = rendererContext.setUniform3f(uniformLocation, value)
    }

    class Vec4fUniform(name: String) : Uniform<Vector4fc>(name) {
        override fun setValue(rendererContext: RendererContext, value: Vector4fc) = rendererContext.setUniform4f(uniformLocation, value)
    }

    class Mat4fUniform(name: String) : Uniform<Matrix4fc>(name) {
        override fun setValue(rendererContext: RendererContext, value: Matrix4fc) {
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

    fun <T: Any> setUniformValue(uniform: Uniform<T>, value: T) {
        uniform.setValue(renderContext, value)
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