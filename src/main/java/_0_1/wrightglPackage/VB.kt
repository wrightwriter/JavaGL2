package _0_1.wrightglPackage

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL45
import java.lang.Float
import java.nio.FloatBuffer

class VB(_geometry: FloatArray, _signature: IntArray, _primitiveType: PrimitiveType?) {
    var pid: Int
    var name: String? = null
    var data: FloatBuffer? = null
    var type: Type
    var culling = VertexCulling.BACK
    var primitiveType = PrimitiveType.TRIANGLES
    var signature: IntArray
    var vertSize: Int
    var vertCnt: Int

    enum class VertexCulling(val value: Int) {
        FRONT(GL11.GL_FRONT), BACK(GL11.GL_BACK), DISABLED(GL11.GL_NONE);
    }

    enum class Type(val value: Int) {
        FLOAT(GL11.GL_FLOAT);
    }

    // SHOULDN'T BE HERE
    enum class PrimitiveType(val value: Int) {
        TRIANGLES_STRIP(GL11.GL_TRIANGLE_STRIP), TRIANGLES(GL11.GL_TRIANGLES), POINTS(GL11.GL_TRIANGLES);
    }

    fun render() {
        val oldCulling = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE)
        GL11.glCullFace(culling.value)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pid)
        for (i in signature.indices) {
            GL20.glVertexAttribPointer(
                i, signature[i], Type.FLOAT.value,
                false, vertSize * Float.BYTES, 0
            )
            GL20.glEnableVertexAttribArray(i)
        }
        GL11.glDrawArrays(primitiveType.value, 0, vertCnt)
        GL11.glCullFace(oldCulling)
    }

    init {
        primitiveType = _primitiveType ?: primitiveType
        signature = _signature
        vertSize = 0
        for (j in signature) {
            vertSize += j
        }
        vertCnt = _geometry.size / vertSize
        type = Type.FLOAT
        pid = GL45.glCreateBuffers() // gl.glGenBuffers(1, vertexBuffer);

        // Create vertex buffer.
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pid)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, _geometry, GL15.GL_STATIC_DRAW)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }
}