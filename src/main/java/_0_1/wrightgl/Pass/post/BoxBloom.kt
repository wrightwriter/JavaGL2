package _0_1.wrightgl.Pass.post

import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgFX
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.*
import java.util.function.Consumer

class BoxBloom constructor(
    _resolution: IVec2 = Global.engine.res.copy(),
    val iterationCnt: Int = 6
): PassFXFB() {
    var uThreshold = UniformFloat("uThreshold", 0.5f, this)
    var uRamp = UniformFloat("uRamp", 0.1f, this)
    var uAmount = UniformFloat("uAmount", 0.1f, this)

    private val fbs = ArrayList<FB>();

    init{
        name = "BoxBloom"

        shaderProgram = ProgFX(
            "FX/boxblur.fp",
            Global.engine.fileSystem.globalResourcesFolder
        )

        fb = FB(_width = _resolution.x, _height = _resolution.y, 1)

        var res = _resolution
        for (i in 0 until iterationCnt){
            res /= 2;
            fbs.add(FB(res.x, res.y, _name = "BoxBloom FB " + i.toString()))
//            if (i != iterations - 1)
//                fbs.add(FB(res.x, res.y))
        }
    }

    override fun run(_inputFramebuffer: FB, _depthTest: Boolean, cb: Consumer<PassFXFB>?) {
        super.run(_inputFramebuffer, _depthTest, cb)
    }

    fun run(
        _inputTexture: Texture,
        _depthTest: Boolean,
        cb: Consumer<PassFXFB>?
    ) {
        GL11.glDisable(GL11.GL_DEPTH_TEST)

        shaderProgram.use()
        setCurrObjectUniforms()
        Global.engine.wgl.setUniform("uIterationCnt", iterationCnt)


        Global.engine.wgl.setUniform("uIsDownsampling", true)
        Global.engine.wgl.setUniform("uIsUpsampling", false)
        for (i in 0 until iterationCnt){
            Global.engine.wgl.setUniform("uCurrIter", i)
            glDisable(GL_BLEND)
            if (i == 0)
                _inputTexture.setUniform("s_InputTex")
            else
                fbs[i-1].textures[0].setUniform("s_InputTex")
            FB.bind(FB.Target.DRAW, fbs[i])
            VB.quadVB.render()
        }


        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE,GL_ONE)

        Global.engine.wgl.setUniform("uIsDownsampling", false)
        Global.engine.wgl.setUniform("uIsUpsampling", true)
        for (i in 0 until iterationCnt - 1){
            Global.engine.wgl.setUniform("uCurrIter", iterationCnt + i)
            fbs[ iterationCnt - 1 - i ].textures[0].setUniform("s_InputTex")
            FB.bind(FB.Target.DRAW, fbs[iterationCnt - 2 - i])
            VB.quadVB.render()
        }


        Global.engine.wgl.setUniform("uCurrIter", iterationCnt*2 - 1)
        glDisable(GL_BLEND)
        FB.bind(FB.Target.DRAW, fb)

        _inputTexture.setUniform("s_OrigTex")
        fbs[0].textures[0].setUniform("s_InputTex")
        VB.quadVB.render()

        // Reset blend mode
        if (Global.engine.wgl.blendEnabled)
            Global.engine.wgl.enableBlend(Global.engine.wgl.currBlendFunc[0], Global.engine.wgl.currBlendFunc[1])
        else
            Global.engine.wgl.disableBlend()
    }
}