package _0_1.wrightgl.Pass.post

import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FBGBuffer
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.AbstractProgram
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11
import java.lang.Exception
import java.util.function.Consumer

class SSR constructor(
    _resolution: IVec2 = Global.engine.res.copy(),
    // If false, will save an ao texture, which you can use in lighting calcs.
    // If true, will apply to input colour and save to the ao texture.
    _applyToInput: Boolean = true
): PassFXFB(){

//    var uIters = UniformFloat("uIters", 16.0f, this)
    var uIters = UniformFloat("uIters", 16.0f, this)
    var uAoBias = UniformFloat("uAoBias", 0.0f, this)
    var uAoRad = UniformFloat("uAoRad", 0.07f, this)
//    var uAoBool = UniformBoolean("uAoRad", false, this)
//    var uAoInt = UniformInt("uAoRad", 1, this)

    override var shaderProgram: AbstractProgram = ProgRender(
        "quad.vp",
        "FX/ssr.fp",
        _folderPath = Global.engine.fileSystem.globalResourcesFolder
    )

    init{
        name = "SSR"

        fb = FB(_width = _resolution.x, _height = _resolution.y, _name = "SSR")
    }

    override fun run(
        _inputFramebuffer: FB,
        _depthTest: Boolean,
        cb: Consumer<PassFXFB>?
    ) {
        if (_inputFramebuffer !is FBGBuffer)
            throw Exception("Need a GBuffer.")
        else {
            val gBuffer = _inputFramebuffer as FBGBuffer
            run(
                _colorTexture = gBuffer.albedoTexture,
                _depthTexture = gBuffer.depthTexture!!,
                _positionTexture = gBuffer.positionTexture,
                _normalsTexture = gBuffer.normalsTexture,
            )
        }

    }

    fun run(
        _colorTexture: Texture,
        _depthTexture: Texture,
        _positionTexture: Texture,
        _normalsTexture: Texture,
    ) {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        shaderProgram.use()


        _colorTexture.setUniform("s_InputColTex")
        _depthTexture.setUniform("s_InputDepthTex")
        _positionTexture.setUniform("s_InputPositionTex")
        _normalsTexture.setUniform("s_InputNormalsTex")

        fb.bindDraw()
        setCurrObjectUniforms()
        VB.quadVB.render()
    }

}