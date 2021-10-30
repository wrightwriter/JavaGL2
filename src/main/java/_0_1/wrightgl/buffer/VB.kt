package _0_1.wrightgl.buffer

import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY
import org.lwjgl.opengl.GL32.GL_TRIANGLE_STRIP_ADJACENCY
import org.lwjgl.opengl.GL40.GL_PATCHES
import java.lang.Float

class VB(
    _geometry: FloatArray,
    _signature: IntArray,
    var primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
): AbstractBuffer() {
    var vaoPid: Int
//    var data: FloatBuffer? = null


    companion object {
        lateinit var quadVB: VB
            internal set
    }

    init {
        signature = _signature
        data = _geometry.clone()

        singleVertElementsCnt = 0
        for (j in signature) {
            singleVertElementsCnt += j
        }

        totalVertCnt = _geometry.size / singleVertElementsCnt

        val singleVertSizeBytes: Int = singleVertElementsCnt * Float.BYTES
        val totalVertSizeBytes: Int = singleVertSizeBytes * totalVertCnt

        pid = GL46.glCreateBuffers()
        GL46.glNamedBufferStorage(pid, _geometry, GL46.GL_DYNAMIC_STORAGE_BIT)
//        GL46.glNamedBufferStorage(vboPid, _geometry, 0)

        vaoPid = GL46.glCreateVertexArrays()
        val bindIdx = 0

        GL46.glVertexArrayVertexBuffer(vaoPid, bindIdx, pid, 0, singleVertSizeBytes)

        var attribOffset: Int = 0
        for (i in signature.indices) {
            GL46.glEnableVertexArrayAttrib(vaoPid, i)
            GL46.glVertexArrayAttribFormat(
                vaoPid, i, signature[i], Type.FLOAT.value,
                false, attribOffset
            )
            GL46.glVertexArrayAttribBinding(vaoPid,i, bindIdx)
            attribOffset += signature[i] * Float.BYTES
        }


//        GL46.glBindVertexArray(0)
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    enum class CullMode(val value: Int) {
        FRONT(GL11.GL_FRONT),
        BACK(GL11.GL_BACK),
        DISABLED(GL11.GL_NONE);
    }

    // SHOULDN'T BE HERE ?
    enum class PrimitiveType(val value: Int) {
        TRIANGLES_STRIP(GL11.GL_TRIANGLE_STRIP),
        TRIANGLES_STRIP_ADJACENCY(GL_TRIANGLE_STRIP_ADJACENCY),
        TRIANGLES(GL11.GL_TRIANGLES),
        TRIANGLES_ADJACENCY(GL_TRIANGLES_ADJACENCY),
        TRIANGLE_FAN(GL_TRIANGLE_FAN),
        LINES(GL_LINES),
        LINES_LOOP(GL_LINE_LOOP),
        LINES_STRIP(GL_LINE_STRIP),
        POINTS(GL11.GL_POINTS),
        PATCHES(GL_PATCHES);
    }

    fun render(
        _primitiveType: PrimitiveType? = null,
        _culling: CullMode? = null,
    ) {
//        val oldCulling = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE)
        if (_culling != null)
            GL46.glCullFace(_culling.value)

        GL46.glBindVertexArray(vaoPid)
        if (_primitiveType != null)
            GL46.glDrawArrays(_primitiveType.value, 0, totalVertCnt)
        else
            GL46.glDrawArrays(primitiveType.value, 0, totalVertCnt)

        GL46.glBindVertexArray(0)
//        GL46.glCullFace(oldCulling)
    }


}