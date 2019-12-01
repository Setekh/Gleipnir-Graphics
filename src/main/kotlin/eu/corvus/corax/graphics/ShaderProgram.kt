package eu.corvus.corax.graphics

import eu.corvus.corax.scene.Object
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer
import org.lwjgl.system.MemoryStack
import org.joml.Matrix4f
import java.util.HashMap



/**
 * stolen
 */
@Deprecated(message = "To be replaced with proper implementation")
class ShaderProgram: Object() {
    private var programId: Int
    private var vertexShaderId: Int = 0
    private var fragmentShaderId: Int = 0
    private var uniforms: MutableMap<String, Int> = mutableMapOf()

    init {
        programId = glCreateProgram()
        if (programId == 0) {
            throw Exception("Could not create Shader")
        }
    }

    fun createUniform(uniformName: String) {
        val uniformLocation = glGetUniformLocation(programId, uniformName)
        if (uniformLocation < 0) {
            throw Exception("Could not find uniform:$uniformName")
        }

        uniforms[uniformName] = uniformLocation
    }

    fun setUniform(uniformName: String, value: Matrix4f) {
        val uniform = uniforms[uniformName] ?: return
        MemoryStack.stackPush().use { stack ->
            // Dump the matrix into a float buffer
            val fb = stack.mallocFloat(16)
            value.get(fb)
            glUniformMatrix4fv(uniform, false, fb)
        }
    }

    fun setUniform(uniformName: String, value: Int) {
        val uniform = uniforms[uniformName] ?: return
        glUniform1i(uniform, value)
    }

    fun createVertexShader(shaderCode: String) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER)
    }

    fun createFragmentShader(shaderCode: String) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER)
    }

    fun createShader(shaderCode: String, shaderType: Int): Int {
        val shaderId = glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Error creating shader. Type: $shaderType")
        }

        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024))
        }

        glAttachShader(programId, shaderId)

        return shaderId
    }

    fun link() {
        glLinkProgram(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024))
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId)
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId)
        }

        glValidateProgram(programId)
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024))
        }
    }

    fun bind() {
        glUseProgram(programId)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun cleanup() {
        unbind()
        if (programId != 0) {
            glDeleteProgram(programId)
        }
    }
}