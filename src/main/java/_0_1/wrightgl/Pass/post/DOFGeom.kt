package _0_1.wrightgl.Pass.post

import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.shader.AbstractProgram
import _0_1.wrightgl.shader.ProgRender
import org.lwjgl.opengl.GL11.*
import java.util.function.Consumer

class DOFGeom constructor(
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
    val pointVB = VB(floatArrayOf(1.0f), intArrayOf(1))

    override var shaderProgram: AbstractProgram = ProgRender(
        "FX/dofGeom.vp",
        "FX/dofGeom.fp",
        "FX/dofGeom.gp",
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
        fb.clearAllAttachments()
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE)
        glEnable(GL_BLEND)
        cb?.accept(this)

        fb.bind( FB.Target.DRAW)
        shaderProgram.use()
        _inputFramebuffer.setFBTexturesAsUniforms()
        setCurrObjectUniforms()
//        VB.quadVB.render(
        pointVB.render(
//            _instanceCnt = fb.textures[0].res.x * fb.textures[0].res.y/25,
            _instanceCnt = fb.textures[0].res.x * fb.textures[0].res.y,
            _primitiveType = VB.PrimitiveType.POINTS
        )
//        pointVB.render(
//            _instanceCnt = fb.textures[0].res.x * fb.textures[0].res.y,
//            _primitiveType = VB.PrimitiveType.POINTS
//        )
    }
}