package _0_1.wrightgl.thing

import _0_1.math.Mat4
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgRender

class ThingInstanced private constructor(
    shaderProgram: ProgRender,
    vertexBuffer: VB,
): Thing(
    shaderProgram,
    vertexBuffer
){
    override var uniformNumbers: HashMap<String, Any> = HashMap()
    override var uniformTextures: HashMap<String, Texture> = HashMap()
    override var uniformImages: HashMap<String, Texture> = HashMap()
    override var boundSSBOs: HashMap<Int, StorageBuffer> = HashMap()

    var instanceCnt = 0
        set(v){
            // TODO, RESIZE BUFFER
            field = v
        }
    lateinit var instancesBuffer: StorageBuffer
        private set

    constructor(
        _shaderProgram: ProgRender,
        _vertexBuffer: VB,
        _instanceCnt: Int = 100,
        _instanceSignature: IntArray = intArrayOf(3,3)
    ) : this(_shaderProgram, _vertexBuffer) {
        instanceCnt = _instanceCnt
        instancesBuffer = StorageBuffer(
            _instanceSignature,
            instanceCnt
        )
        setBoundSSBO(0,instancesBuffer)

        shaderProgram = _shaderProgram
        vertexBuffers.add(_vertexBuffer)
        modelMatrix = Mat4.identityMatrix
    }


    init{
//        instancesBuffer
    }

}