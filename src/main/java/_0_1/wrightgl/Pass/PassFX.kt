package _0_1.wrightgl.Pass

import _0_1.engine.Constants
import _0_1.main.Global
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.shader.ProgFX
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11.*
import java.util.function.Consumer

open class PassFX protected constructor() : AbstractPass(){
    constructor(
        _fileNameFrag: String,
        _folderPath: String = Global.engine.fileSystem.sketchResourcesFolder
    ) : this() {
        shaderProgram = ProgFX(
            _fileNameFrag,
            _folderPath
        )
    }

    constructor(_shaderProgram: ProgRender) : this() {
        shaderProgram = _shaderProgram
    }

    fun attachFB(): PassFXFB{
        // Kill me.
        val newPass = object : PassFXFB( shaderProgram as ProgRender){
            init{
            }
        }
        newPass.uniformNumbers = this.uniformNumbers
        newPass.uniformTextures = this.uniformTextures
        newPass.uniformImages = this.uniformImages
        newPass.boundSSBOs = this.boundSSBOs
        return newPass
    }

    open fun run(
        _outputFramebuffer: FB = Global.engine.wgl.currDrawFB,
        _depthTest: Boolean = false,
        cb: Consumer<PassFX>? = null
    ) {
        if (_depthTest == false)
            glDisable(GL_DEPTH_TEST)
        else
            glEnable(GL_DEPTH_TEST)

        shaderProgram.use()
        cb?.accept(this)
        FB.bind(FB.Target.DRAW, _outputFramebuffer)
        setCurrObjectUniforms()
        VB.quadVB.render()
    }

    // TODO
    // TOUCHES GL_DEPTH_TEST STATE!!
    open fun run(
        _inputFramebuffer: FB,
        _outputFramebuffer: FB,
        _depthTest: Boolean = false,
        cb: Consumer<PassFX>? = null
    ) {
        if (_depthTest == false)
            glDisable(GL_DEPTH_TEST)
        else
            glEnable(GL_DEPTH_TEST)
        shaderProgram.use()
        cb?.accept(this)
        _inputFramebuffer.setFBTexturesAsUniforms(Constants.InputFBUniformName)
        FB.bind(FB.Target.DRAW, _outputFramebuffer)
        setCurrObjectUniforms()
        VB.quadVB.render(

        )
    }
}


