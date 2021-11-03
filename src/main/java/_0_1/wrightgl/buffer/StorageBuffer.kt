package _0_1.wrightgl.buffer

import org.lwjgl.opengl.GL46

class StorageBuffer private constructor(): AbstractBuffer() {

//    lateinit  var signature: IntArray
//        set

    constructor(
        _signature: IntArray,
        _data: FloatArray
    ) : this() {
        signature = _signature
        data = _data.clone()

        singleVertElementsCnt = 0
        for (j in signature) {
            singleVertElementsCnt += j
        }

        totalVertCnt = _data.size / singleVertElementsCnt

        createBuffer()
    }
    constructor(
        _signature: IntArray,
        _vertCnt: Int,
        _initialValue: Float = 0.0f
    ) : this() {
        signature = _signature

        singleVertElementsCnt = 0
        for (j in signature) {
            singleVertElementsCnt += j
        }

        totalVertCnt = _vertCnt

        // Array of zeroes
        data = Array<Float>(_vertCnt){i->_initialValue}.toFloatArray()

        createBuffer()
    }
    private fun createBuffer(){
        pid = GL46.glCreateBuffers()
        GL46.glNamedBufferStorage(pid, data, GL46.GL_DYNAMIC_STORAGE_BIT)
    }
}