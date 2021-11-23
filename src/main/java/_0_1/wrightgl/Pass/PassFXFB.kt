package _0_1.wrightgl.Pass

import _0_1.engine.Constants
import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FBPingPong
import _0_1.wrightgl.shader.ProgFX
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11
import java.util.function.Consumer

open class PassFXFB protected constructor() : AbstractPass() {

    lateinit var fb: FB
        protected set

    constructor(
        _fileNameFrag: String,
        _folderPath: String = Global.engine.fileSystem.sketchResourcesFolder,
        _resolution: IVec2 = Global.engine.res.copy(),
        _isPingPong: Boolean = false
    ) : this() {
        shaderProgram = ProgFX(
            _fileNameFrag,
            _folderPath
        )
        fb = if (_isPingPong)
            FBPingPong(_width = _resolution.x, _height = _resolution.y)
        else
            FB(_width = _resolution.x, _height = _resolution.y)
    }

    constructor(
        _shaderProgram: ProgRender,
        _resolution: IVec2 = Global.engine.res.copy(),
        _isPingPong: Boolean = false
    ) : this() {
        shaderProgram = _shaderProgram
        fb = if (_isPingPong)
            FBPingPong(_width = _resolution.x, _height = _resolution.y)
        else
            FB(_width = _resolution.x, _height = _resolution.y)
    }


    // TODO
    // TOUCHES GL_DEPTH_TEST STATE!!
    open fun run(
        _inputFramebuffer: FB,
        _depthTest: Boolean = false,
        cb: Consumer<PassFXFB>? = null
    ) {
        if (_depthTest == false)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
        else
            GL11.glEnable(GL11.GL_DEPTH_TEST)

        shaderProgram.use()
        cb?.accept(this)

        if (fb is FBPingPong)
            (fb as FBPingPong).backBuffer.setFBTexturesAsUniforms(Constants.PrevFrameUniformName)

        _inputFramebuffer.setFBTexturesAsUniforms(Constants.InputFBUniformName)

        FB.bind(FB.Target.DRAW, fb)
        setCurrObjectUniforms()
        VB.quadVB.render()
    }

}