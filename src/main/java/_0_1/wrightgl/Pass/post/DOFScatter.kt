package _0_1.wrightgl.Pass.post

import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.Pass.PassCompute
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.fb.FB
import java.util.function.Consumer

class DOFScatter constructor(
    _resolution: IVec2 = Global.engine.res.copy(),
): PassFXFB() {

    var uFocusDist = 1.0f
        set(v){
            field = v
            setUniform("uFocusDist", v)
        }
    var uFocalLength = 1.0f
        set(v){
            field = v
            setUniform("uFocalLength", field)
        }

    val computePass = PassCompute(
        "FX/dofScatter.cp",
        _folderPath = Global.engine.fileSystem.globalResourcesFolder
    )

    init{
        uFocusDist = 3.5f
        uFocalLength = 1.2f


        fb = FB(_width = _resolution.x, _height = _resolution.y, _name = "DOF")
    }

    override fun run(
        _inputFramebuffer: FB,
        _depthTest: Boolean,
        cb: Consumer<PassFXFB>?
    ) {
        cb?.accept(this)

        fb.clearAllAttachments()

        computePass.shaderProgram.use()
        computePass.setUniformTexture("s_InputCol", _inputFramebuffer.textures[0])
        computePass.setUniformTexture("s_InputDepth", _inputFramebuffer.depthTexture!!)
        setCurrObjectUniforms()
        // ------ PREPASS ------ //
        computePass.dispatch(
            fb.textures[0],
        )

    }
}