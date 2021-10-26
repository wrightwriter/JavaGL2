package _0_1.enginePackage

import _0_1.mathPackage.Mat4.Companion.identityMatrix
//import _0_1.wrightglPackage.WrightGL.Program.use
//import _0_1.wrightglPackage.WrightGL.setUniform
//import _0_1.wrightglPackage.WrightGL.VB.render
import _0_1.wrightglPackage.Program
import _0_1.wrightglPackage.VB
import _0_1.mathPackage.Mat4
import _0_1.mainPackage.Global
import kotlin.jvm.JvmOverloads
import _0_1.wrightglPackage.WrightGL
import java.util.ArrayList
import java.util.function.Consumer

class Thing(var shaderProgram: Program, _vertexBuffer: VB) {
    var vertexBuffers: MutableList<VB> = ArrayList()
    var modelMatrix: Mat4
    var callback: ((Thing) -> Unit)? = null
//        set(_modelMatrixCallback: ((Thing) -> Void)?){
//            field = _modelMatrixCallback
//        }
//        get() = field
//    fun setCallback(_modelMatrixCallback: Consumer<Thing>?) {
//        callback = _modelMatrixCallback
//    }


    @JvmOverloads
    fun render(_program: Program? = null, innerCallback: Consumer<Thing?>? = null) {
        // Shader
        val currProgram = _program ?: shaderProgram
        currProgram.use()
        innerCallback?.accept(this)

        // Update model matrix
        if (callback != null) callback!!(this)

        // Uniforms
        Global.engine!!.wgl.setUniform("M", modelMatrix.vals, WrightGL.UniformType.Matrix)

        // Render
        for (buffer in vertexBuffers) {
            buffer.render()
        }
    }

    init {
        vertexBuffers.add(_vertexBuffer)
        modelMatrix = identityMatrix
    }
}