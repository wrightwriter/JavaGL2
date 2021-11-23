package _0_1.wrightgl.buffer

import _0_1.wrightgl.Model
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL46

class VBIndexed private constructor(): VB(){

    var vertexBufferPid = 0
        private set
    var normalBufferPid = 0
        private set
    var texCoordBufferPid = 0
        private set
    var coloursBufferPid = 0
        private set
    var indicesBufferPid = 0
        private set
    constructor(
        _mesh: Model.Mesh,
        _primitiveType: VB.PrimitiveType = VB.PrimitiveType.TRIANGLES_STRIP,
    ) : this() {
//        data = _geometry.clone()
        vaoPid = GL46.glCreateVertexArrays()

        totalVertCnt = _mesh.elementCount

        singleVertSizeBytes  = singleVertElementsCnt * Float.SIZE_BYTES
        totalSizeBytes = singleVertSizeBytes * totalVertCnt


        primitiveType = _primitiveType

        val tempSignature = arrayListOf<Int>()
        if(_mesh.posList.size > 0){
            tempSignature.add(3)
            vertexBufferPid = makeVertexBuffer(_mesh.posList, 3, 0, 0)
        }
        if( _mesh.normalsList.size > 0) {
            tempSignature.add(3)
            normalBufferPid = makeVertexBuffer(_mesh.normalsList, 3, 1, 1)
        }
        if(_mesh.texCoordList.size > 0){
            tempSignature.add(2)
            texCoordBufferPid = makeVertexBuffer(_mesh.texCoordList, 2, 2, 2)
        }
        if(_mesh.coloursList.size > 0){
            tempSignature.add(4)
            coloursBufferPid = makeVertexBuffer(_mesh.coloursList, 2, 3, 3)
        }
        signature = tempSignature.toIntArray()

        indicesBufferPid = GL46.glCreateBuffers()
        GL46.glNamedBufferStorage(indicesBufferPid, _mesh.indicesList.toIntArray(),GL46.GL_DYNAMIC_STORAGE_BIT)
        GL46.glVertexArrayElementBuffer(vaoPid, indicesBufferPid)


    }

    constructor(
        _geometry: FloatArray,
        _indices: IntArray,
        _signature: IntArray,
        _primitiveType: VB.PrimitiveType = VB.PrimitiveType.TRIANGLES,
        _mapped: Boolean = false,
        _flags: Int = GL46.GL_MAP_WRITE_BIT or GL46.GL_MAP_READ_BIT or GL46.GL_MAP_PERSISTENT_BIT or GL46.GL_MAP_COHERENT_BIT,
    ) : this() {
        vaoPid = GL46.glCreateVertexArrays()

        signature = _signature

        singleVertElementsCnt = 0
        for (j in signature) {
            singleVertElementsCnt += j
        }
//        totalVertCnt = _geometry.size / singleVertElementsCnt
        totalVertCnt = _indices.size

        singleVertSizeBytes  = singleVertElementsCnt * Float.SIZE_BYTES
        totalSizeBytes = singleVertSizeBytes * totalVertCnt

        primitiveType = _primitiveType

        indicesBufferPid = GL46.glCreateBuffers()
        GL46.glNamedBufferStorage(indicesBufferPid, _indices,GL46.GL_DYNAMIC_STORAGE_BIT)
        GL46.glVertexArrayElementBuffer(vaoPid, indicesBufferPid)



        primitiveType = _primitiveType

        pid = GL46.glCreateBuffers()

        GL46.glNamedBufferStorage(pid, _geometry, GL46.GL_DYNAMIC_STORAGE_BIT or _flags )

        val bindIdx = 0

        GL46.glVertexArrayVertexBuffer(vaoPid, bindIdx, pid, 0, singleVertSizeBytes)

        var attribOffset: Int = 0
        for (i in signature.indices) {
            GL46.glEnableVertexArrayAttrib(vaoPid, i)
            GL46.glVertexArrayAttribFormat(
                vaoPid, i, signature[i], AbstractBuffer.Type.FLOAT.value,
                false, attribOffset
            )
            GL46.glVertexArrayAttribBinding(vaoPid,i, bindIdx)
            attribOffset += signature[i] * Float.SIZE_BYTES
        }


//        if (_mapped){
//            flags = _flags
//            mapGpuBuff(
//                totalVertCnt,
//                flags
//            )
//        }

    }

    private fun makeVertexBuffer(values: ArrayList<Float>, _singleVertElementsCnt: Int, attribIdx: Int, bindIdx: Int):Int {
        val vboPid = GL46.glCreateBuffers()
        GL46.glNamedBufferStorage(vboPid, values.toFloatArray(), GL46.GL_DYNAMIC_STORAGE_BIT)


        GL46.glVertexArrayVertexBuffer(
            vaoPid, bindIdx, vboPid,
            0, _singleVertElementsCnt*Float.SIZE_BYTES)

        GL46.glEnableVertexArrayAttrib(vaoPid, attribIdx)
        // vertexattribpointer
        GL46.glVertexArrayAttribFormat(
            vaoPid, attribIdx, _singleVertElementsCnt,
            AbstractBuffer.Type.FLOAT.value, false,0
        )
        GL46.glVertexArrayAttribBinding( vaoPid, attribIdx, bindIdx)
        return vboPid
    }

    override fun render(
        _primitiveType: VB.PrimitiveType?,
        _culling: VB.CullMode?,
        _instanceCnt: Int,
    ) {
        if (_culling == CullMode.DISABLED)
            GL46.glDisable(GL11.GL_CULL_FACE)
        else{
            GL46.glEnable(GL11.GL_CULL_FACE)
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
            GL46.glDrawElements(primitives.value, totalVertCnt, GL46.GL_UNSIGNED_INT, 0)
//            GL46.glDrawArrays(primitives.value, 0, totalVertCnt)

        GL46.glBindVertexArray(0)
    }
//        fun bindAsSSBO(){
//            val bindIdx = Global.engine.wgl.currSSBOBindNumber
//            Global.engine.wgl.currSSBOBindNumber++
//            bindAsSSBO(bindIdx)
//        }
//        fun bindAsSSBO(bindIdx: Int = 0){
//            GL46.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindIdx, pid)
//            Global.engine.wgl.setUniform(
//                "u_ssboRes[" + bindIdx.toString() + "]",
//                Vec3(totalVertCnt,1,1).vals
//            )
//
//        }
}