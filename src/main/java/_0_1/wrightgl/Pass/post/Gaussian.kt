package _0_1.wrightgl.Pass.post

import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.math.vector.Vec2
import _0_1.wrightgl.Pass.PassFX
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FBPingPong
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgFX
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.glDrawBuffers
import java.util.function.Consumer

class Gaussian constructor(
    _resolution: IVec2 = Global.engine.res.copy(),
    _isPingPong: Boolean = false
): PassFXFB() {
    var window = 5
        set(v){
            field = v
            setUniform("window", window)
        }
    var sigma= 1.0f
        set(v){
            field = v
            setUniform("sigma", sigma)
        }
    var spread = 1.0f
        set(v){
            field = v
            setUniform("spread", spread)
        }
    var gain = 1.0f
        set(v){
            field = v
            setUniform("gain", gain)
        }
    init{
        shaderProgram = ProgFX(
            "FX/gaussian.fp",
            Global.engine.fileSystem.globalResourcesFolder
        )

        window = 5
        sigma= 1.0f
        spread = 1.0f
        gain = 1.0f

        fb = FBPingPong(_width = _resolution.x, _height = _resolution.y, 2)


        (fb as FBPingPong).ping()



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
        _inputTexture.setUniform("s_InputTex")

        Global.engine.wgl.setUniform("uGuassianPassNr", 0)

//        FB.bind(FB.Target.DRAW, fb)
        FB.bind(FB.Target.DRAW, fb)
        glDrawBuffers(intArrayOf(0))

        setCurrObjectUniforms()
        VB.quadVB.render()
    }
}