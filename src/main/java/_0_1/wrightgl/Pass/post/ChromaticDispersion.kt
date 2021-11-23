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

class ChromaticDispersion constructor(
    _resolution: IVec2 = Global.engine.res.copy(),
): PassFXFB(){

    // Knobs to control the size and the "zoom" amount of the lens
    var uLensRad = UniformFloat("uLensRad", 1.0f, this)


    // pretend that the lens is spherical
    // For the z component, see https://www.desmos.com/calculator/5p0apo0bqm
    // Fudge the z component for stylistic control
    var uLensZoom = UniformFloat("uLensZoom", 8.0f, this)
    var uLensRadFudge = UniformFloat("uLensRadFudge", 0.5f, this)


    override var shaderProgram: AbstractProgram = ProgRender(
        "quad.vp",
        "FX/chromaticdispersion.fp",
        _folderPath = Global.engine.fileSystem.globalResourcesFolder
    )

    init{
        name = "Chromatic Dispersion"
        fb = FB(_width = _resolution.x, _height = _resolution.y, _name = "ChromAb")
    }

    override fun run(
        _inputFramebuffer: FB,
        _depthTest: Boolean,
        cb: Consumer<PassFXFB>?
    ) {
        if (_inputFramebuffer !is FBGBuffer){
            val buffer = _inputFramebuffer
            run(
                _colorTexture= _inputFramebuffer.textures[0],
            )
        } else {
            val gBuffer = _inputFramebuffer as FBGBuffer
            run(
                _colorTexture= gBuffer.albedoTexture,
            )
        }

    }

    fun run(
        _colorTexture: Texture,
    ) {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        shaderProgram.use()
        _colorTexture.setUniform("s_InputColTex")
        fb.bindDraw()
        setCurrObjectUniforms()
        VB.quadVB.render()
    }

}