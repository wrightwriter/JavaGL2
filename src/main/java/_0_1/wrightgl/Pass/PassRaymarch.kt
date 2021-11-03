package _0_1.wrightgl.Pass

import _0_1.main.Global
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.shader.ProgRaymarch
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11
import java.util.function.Consumer

class PassRaymarch protected constructor(): AbstractPass() {

    constructor(
        _fileNameFrag: String,
        _folderPath: String = Global.engine.fileSystem.sketchResourcesFolder
    ) : this() {
        shaderProgram = ProgRaymarch(
            _fileNameFrag,
            _folderPath
        )
    }

    constructor(_shaderProgram: ProgRender) : this() {
        shaderProgram = _shaderProgram
    }

    // TODO
    // TOUCHES GL_DEPTH_TEST STATE!!
    fun run(
        _outputFramebuffer: FB = Global.engine.wgl.currDrawFB,
        _depthTest: Boolean = true,
        cb: Consumer<PassRaymarch>? = null
    ) {
        if (_depthTest == false)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
        else
            GL11.glEnable(GL11.GL_DEPTH_TEST)

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
        _depthTest: Boolean = true,
        cb: Consumer<PassRaymarch>? = null
    ) {
        if (_depthTest == false)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
        else
            GL11.glEnable(GL11.GL_DEPTH_TEST)
        shaderProgram.use()
        cb?.accept(this)
        _inputFramebuffer.setUniformTextures("s_Input")
        FB.bind(FB.Target.DRAW, _outputFramebuffer)
        setCurrObjectUniforms()
        VB.quadVB.render(

        )
    }
}