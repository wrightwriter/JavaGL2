package _0_1.wrightgl.thing

import _0_1.math.Mat4.Companion.identityMatrix
//import _0_1.wrightglPackage.WrightGL.Program.use
//import _0_1.wrightglPackage.WrightGL.setUniform
//import _0_1.wrightglPackage.WrightGL.VB.render
import _0_1.wrightgl.shader.ProgRender
import _0_1.math.Mat4
import _0_1.main.Global
import _0_1.math.vector.Vec3
import _0_1.wrightgl.AbstractUniformsContainer
import _0_1.wrightgl.Model
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.buffer.VBEditable
import org.lwjgl.opengl.GL11.*
import java.util.ArrayList
import java.util.function.Consumer

open class Thing protected constructor(): AbstractUniformsContainer() {

    var vertexBuffers: MutableList<VB> = ArrayList()
        protected set
    var models: MutableList<Model> = ArrayList()
        protected set

    var modelMatrix  = Mat4.identityMatrix
        private set

    var modelMatrixInverseTranspose  = Mat4.identityMatrix
        private set

    var position: Vec3 = Vec3(0,0,0)
        set(v) {
            field = v
            // Cringe
            updateMatrices()
        }
    var rotation: Vec3 = Vec3(0,0,0)
        set(v) {
            field = v
            // Cringe
            updateMatrices()
        }
    var scale: Vec3 = Vec3(1,1,1)
        set(v) {
            field = v
            // Cringe
            updateMatrices()
        }

    protected fun updateMatrices(){
        modelMatrix =  Mat4.identityMatrix
        modelMatrix = modelMatrix.rotateX(rotation.x)
        modelMatrix =  modelMatrix.rotateY(rotation.y)
        modelMatrix =  modelMatrix.rotateZ(rotation.z)
        modelMatrix =  modelMatrix.scale(scale)
        modelMatrix =  modelMatrix.translate(position)
        modelMatrixInverseTranspose = modelMatrix.inverse().transpose()
    }

    var culling: VB.CullMode = VB.CullMode.BACK
    var primitiveType: VB.PrimitiveType? = null
    var depthWrite = true
    var depthTest = true


    var callback: ((Thing) -> Unit)? = null

    lateinit var shaderProgram: ProgRender
    var shaderProgramDeferred: ProgRender? = null
    var shaderProgramShadowMap: ProgRender? = null

    constructor(
        _shaderProgram: ProgRender,
        _vertexBuffer: VB,
        _deferred: Boolean = false,
        _shadowMapped: Boolean= false
    ) : this() {
        updateMatrices()

        shaderProgram = _shaderProgram

        if (_deferred && shaderProgram.isDeferred != true)
            shaderProgram = shaderProgram.createDeferredProgramFromThis()

        if (_shadowMapped)
            shaderProgramShadowMap = shaderProgram.createShadowMapProgramFromThis()

        vertexBuffers.add(_vertexBuffer)
    }
    constructor(
        _shaderProgram: ProgRender,
        _model: Model,
        _deferred: Boolean = false,
        _shadowMapped: Boolean= false
    ) : this() {
        updateMatrices()

        shaderProgram = _shaderProgram
        models.add(_model)
        modelMatrix = identityMatrix

        if (_deferred && shaderProgram.isDeferred != true)
            shaderProgram = shaderProgram.createDeferredProgramFromThis()

        if (_shadowMapped)
            shaderProgramShadowMap = shaderProgram.createShadowMapProgramFromThis()
    }

    protected fun setUpRenderState(
        _program: ProgRender,
        _cb: Consumer<Thing>? = null,
        _primitiveType: VB.PrimitiveType? = VB.PrimitiveType.TRIANGLES,
        _culling: VB.CullMode? = culling,
    ){
        _program.use()

        _cb?.accept(this)

        setCurrObjectUniforms()
        // Update model matrix
        if (callback != null) callback!!(this)

        if (depthTest)
            glEnable( GL_DEPTH_TEST );
        else
            glDisable( GL_DEPTH_TEST );

        // Uniforms
        Global.engine.wgl.setUniform("M", modelMatrix)
        Global.engine.wgl.setUniform("MInverseTranspose", modelMatrixInverseTranspose)

    }

    open fun render(
        _program: ProgRender = shaderProgram,
        _cb: Consumer<Thing>? = null,
        _primitiveType: VB.PrimitiveType? = primitiveType,
        _culling: VB.CullMode? = culling,
        _depthTest: Boolean = depthTest
    ) {
        setUpRenderState(_program, _cb, _primitiveType, _culling)

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

    fun triangulate(){
        for (buffer in vertexBuffers) {
            if (buffer is VBEditable)
                buffer.triangulate()
        }
    }

    open fun renderTriangulated(
        _program: ProgRender,
        _cb: Consumer<Thing>? = null,
        _primitiveType: VB.PrimitiveType? = VB.PrimitiveType.TRIANGLES,
        _culling: VB.CullMode? = culling,
    ) {
        setUpRenderState(_program, _cb, _primitiveType, _culling)

        for (buffer in vertexBuffers) {
            if (buffer is VBEditable){
                (buffer as VBEditable).renderTriangulated(
                    _culling = _culling ?: culling,
                )
            }
        }
    }

    open fun renderOutlines(
        _program: ProgRender,
        _cb: Consumer<Thing>? = null,
        _primitiveType: VB.PrimitiveType? = VB.PrimitiveType.LINES_STRIP,
        _culling: VB.CullMode? = culling,
    ) {
        setUpRenderState(_program, _cb, _primitiveType, _culling)
    // Render
        for (buffer in vertexBuffers) {
            buffer.render(
                _primitiveType,
                _culling ?: culling,
            )
        }
        for (model in models) {
            model.renderOutlines(
                _primitiveType,
                _culling ?: culling,
            )
        }
    }

}