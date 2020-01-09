package eu.corvus.corax.graphics.context

import eu.corvus.corax.graphics.*
import eu.corvus.corax.graphics.buffers.VertexArrayObject
import eu.corvus.corax.graphics.buffers.VertexBufferObject
import eu.corvus.corax.graphics.buffers.types.*
import eu.corvus.corax.graphics.material.shaders.Shader
import eu.corvus.corax.graphics.material.textures.Texture
import eu.corvus.corax.scene.Object
import eu.corvus.corax.scene.assets.AssetManager
import eu.corvus.corax.utils.Logger
import kotlinx.coroutines.runBlocking
import org.joml.Vector3fc
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class GLFWOpenGLContext : RendererContext {
    init {
        // Bind GL textures
        GlTextureFormats.initialize()
    }

    override fun viewPort(x: Int, y: Int, w: Int, h: Int) = glViewport(x, y, w, h)

    override fun clear(mask: Int) = glClear(mask)

    override fun clearColor(color: Color) = glClearColor(color.r(), color.g(), color.b(), color.a())

    override fun enable(mask: Int) = glEnable(mask)

    override fun bindBufferArray(vertexArrayObject: VertexArrayObject) {
        glBindVertexArray(vertexArrayObject.id)
    }

    override fun bindBufferObject(vertexBufferObject: VertexBufferObject) {
        val target = targetBufferType(vertexBufferObject)
        glBindBuffer(target, vertexBufferObject.id)
    }

    override fun createProgram(assetManager: AssetManager, shader: Shader) {
        val programId = glCreateProgram()
        shader.onCreate(programId)

        val vertexShaderId = createShader(assetManager, GL20.GL_VERTEX_SHADER, shader.vertexResource)
        val fragmentShaderId = createShader(assetManager, GL20.GL_FRAGMENT_SHADER, shader.fragmentResource)

        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragmentShaderId)

        glLinkProgram(shader.programId)
        if (glGetProgrami(shader.programId, GL_LINK_STATUS) == 0) {
            throw Exception("Error linking Shader code: ${glGetProgramInfoLog(shader.programId, 1024)}")
        }

        glDetachShader(shader.programId, vertexShaderId)
        glDetachShader(shader.programId, fragmentShaderId)

        glValidateProgram(shader.programId)
        if (glGetProgrami(shader.programId, GL_VALIDATE_STATUS) == 0) {
            Logger.info("Warning validating Shader code: ${glGetProgramInfoLog(shader.programId, 1024)}")
        }

        shader.onReady()
    }

    override fun useProgram(shader: Shader) {
        glUseProgram(shader.programId)
    }

    private fun createShader(assetManager: AssetManager, shaderType: Int, shaderResource: String): Int {
        val shaderSource = runBlocking { assetManager.loadRaw(shaderResource).toString(Charsets.UTF_8) }

        val shaderId = glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Error creating shader. Type: ${shaderTypeName(shaderType)}")
        }

        glShaderSource(shaderId, shaderSource)
        glCompileShader(shaderId)

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw Exception("Error compiling Shader code: ${glGetShaderInfoLog(shaderId, 1024)}")
        }

        return shaderId
    }

    override fun unbindBufferArray(vertexArrayObject: VertexArrayObject) {
        glBindVertexArray(0)
    }

    override fun unbindBufferObject(vertexBufferObject: VertexBufferObject) {
        val target = targetBufferType(vertexBufferObject)
        glBindBuffer(target, 0)
    }

    override fun createArrayBufferData(vertexArrayObject: VertexArrayObject) {
        val vaoId = glGenVertexArrays()
        vertexArrayObject.onAssign(vaoId)

        bindBufferArray(vertexArrayObject)

        repeat(vertexArrayObject.vertexBuffers.count()) {
            val bufferObject = vertexArrayObject.vertexBuffers[it] ?: return@repeat
            val id = glGenBuffers()

            bufferObject.onAssign(id)
            when (bufferObject) {
                is VertexBuffer, is TextureCoordsBuffer, is NormalBuffer, is TangentBuffer, is BiTangentBuffer -> createFloatBufferData(
                    bufferObject.type.ordinal,
                    bufferObject
                )
                is IndexBuffer -> createIndexBufferData(bufferObject)
            }
            bufferObject.clearData()
        }
    }

    private fun createIndexBufferData(bufferObject: IndexBuffer) {
        val data = bufferObject.data()
        bindBufferObject(bufferObject)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data as IntBuffer, GL_STATIC_DRAW)
    }

    private fun createFloatBufferData(index: Int, bufferObject: VertexBufferObject) {
        val data = bufferObject.buffer

        bindBufferObject(bufferObject)
        glBufferData(GL_ARRAY_BUFFER, data as FloatBuffer, GL_STATIC_DRAW)
        glVertexAttribPointer(index, bufferObject.type.size, GL_FLOAT, false, 0, 0)
    }

    override fun draw(vertexArrayObject: VertexArrayObject) {
        // Render the vertex buffer
        vertexArrayObject.vertexBuffers.forEach { // maybe make it more efficient?
            if (it != null && it !is IndexBuffer){
                glEnableVertexAttribArray(it.type.ordinal)
            }
        }

        glDrawElements(GL_TRIANGLES, vertexArrayObject.size, GL_UNSIGNED_INT, 0)
    }

    override fun setUniformMatrix4fv(location: Int, transpose: Boolean, fb: FloatBuffer) {
        glUniformMatrix4fv(location, transpose, fb)
    }

    override fun setUniform1f(location: Int, value: Float) {
        glUniform1f(location, value)
    }

    override fun setUniform3f(location: Int, value: Vector3fc) {
        glUniform3f(location, value.x(), value.y(), value.z())
    }

    override fun setUniform1i(location: Int, value: Int) {
        glUniform1i(location, value)
    }

    override fun getUniformLocation(programId: Int, name: String): Int {
        val uniformLocation = glGetUniformLocation(programId, name)

        if (uniformLocation < 0) {
            when (uniformLocation) {
                -3 -> Logger.error("Trying to get an uniform from a uninitialized shader! $name")
                -2 -> Logger.error("Trying to get an non existing uniform! $name")
                -1 -> Logger.warn("Uniform exists but was optimized out of the shader code! $name")
            }
        }

        return uniformLocation
    }

    override fun free(glObject: Object) {
        when (glObject) {
            is VertexArrayObject -> glDeleteVertexArrays(glObject.id)
            is VertexBufferObject -> glDeleteBuffers(glObject.id)
            is Texture -> GL11.glDeleteTextures(glObject.id)
        }
    }

    private fun targetBufferType(vertexBufferObject: VertexBufferObject): Int {
        return when (vertexBufferObject.type) {
            BufferType.Vertex -> GL15.GL_ARRAY_BUFFER
            BufferType.TextCoord -> GL15.GL_ARRAY_BUFFER
            BufferType.Normals -> GL15.GL_ARRAY_BUFFER
            BufferType.Tangents -> GL15.GL_ARRAY_BUFFER
            BufferType.BiTangents -> GL15.GL_ARRAY_BUFFER
            BufferType.Indices -> GL15.GL_ELEMENT_ARRAY_BUFFER
        }
    }

    private fun shaderTypeName(it: Int): String {
        return when (it) {
            GL_VERTEX_SHADER -> "Vertex shader"
            GL_FRAGMENT_SHADER -> "Vertex shader"
            else -> "Unknown type"
        }
    }
}