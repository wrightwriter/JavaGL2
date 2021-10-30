package _0_1.wrightgl.Pass

import _0_1.main.Glob
import _0_1.wrightgl.AbstractUniformsContainer
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgFX
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11.glDisable
import java.util.function.Consumer

open class FXPass protected constructor() : AbstractPass(), AbstractUniformsContainer {

    override var uniformNumbers: HashMap<String, Any> = HashMap()
    override var uniformTextures: HashMap<String, Texture> = HashMap()
    override var uniformImages: HashMap<String, Texture> = HashMap()
    override var boundSSBOs: HashMap<Int, StorageBuffer> = HashMap()



    constructor(
        _fileNameFrag: String,
        _folderPath: String = Glob.engine.fileSystem.sketchResourcesFolder
    ) : this() {
        shaderProgram = ProgFX(
            _fileNameFrag,
            _folderPath
        )
    }

    constructor(_shaderProgram: ProgRender) : this() {
        shaderProgram = _shaderProgram
    }

    // TODO
    // TOUCHES GL_DEPTH_TEST STATE!!
    open fun run(
        _outputFramebuffer: FB,
        cb: Consumer<FXPass>? = null
    ) {
        glDisable(GL_DEPTH_TEST)
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
        cb: Consumer<FXPass>? = null
    ) {
        glDisable(GL_DEPTH_TEST)
        shaderProgram.use()
        cb?.accept(this)
        _inputFramebuffer.setUniformTextures("s_Input")
        FB.bind(FB.Target.DRAW, _outputFramebuffer)
        setCurrObjectUniforms()
        VB.quadVB.render()
    }
}


