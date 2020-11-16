package eu.corvus.corax.scene

import org.joml.Matrix4f
import org.joml.Vector3f

open class Camera(name: String = "Camera") : Spatial(name) {
    companion object{
        @JvmStatic
        val UP = Vector3f(0f,1f, 0f)
    }

    private var fov: Float = 70.0f

    var width: Int = 300
    var height: Int = 300

    val viewMatrix = Matrix4f()
    val projectionMatrix = Matrix4f()
    val viewProjectionMatrix = Matrix4f()

    var isPerspective = false
        private set

    init {
        alwaysCompute = true
    }

    val up: Vector3f
        get() = transform.rotation.positiveY(Vector3f())

    val dir: Vector3f
        get() = transform.rotation.positiveZ(Vector3f())

    val left: Vector3f
        get() = transform.rotation.positiveX(Vector3f())

    fun useAsPerspective(fov: Float, aspectRatio: Float, near: Float = 0.01f, far: Float = 300f) {
        this.fov = fov
        projectionMatrix.identity().perspective(fov, aspectRatio, near, far)
        isPerspective = true
    }

    fun updateResize(width: Int, height: Int) {
        val aspectRatio = width.toFloat() / height.toFloat()
        if (isPerspective)
            useAsPerspective(fov, aspectRatio)
        else
            error("Not supported!")

        this.width = width
        this.height = height
        forceUpdate()
    }

    override fun computeWorldTransform() {
        super.computeWorldTransform()
        computeMatrices()
    }

    private fun computeMatrices() {
        val cameraPos = worldTransform.translation
        val rotation = worldTransform.rotation

        viewMatrix.identity().rotate(rotation).translate(cameraPos.x, cameraPos.y, cameraPos.z)
        viewProjectionMatrix.set(projectionMatrix).mul(viewMatrix)
    }
}