package _0_1.wrightgl.buffer

import _0_1.engine.Constants
import _0_1.main.Global
import _0_1.math.vector.IVec3
import _0_1.math.vector.Vec3
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER
import org.lwjgl.opengl.GL46

abstract class AbstractBuffer {
    var type = Type.FLOAT
    var pid: Int = 0
    var name: String? = null
    lateinit var data: FloatArray
        protected set
    lateinit var signature: IntArray
        protected set

    var totalVertCnt: Int = 0
        protected set
    var singleVertElementsCnt: Int = 0
        protected set

    var singleVertSizeBytes: Int = 0
        protected set
    var totalSizeBytes: Int = 0
        protected set

    fun bindAsSSBO(){
        val bindIdx = Global.engine.wgl.currSSBOBindNumber
        Global.engine.wgl.currSSBOBindNumber++
        bindAsSSBO(bindIdx)
    }
    fun bindAsSSBO(bindIdx: Int = 0){
        GL46.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, bindIdx, pid)
        Global.engine.wgl.setUniform(
            Constants.ssboRes + "[" + bindIdx.toString() + "]",
            totalVertCnt
        )

    }
    enum class Type(val value: Int) {
        FLOAT(GL11.GL_FLOAT);
    }
}