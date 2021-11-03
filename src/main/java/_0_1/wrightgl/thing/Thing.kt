package _0_1.wrightgl.thing

import _0_1.math.Mat4.Companion.identityMatrix
//import _0_1.wrightglPackage.WrightGL.Program.use
//import _0_1.wrightglPackage.WrightGL.setUniform
//import _0_1.wrightglPackage.WrightGL.VB.render
import _0_1.wrightgl.shader.ProgRender
import _0_1.math.Mat4
import _0_1.main.Global
import _0_1.wrightgl.AbstractUniformsContainer
import _0_1.wrightgl.Model
import _0_1.wrightgl.WrightGL
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.Texture
import org.lwjgl.opengl.GL11.*
import java.util.ArrayList
import java.util.function.Consumer

open class Thing protected constructor(): AbstractUniformsContainer() {

    var vertexBuffers: MutableList<VB> = ArrayList()
        protected set
    var models: MutableList<Model> = ArrayList()
        protected set

    lateinit var modelMatrix: Mat4

    var culling: VB.CullMode = VB.CullMode.BACK
    var primitiveType: VB.PrimitiveType? = null
    var depthWrite = true
    var depthTest = true
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
    constructor(_shaderProgram: ProgRender, _model: Model) : this() {
        shaderProgram = _shaderProgram
        models.add(_model)
        modelMatrix = identityMatrix
    }

    open fun render(
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

        if (depthTest)
            glEnable( GL_DEPTH_TEST );
        else
            glDisable( GL_DEPTH_TEST );

//        glEnable(GL_DEPTH_TEST)
//        glDepthFunc(GL_LESS)
//        glEnable( GL_CULL_FACE)

        // Uniforms
        Global.engine.wgl.setUniform("M", modelMatrix)

        // Render
        for (buffer in vertexBuffers) {
            buffer.render(
                _primitiveType,
                _culling ?: culling,
            )
        }
        for (model in models) {
            model.render(
                _primitiveType,
                _culling ?: culling,
            )
        }
    }

}