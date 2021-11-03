package _0_1.wrightgl.buffer

import org.lwjgl.opengl.*

class VBEditable : VB {
    private constructor(
        _geometry: FloatArray,
        _signature: IntArray,
        _primitiveType: PrimitiveType,
        _mapped: Boolean
    ) : super(_geometry, _signature, _primitiveType, true){ }

    var maxVertCnt: Int = 1000000
        private set


    override var flags = GL46.GL_MAP_WRITE_BIT or GL46.GL_MAP_READ_BIT or GL46.GL_MAP_PERSISTENT_BIT or GL46.GL_MAP_COHERENT_BIT
        protected set


    fun getFloat(idx: Int, offs: Int): Float{
        return gpuBuff!!.get(singleVertElementsCnt*(idx) + offs)
    }
    fun getVertAt(idx: Int){
        val result = FloatArray(singleVertElementsCnt)
        gpuBuff!!.get(singleVertElementsCnt*(idx), result )
    }
    fun setAtOffs(idx: Int, offs: Int,value: Float){
        gpuBuff!!.put(singleVertElementsCnt*(idx) + offs, value)
    }
    fun setAt(idx: Int,value: Float){
        gpuBuff!!.put(singleVertElementsCnt*(idx), value)
    }
    fun setAt(idx: Int,value: FloatArray){
        gpuBuff!!.put(singleVertElementsCnt*idx, value)
    }
    @JvmName("setAt1")
    fun setAt(idx: Int,vararg value: Float){
        gpuBuff!!.put(singleVertElementsCnt*idx, value)
    }
    fun pushOne(vararg value: Float){
        gpuBuff!!.put(singleVertElementsCnt*(totalVertCnt++), value)
    }
    @JvmName("push1")
    fun pushOne(value: FloatArray){
        gpuBuff!!.put(singleVertElementsCnt*(totalVertCnt++), value)
    }
    fun pushMany(vararg value: Float){
        val cnt = value.size/singleVertElementsCnt
        gpuBuff!!.put(singleVertElementsCnt*(totalVertCnt), value)
        totalVertCnt += cnt
    }
    fun increment(value: FloatArray){
        totalVertCnt++
    }

    constructor(
        _geometry: FloatArray,
        _signature: IntArray,
        _primitiveType: PrimitiveType,
    ) : super(_geometry, _signature, _primitiveType, true) {
        maxVertCnt = totalVertCnt
    }


    constructor(
        _initialVertCnt: Int = 0,
        _maxVertCnt: Int = 1000000,
        _signature: IntArray = intArrayOf(3),
        _primitiveType: PrimitiveType = PrimitiveType.TRIANGLES_STRIP,
    ){
        signature = _signature
        totalVertCnt = _initialVertCnt

        singleVertElementsCnt = 0
        for (j in signature) {
            singleVertElementsCnt += j
        }
        singleVertSizeBytes  = singleVertElementsCnt * Float.SIZE_BYTES
        totalSizeBytes = singleVertSizeBytes * totalVertCnt

        data = FloatArray(_maxVertCnt*singleVertElementsCnt){0.0f}

        primitiveType = _primitiveType

        createVBOandVAO( )
        mapGpuBuff(
            maxVertCnt,
            flags
        )

    }
}