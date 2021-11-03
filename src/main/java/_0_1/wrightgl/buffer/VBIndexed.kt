package _0_1.wrightgl.buffer

import _0_1.wrightgl.Model
import org.lwjgl.opengl.GL46

class VBIndexed private constructor(){
    var type = AbstractBuffer.Type.FLOAT
    var vaoPid: Int = 0
    var name: String? = null

//        lateinit var data: FloatArray
//            protected set

    var totalVertCnt: Int = 0
        protected set
    var singleVertElementsCnt: Int = 0
        set

    var singleVertSizeBytes: Int = 0
        set
    var totalSizeBytes: Int = 0
        set

    var signature: ArrayList<Int> = ArrayList()
        private set

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
    lateinit var primitiveType: VB.PrimitiveType
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



        if(_mesh.posList.size > 0){
            signature.add(3)
            vertexBufferPid = makeVertexBuffer(_mesh.posList, 3, 0, 0)
        }
        if( _mesh.normalsList.size > 0) {
            signature.add(3)
            normalBufferPid = makeVertexBuffer(_mesh.normalsList, 3, 1, 1)
        }
        if(_mesh.texCoordList.size > 0){
            signature.add(2)
            texCoordBufferPid = makeVertexBuffer(_mesh.texCoordList, 2, 2, 2)
        }
        if(_mesh.coloursList.size > 0){
            signature.add(4)
            coloursBufferPid = makeVertexBuffer(_mesh.coloursList, 2, 3, 3)
        }

        indicesBufferPid = GL46.glCreateBuffers()
        GL46.glNamedBufferStorage(indicesBufferPid, _mesh.indicesList.toIntArray(),GL46.GL_DYNAMIC_STORAGE_BIT)
        GL46.glVertexArrayElementBuffer(vaoPid, indicesBufferPid)


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

    fun render(
        _primitiveType: VB.PrimitiveType? = null,
        _culling: VB.CullMode? = null,
        _instanceCnt: Int = 1,
    ) {
        if (_culling != null)
            GL46.glCullFace(_culling.value)

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