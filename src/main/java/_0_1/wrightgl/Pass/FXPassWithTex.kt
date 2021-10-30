package _0_1.wrightgl.Pass

import _0_1.main.Glob
import _0_1.math.vector.IVec2
import _0_1.wrightgl.AbstractUniformsContainer
import _0_1.wrightgl.buffer.StorageBuffer
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FBPingPong
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgFX
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11
import java.util.function.Consumer

class FXPassWithTex private constructor() : AbstractPass(), AbstractUniformsContainer {
    override var uniformNumbers: HashMap<String, Any> = HashMap()
    override var uniformTextures: HashMap<String, Texture> = HashMap()
    override var uniformImages: HashMap<String, Texture> = HashMap()
    override var boundSSBOs: HashMap<Int, StorageBuffer> = HashMap()

    lateinit var fb: FB
        private set


    constructor(
        _fileNameFrag: String,
        _folderPath: String = Glob.engine.fileSystem.sketchResourcesFolder,
        _resolution: IVec2 = Glob.engine.res.copy(),
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
        _resolution: IVec2 = Glob.engine.res.copy(),
        _isPingPong: Boolean = false
    ) : this() {
        fb = if (_isPingPong)
            FBPingPong(_width = _resolution.x, _height = _resolution.y)
        else
            FB(_width = _resolution.x, _height = _resolution.y)
    }


//    private fun addUniforms(){
//
//    }

    // TODO
    // TOUCHES GL_DEPTH_TEST STATE!!
    fun run(
        _inputFramebuffer: FB,
        cb: Consumer<FXPassWithTex>? = null
    ) {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        shaderProgram.use()
        cb?.accept(this)
        if (fb is FBPingPong)
            (fb as FBPingPong).backBuffer.setUniformTextures("s_PrevFrame")
        _inputFramebuffer.setUniformTextures("s_Input")
        FB.bind(FB.Target.DRAW, fb)
        setCurrObjectUniforms()
        VB.quadVB.render()
    }
}