package _0_1.wrightgl

import _0_1.engine.IO
import _0_1.main.Global
import _0_1.math.Mat4
import _0_1.math.MathUtils
import _0_1.math.vector.Vec2
import _0_1.math.vector.Vec3
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.cos
import kotlin.math.sin


abstract class Camera {
    var roll = 0.0f
    var fov = 80.0f
    var near = 0.01f
    var far = 100.001f
    var eyePos: Vec3 = Vec3(0,0,-5)

    var viewMatrix: Mat4 = Mat4.identityMatrix
    var projectionMatrix: Mat4 = Mat4.identityMatrix
    var inverseViewMatrix: Mat4 = Mat4.identityMatrix

    abstract fun update()
    protected fun updateProjMatrix(){
        projectionMatrix = Mat4.getPerspectiveMatrix(
            fov,
            near,
            far,
            Global.engine.res.x.toFloat(),
            Global.engine.res.y.toFloat()
        )
    }
}
class CameraLookAt: Camera(){
    var lookAt: Vec3 = Vec3(0.001f,0.001f,0.001f)
    var up: Vec3 = Vec3(0,1,0)

    override fun update(){
//        viewMatrix =  Mat4.getLookAtSOMatrix(eyePos, lookAt)* Mat4.identityMatrix
//        viewMatrix =  Mat4.getLookAtOldMatrix(eyePos, lookAt, Vec3(0,1,0))
//          viewMatrix =
        var mat = Matrix4f().identity()

          org.joml.Matrix4f().identity().lookAt(
              -eyePos.x, eyePos.y, eyePos.z,
             -lookAt.x, lookAt.y, lookAt.z,
              0.0f,1.0f,0.0f).get(viewMatrix.vals)


//        ) * Mat4.getTranslationMatrix(eyePos.negative()) * Mat4.identityMatrix
//        inverseViewMatrix = Mat4.getInverseLookAtMatrix(
//            eyePos,
//            lookAt,
//            up
//        )
        inverseViewMatrix = projectionMatrix
            .times(viewMatrix)
            .inverse()

        updateProjMatrix()
    }
}
class CameraPilot: Camera(){
    var pitch = 0.0f;
    var yaw= 0.0f;

    override fun update(){
        val io = Global.engine.io
        // Pitch / Yaw
        if (io.keyboard[IO.Key.LCtrl]!!.Down){
            val deltaMouse = io.deltaMousePos/Global.engine.res.x.toFloat()
            val speed = 1.25f;

            if (io.LMBDown ) {
                pitch += deltaMouse.y*speed;
                yaw -= deltaMouse.x*speed;
            }


        }
        // Movement

        val dir = Vec3(0, 0, 1)
            .rotX (pitch * MathUtils.Tau)
            .rotY( yaw * MathUtils.Tau)

        val len = Vec3.length(dir)

        val right = Vec3.normalize(Vec3.cross(Vec3(0,1,0), dir))
        val up: Vec3 = Vec3.cross(dir, right)

        if (io.keyboard[IO.Key.LCtrl]!!.Down){
            var keyInputRoll = 0.0f;
            var speed = 10.05f;
            speed *= Global.engine.deltaTime;


            val keyInput = Vec2(0.0f)
            if (glfwGetWindowAttrib(Global.engine.glfwWindow, GLFW_FOCUSED) == GLFW_TRUE) {
                if (io.keyboard[IO.Key.A]!!.Down) {
                    keyInput[0] -= 1.0f;
                }
                if (io.keyboard[IO.Key.D]!!.Down) {
                    keyInput[0] += 1.0f;
                }
                if (io.keyboard[IO.Key.W]!!.Down) {
                    keyInput[1] += 1.0f;
                }
                if (io.keyboard[IO.Key.S]!!.Down) {
                    keyInput[1] -= 1.0f;
                }
                if (io.keyboard[IO.Key.Q]!!.Down) {
                    keyInputRoll -= 1.0f;
                }
                if (io.keyboard[IO.Key.E]!!.Down) {
                    keyInputRoll += 1.0f;
                }
                if (io.keyboard[IO.Key.LShift]!!.Down) {
                    speed *= 2.0f;
                }
//                if (keyInput[0] == 0 && keyInput[1] == 0 && keyInputRoll == 0) {
//                    return false;
//                }



                val deltaDir = dir.copy() * keyInput[1] * speed
                val deltaRight = right.copy() * keyInput[0] * speed

                eyePos += deltaRight + deltaDir

                roll += keyInputRoll * speed * 0.1f


                //float* up = Math::normalize(Math::cross(dir, right));
                //up = Math::multiply(up, keyInput[0]);
            }

        }

//        viewMatrix = Mat4.getLookAtSOMatrix(
//            eyePos,
//            eyePos + dir,
//            Vec3(0,1,0)
//        )

        var mat = Matrix4f().identity()

        org.joml.Matrix4f().identity().lookAt(
            -eyePos.x, eyePos.y, eyePos.z,
            -eyePos.x - dir.x, eyePos.y + dir.y, eyePos.z + dir.z,
            0.0f,1.0f,0.0f).get(viewMatrix.vals)
//        viewMatrix = Mat4.getLookAtSOMatrix(
//            eyePos,
//            eyePos + dir,
//            Vec3(0,1,0)
//        )

        updateProjMatrix()

        inverseViewMatrix = projectionMatrix
            .times(viewMatrix)
            .inverse()
//            .times(Mat4.getTranslationMatrix(eyePos))
    }
}
