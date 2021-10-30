package _0_1.wrightgl.thing

import _0_1.math.Mat4.Companion.identityMatrix
//import _0_1.wrightglPackage.WrightGL.Program.use
//import _0_1.wrightglPackage.WrightGL.setUniform
//import _0_1.wrightglPackage.WrightGL.VB.render
import _0_1.wrightgl.shader.ProgRender
import _0_1.math.Mat4
import _0_1.main.Glob
import _0_1.wrightgl.AbstractUniformsContainer
import _0_1.wrightgl.WrightGL
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.Texture
import java.util.ArrayList
import java.util.function.Consumer

open class Thing protected constructor(): AbstractUniformsContainer {
    override var uniformNumbers: HashMap<String, Any> = HashMap()
    override var uniformTextures: HashMap<String, Texture> = HashMap()
    override var uniformImages: HashMap<String, Texture> = HashMap()
    override var boundSSBOs: HashMap<Int, StorageBuffer> = HashMap()

    var vertexBuffers: MutableList<VB> = ArrayList()
        protected set
    lateinit var modelMatrix: Mat4

    var culling: VB.CullMode = VB.CullMode.BACK
    var primitiveType: VB.PrimitiveType? = null
//    lateinit var modelMatrix: Mat4



    var callback: ((Thing) -> Unit)? = null

    lateinit var shaderProgram: ProgRender
//        set(_modelMatrixCallback: ((Thing) -> Void)?){
//            field = _modelMatrixCallback
//        }
//        get() = field
//    fun setCallback(_modelMatrixCallback: Consumer<Thing>?) {
//        callback = _modelMatrixCallback
//    }
    constructor(_shaderProgram: ProgRender, _vertexBuffer: VB) : this() {
        shaderProgram = _shaderProgram
        vertexBuffers.add(_vertexBuffer)
        modelMatrix = identityMatrix
    }

    fun render(
        _program: ProgRender = shaderProgram,
        _cb: Consumer<Thing>? = null,
        _primitiveType: VB.PrimitiveType? = primitiveType,
        _culling: VB.CullMode? = culling,
    ) {
        // Shader
        _program.use()

        _cb?.accept(this)

        setCurrObjectUniforms()
        // Update model matrix
        if (callback != null) callback!!(this)

        // Uniforms
        Glob.engine.wgl.setUniform("M", modelMatrix.vals, WrightGL.UniformType.Matrix)

        // Render
        for (buffer in vertexBuffers) {
            buffer.render(
                _primitiveType,
                _culling,
            )
        }
    }

}