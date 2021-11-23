package _0_1.wrightgl.Pass

import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FBPingPong
import _0_1.wrightgl.shader.ProgFX
import _0_1.wrightgl.shader.ProgRaymarch
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11
import java.util.function.Consumer

class PassRaymarchFB protected constructor(): PassFXFB() {

    constructor(
        _fileNameFrag: String,
        _folderPath: String = Global.engine.fileSystem.sketchResourcesFolder,
        _resolution: IVec2 = Global.engine.res.copy(),
        _isPingPong: Boolean = false
    ) : this() {
        shaderProgram = ProgRaymarch(
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
    ) : this(){
        shaderProgram = _shaderProgram
        fb = if (_isPingPong)
            FBPingPong(_width = _resolution.x, _height = _resolution.y)
        else
            FB(_width = _resolution.x, _height = _resolution.y)
    }


//    // TODO
//    // TOUCHES GL_DEPTH_TEST STATE!!
//    override fun run(
//        _outputFramebuffer: FB ,
//        _depthTest: Boolean ,
//        cb: Consumer<PassFX>?
//    ) {
//        super.run(_outputFramebuffer, _depthTest, cb)
//    }
//
//    // TODO
//    // TOUCHES GL_DEPTH_TEST STATE!!
//    override fun run(
//        _inputFramebuffer: FB,
//        _outputFramebuffer: FB,
//        _depthTest: Boolean,
//        cb: Consumer<PassFX>?
//    ) {
//        super.run(_inputFramebuffer, _outputFramebuffer, _depthTest, cb)
//    }
}