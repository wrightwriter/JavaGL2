package _0_1.wrightgl.thing

import _0_1.main.Global
import _0_1.math.Mat4
import _0_1.wrightgl.WrightGL
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgRender
import java.util.function.Consumer

class ThingInstanced private constructor(
    shaderProgram: ProgRender,
    vertexBuffer: VB,
): Thing(
    shaderProgram,
    vertexBuffer
){

    var instancesCnt = 0
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
        instancesCnt = _instanceCnt
        instancesBuffer = StorageBuffer(
            _instanceSignature,
            instancesCnt
        )

        bindSSBOToIdx(0,instancesBuffer)

        shaderProgram = _shaderProgram
        vertexBuffers.add(_vertexBuffer)
        modelMatrix = Mat4.identityMatrix
    }

    override fun render(
        _program: ProgRender,
        _cb: Consumer<Thing>?,
        _primitiveType: VB.PrimitiveType?,
        _culling: VB.CullMode?,
    ) {
        // Shader
        _program.use()

        _cb?.accept(this)

        setCurrObjectUniforms()
        // Update model matrix
        if (callback != null) callback!!(this)

        // Uniforms
        Global.engine.wgl.setUniform("M", modelMatrix)

        // Render
        for (buffer in vertexBuffers) {
            buffer.render(
                _primitiveType,
                _culling,
                instancesCnt
            )
        }

        for (model in models) {
            model.render(
                _primitiveType,
                _culling,
            )
        }
    }

    init{
//        instancesBuffer
    }

}