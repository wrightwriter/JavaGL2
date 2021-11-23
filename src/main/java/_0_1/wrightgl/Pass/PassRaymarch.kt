package _0_1.wrightgl.Pass

import _0_1.main.Global
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.shader.ProgRaymarch
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11
import java.util.function.Consumer

class PassRaymarch protected constructor(): PassFX() {

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

//    // TODO
//    // TOUCHES GL_DEPTH_TEST STATE!!
//    override fun run(
//        _outputFramebuffer: FB,
//        _depthTest: Boolean,
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