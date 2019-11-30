package eu.corvus.corax.scene

import org.joml.Matrix4f

open class Camera(name: String = "Camera") : Spatial(name) {
    val viewMatrix = Matrix4f()
    val projectionMatrix = Matrix4f()
    val viewProjectionMatrix = Matrix4f()

    fun useAsProjection(fov: Double, aspectRatio: Float, near: Float = 0.01f, far: Float = 300f) {
        projectionMatrix.identity().perspective(fov.toFloat(), aspectRatio, near, far)
    }

    fun computeMatrices() {
        val cameraPos = transform.translation
        val rotation = transform.rotation

        viewMatrix.identity().rotate(rotation).translate(cameraPos)

        viewProjectionMatrix.set(projectionMatrix).mul(viewMatrix)
    }
}