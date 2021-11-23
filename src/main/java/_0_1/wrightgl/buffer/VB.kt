package _0_1.wrightgl.buffer

import _0_1.wrightgl.Model
import io.github.jdiemke.triangulation.DelaunayTriangulator
import io.github.jdiemke.triangulation.Vector2D
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY
import org.lwjgl.opengl.GL32.GL_TRIANGLE_STRIP_ADJACENCY
import org.lwjgl.opengl.GL40.GL_PATCHES
import org.poly2tri.Poly2Tri
import org.poly2tri.geometry.polygon.Polygon
import org.poly2tri.geometry.polygon.PolygonPoint
import org.tinfour.common.Vertex
import org.tinfour.voronoi.BoundedVoronoiBuildOptions
import org.tinfour.voronoi.BoundedVoronoiDiagram
import java.nio.FloatBuffer
import java.util.ArrayList

open class VB protected constructor( ): AbstractBuffer() {
    var vaoPid: Int = 0
        protected set
//    var data: FloatBuffer? = null
    var primitiveType: PrimitiveType = PrimitiveType.TRIANGLES
    var mapped: Boolean = true
        protected set




    var gpuBuff: FloatBuffer? = null

//    val flags = GL46.GL_MAP_READ_BIT  or GL46.GL_MAP_PERSISTENT_BIT or GL_MAP_FLUSH_EXPLICIT_BIT
    open var flags = 0
        protected set

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

    companion object {
        lateinit var quadVB: VB
            internal set
//        lateinit var singlePointVB: VB
//            internal set
    }

    constructor(
        _geometry: FloatArray,
        _signature: IntArray,
        _primitiveType: PrimitiveType = PrimitiveType.TRIANGLES,
        _mapped: Boolean = false,
        _flags: Int = GL46.GL_MAP_WRITE_BIT or GL46.GL_MAP_READ_BIT or GL46.GL_MAP_PERSISTENT_BIT or GL46.GL_MAP_COHERENT_BIT,
    ) : this() {
        signature = _signature
        data = _geometry.clone()

        singleVertElementsCnt = 0
        for (j in signature) {
            singleVertElementsCnt += j
        }

        totalVertCnt = data.size / singleVertElementsCnt

        singleVertSizeBytes  = singleVertElementsCnt * Float.SIZE_BYTES
        totalSizeBytes = singleVertSizeBytes * totalVertCnt


        primitiveType = _primitiveType
        createVBOandVAO( )


        if (_mapped){
            flags = _flags
            mapGpuBuff(
                totalVertCnt,
                flags
            )
        }

    }

    protected fun createVBOandVAO( ){
        pid = GL46.glCreateBuffers()

        vaoPid = GL46.glCreateVertexArrays()
        GL46.glNamedBufferStorage(pid, data, GL46.GL_DYNAMIC_STORAGE_BIT or flags )

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
            attribOffset += signature[i] * Float.SIZE_BYTES
        }
    }

    protected fun mapGpuBuff(
        vertCnt: Int,
        _flags: Int,
    ){

        val buffByteSz = vertCnt*singleVertSizeBytes
        gpuBuff = GL46.glMapNamedBufferRange(
            pid,
            0,
            buffByteSz.toLong(),
            _flags
        )!!.asFloatBuffer()
        // -- DONT NEED THESE, TEST -- //
        GL45.glFlushMappedNamedBufferRange(pid, 0, buffByteSz.toLong())
        GL42.glMemoryBarrier(GL44.GL_CLIENT_MAPPED_BUFFER_BARRIER_BIT)
        GL42.glMemoryBarrier(GL42.GL_ALL_BARRIER_BITS)


        if(gpuBuff== null){
            print("aaa")
        }
        val err = GL11.glGetError()
        if(err != GL46.GL_NO_ERROR){
            print("aaaa")
        }

        mapped = true
    }


    open fun render(
        _primitiveType: PrimitiveType? = null,
        _culling: CullMode? = null,
        _instanceCnt: Int = 1,
    ) {

        if (_culling == CullMode.DISABLED)
            GL46.glDisable(GL_CULL_FACE)
        else{
            GL46.glEnable(GL_CULL_FACE)
            if (_culling != null)
                GL46.glCullFace(_culling.value)

        }

        GL46.glBindVertexArray(vaoPid)

        var primitives = primitiveType
        if (_primitiveType != null )
            primitives = _primitiveType

        if (_instanceCnt > 1)
            GL46.glDrawArraysInstanced(primitives.value, 0, totalVertCnt, _instanceCnt)
        else
            GL46.glDrawArrays(primitives.value, 0, totalVertCnt)

        GL46.glBindVertexArray(0)
    }


}