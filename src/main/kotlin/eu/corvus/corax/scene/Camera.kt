package eu.corvus.corax.scene

import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector3f

open class Camera {
    val projectionMatrix : Matrix4fc = Matrix4f()

    private val mProjectionMatrix = projectionMatrix as Matrix4f

    fun useAsProjection(fov: Float, aspectRatio: Float, near: Float = 0.01f, far: Float = 300f) {
        mProjectionMatrix.perspective(fov, aspectRatio, near, far)
    }

    fun setLocation(location: Vector3f) {
        mProjectionMatrix.translate(location)
    }
}