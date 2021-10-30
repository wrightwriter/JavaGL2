package _0_1.wrightgl

import _0_1.main.Glob
import _0_1.math.Mat4
import _0_1.math.vector.Vec3

abstract class Camera {
    var roll = 0.0f
    var fov = 60.0f
    var near = 0.01f
    var far = 100.001f
    var eyePos: Vec3 = Vec3(0,0,-5)

    var viewMatrix: Mat4 = Mat4.identityMatrix
    var projectionMatrix: Mat4 = Mat4.identityMatrix
    var inverseViewMatrix: Mat4 = Mat4.identityMatrix

    abstract fun update()
    protected fun updatePerspectiveMatrix(){
        projectionMatrix = Mat4.getPerspectiveMatrix(
            fov,
            near,
            far,
            Glob.engine.res.x.toFloat(),
            Glob.engine.res.y.toFloat()
        )
    }
}
class CameraLookAt: Camera(){
    var lookAt: Vec3 = Vec3(0.001f,0.001f,0.001f)
    var up: Vec3 = Vec3(0,1,0)

    override fun update(){
        viewMatrix = Mat4.getLookAtMatrix(
            eyePos,
            lookAt,
            up
        )
        inverseViewMatrix = Mat4.getInverseLookAtMatrix(
            eyePos,
            lookAt,
            up
        )
        updatePerspectiveMatrix()
    }
}
class CameraPilot: Camera(){
    var pitch = 0.0f;
    var yaw= 0.0f;
    override fun update(){
        viewMatrix = Mat4.identityMatrix
        updatePerspectiveMatrix()
    }
}
