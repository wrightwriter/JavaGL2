package _0_1.wrightgl.buffer

import _0_1.math.Mat4
import _0_1.math.vector.Vec2
import _0_1.math.vector.Vec3
import _0_1.math.vector.Vec4
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL15.glBindBuffer
import org.lwjgl.opengl.GL30.glBindBufferBase
import org.lwjgl.opengl.GL30.glBindBufferRange
import org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER
import org.lwjgl.opengl.GL46

class UBO(
    val name: String,
    val maxSz: Int = 1024 // size in bytes
) {
    var pid = 0
        internal set
    var offsBytes = 0
        internal set

    init{
        pid = GL46.glCreateBuffers()
        GL46.glNamedBufferStorage(pid, maxSz.toLong(), GL46.GL_DYNAMIC_STORAGE_BIT)
    }

    // float, bool, int 4
    // array element 16
    // vec 16
    // mat4 16 * 4

    fun bind(bindIdx: Int): UBO{
        glBindBufferBase(GL_UNIFORM_BUFFER, bindIdx, pid);
        return this
    }
    fun rewind(bytes: Int = 0){
        offsBytes = bytes
    }

    private fun startAlign(byteSz: Int){
        val remainer = offsBytes % byteSz
        if (remainer != 0)
            offsBytes += byteSz - remainer
    }
    private fun endAlign(byteSz: Int){
        offsBytes += byteSz
    }
    fun add(v: Float){
        val byteSz = 4
        startAlign(byteSz)
        GL46.glNamedBufferSubData(pid, offsBytes.toLong(), floatArrayOf(v))
        endAlign(byteSz)
    }
    fun add(v: Int){
        val byteSz = 4
        startAlign(byteSz)
        GL46.glNamedBufferSubData(pid, offsBytes.toLong(), intArrayOf(v))
        endAlign(byteSz)
    }
    fun add(v: Vec4){
        val byteSz = 16
        startAlign(byteSz)
        GL46.glNamedBufferSubData(pid, offsBytes.toLong(), v.vals)
        endAlign(byteSz)
    }
    fun add(v: Vec3){
        add(Vec4(v.x,v.y,v.z,0.0f))
    }
    fun add(v: Vec2){
        val byteSz = 8
        startAlign(byteSz)
        GL46.glNamedBufferSubData(pid, offsBytes.toLong(), v.vals)
        endAlign(byteSz)
    }
    fun add(v: Mat4){
        val byteSz = 16
        startAlign(byteSz)
        GL46.glNamedBufferSubData(pid, offsBytes.toLong(), v.vals)
        endAlign(byteSz*4)
    }
    fun add(v: FloatArray){
        val byteSz = 16
        startAlign(byteSz)
        GL46.glNamedBufferSubData(pid, offsBytes.toLong(), v)
        endAlign(byteSz*v.size)
    }
    fun add(v: Array<Vec4>){
        for (vec in v){
            add(vec)
        }
    }
}