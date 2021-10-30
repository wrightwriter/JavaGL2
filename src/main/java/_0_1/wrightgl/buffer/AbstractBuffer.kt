package _0_1.wrightgl.buffer

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

    fun bindAsSSBO(bindIdx: Int = 0){
        GL46.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, bindIdx, type.value)
    }
    enum class Type(val value: Int) {
        FLOAT(GL11.GL_FLOAT);
    }
}