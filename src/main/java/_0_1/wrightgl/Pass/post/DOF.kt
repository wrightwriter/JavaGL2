package _0_1.wrightgl.Pass.post

import _0_1.engine.Constants
import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.shader.ProgFX
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL46
import java.util.function.Consumer

class DOF constructor(
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

    val fbPrepass = FB(_width = _resolution.x, _height = _resolution.y,2, _name = "DOF Prepass")
    val prepassShaderProgram = ProgFX(
        "FX/dofPrepass.fp",
        Global.engine.fileSystem.globalResourcesFolder
    )

    val fbBokehpass = FB(_width = _resolution.x, _height = _resolution.y,2, _name = "DOF Bokehpass")
    val bokehpassShaderProgram = ProgFX(
        "FX/dofBokehpass.fp",
        Global.engine.fileSystem.globalResourcesFolder
    )

    val fbFilterpass = FB(_width = _resolution.x, _height = _resolution.y,2, _name = "DOF Filterpass")
    val filterpassShaderProgram = ProgFX(
        "FX/dofFilterpass.fp",
        Global.engine.fileSystem.globalResourcesFolder
    )


    init{
        uFocusDist = 3.5f
        uFocalLength = 1.2f


        shaderProgram = ProgFX(
            "FX/dof.fp",
            Global.engine.fileSystem.globalResourcesFolder
        )

        fb = FB(_width = _resolution.x, _height = _resolution.y, _name = "DOF")
    }

    override fun run(
        _inputFramebuffer: FB,
        _depthTest: Boolean,
        cb: Consumer<PassFXFB>?
    ) {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_BLEND)

//        fb.clearAllAttachments()
//        fbPrepass.clearAllAttachments()
        cb?.accept(this)

        // ------ PREPASS ------ //
        prepassShaderProgram.use()
        GL46.glViewport(0,0, fbPrepass.textures[0].res.x, fbPrepass.textures[0].res.y)
//        Global.engine.wgl.setUniform("fbRes", )
        fbPrepass.bind(FB.Target.DRAW)
        _inputFramebuffer.setFBTexturesAsUniforms(Constants.InputFBUniformName)
        setCurrObjectUniforms()
        VB.quadVB.render()

        GL46.glViewport(0,0, fb.textures[0].res.x, fb.textures[0].res.y)
        // ------ BOKEH ------ //
        bokehpassShaderProgram.use()
        fbBokehpass.bind(FB.Target.DRAW)
        _inputFramebuffer.setFBTexturesAsUniforms(Constants.InputFBUniformName)
        fbPrepass.setFBTexturesAsUniforms("s_PrepassFB")
        setCurrObjectUniforms()
        VB.quadVB.render()

        // ------ FILTER ------ //
        filterpassShaderProgram.use()
        fbFilterpass.bind(FB.Target.DRAW)
        _inputFramebuffer.setFBTexturesAsUniforms(Constants.InputFBUniformName)
        fbPrepass.setFBTexturesAsUniforms("s_PrepassFB")
        fbBokehpass.setFBTexturesAsUniforms("s_BokehpassFB")
        setCurrObjectUniforms()
        VB.quadVB.render()

        // ------ COMP ------ //
        shaderProgram.use()
        fb.bind(FB.Target.DRAW, )
        fbPrepass.setFBTexturesAsUniforms("s_PrepassFB")
        fbBokehpass.setFBTexturesAsUniforms("s_BokehpassFB")
        fbFilterpass.setFBTexturesAsUniforms("s_FilterpassFB")
        _inputFramebuffer.setFBTexturesAsUniforms(Constants.InputFBUniformName)
        setCurrObjectUniforms()
        VB.quadVB.render()
    }
}