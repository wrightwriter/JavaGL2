package _0_1.wrightgl.Pass.post

import _0_1.engine.Constants
import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FBPingPong
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.AbstractProgram
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11
import java.lang.Exception
import java.util.function.Consumer

class DOFTeardown constructor(
    _resolution: IVec2 = Global.engine.res.copy(),
): PassFXFB(){

    var uFocusPoint = UniformFloat("uFocusPoint", 3.5f, this)
    var uFocusScale = UniformFloat("uFocusScale", 0.5f, this)
    var uMaxBlurSize = UniformFloat("uMaxBlurSize", 20.0f, this)
    var uRadScale = UniformFloat("uRadScale", 3.2f, this)

    override var shaderProgram: AbstractProgram = ProgRender(
        "quad.vp",
        "FX/dofTeardown.fp",
        _folderPath = Global.engine.fileSystem.globalResourcesFolder
    )

    init{
        name = "Depth of Field"
        fb = FB(_width = _resolution.x, _height = _resolution.y, _name = "DOF")
    }

    override fun run(
        _inputFramebuffer: FB,
        _depthTest: Boolean,
        cb: Consumer<PassFXFB>?
    ) {
        shaderProgram.use()
        Global.engine.wgl.setUniform("uRunningOnFB", 1)
        if (_inputFramebuffer.depthTexture == null)
            throw Exception("No depth texture found")
        super.run(_inputFramebuffer, _depthTest, cb)
    }

    fun run(
        _inputTexture: Texture,
        _depthTexture: Texture,
    ) {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        shaderProgram.use()
        Global.engine.wgl.setUniform("uRunningOnFB", 0)

        _inputTexture.setUniform("s_InputColTex")
        _depthTexture.setUniform("s_InputDepthTex")

//        .setFBTexturesAsUniforms(Constants.inputFB)
        FB.bind(FB.Target.DRAW, fb)
        setCurrObjectUniforms()
        VB.quadVB.render()
    }

}