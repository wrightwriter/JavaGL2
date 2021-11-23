package sketches.outlines

//import _0_1.wrightglPackage.WrightGL.updateMatrices
//import _0_1.wrightglPackage.WrightGL.FB.clear

import _0_1.engine.Engine
import _0_1.engine.Sketch
import _0_1.engine.gui.GUISliderFloat
import _0_1.engine.gui.GUISliderVec3Infinite
import _0_1.math.vector.Vec3
import _0_1.wrightgl.CameraPilot
import _0_1.wrightgl.Model
import _0_1.wrightgl.Pass.PassFX
import _0_1.wrightgl.Pass.post.*
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FB.Companion.bind
import _0_1.wrightgl.fb.FB.Companion.defaultFB
import _0_1.wrightgl.fb.FBGBuffer
import _0_1.wrightgl.shader.ProgRender
import _0_1.wrightgl.thing.Thing
import org.lwjgl.opengl.GL32.*


//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class SketchOutlines(
    engine: Engine
) : Sketch(engine) {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val engineSettings = Engine.Settings()
            engineSettings.res.x = 1200
            engineSettings.res.y = 768
            engineSettings.renderdocEnabled = true
            engineSettings.openGLDebugEnabled = true
            engineSettings.nanoVGEnabled = false
            engineSettings.liveShaderReloading = true
            Engine.startSketch(SketchOutlines.javaClass, engineSettings)
        }
    }
    // ---------------- GUI ---------------- //
    var cameraRotGUI = GUISliderFloat("Camera Rotation")
    var cameraPosGUI = GUISliderVec3Infinite(
        "Camera Position",
        Vec3(0.0f,0.0f,-5.0f)
    )
    var cameraLookAtGUI = GUISliderVec3Infinite(
        "Camera Look At",
        Vec3(0.0f,0.0f,0.0f)
    )


    var drawFB: FB = FB(hasDepth = true)
    var deferredFB: FB = FBGBuffer()


    val pointsProgram = ProgRender( "line.vp", "points.fp", "points.gp")
    val lineProgram = ProgRender( "line.vp", "line.fp", "lineStrip.gp" )
    val polyProgram = ProgRender( "cube.vp", "cube.fp").createDeferredProgramFromThis()


    val cubeModelThing = Thing(
//        ProgRender("cubeModel.vp", "cubeModel.fp"),
        lineProgram,
        Model("cubeb.obj")
    )


    var fxaaPass = FXAA()
    var dofPass  = DOFTeardown()
    var ssaoPass = SSAO()
    var postPass = PassFX("post.fp")

//    var focusLen = GUISliderFloatInfinite(
//        "Focus len",
//        dofPass.uFocusPoint
//    )
//    var focusSz = GUISliderFloatInfinite(
//        "Focus width",
//        dofPass.uFocusScale
//    )
//    var maxBlurSize = GUISliderFloatInfinite(
//        "Max blur size",
//        dofPass.uMaxBlurSize
//    )
//    var radScale = GUISliderFloatInfinite(
//        "Rad Scale",
//        dofPass.uRadScale
//    )


    override fun setup() {
        dofPass.exposeUniformsToGUI()
        camera = CameraPilot()
    }
    override fun display() {

//        camera.eyePos = cameraPosGUI.get()
//        (camera as CameraLookAt).lookAt = cameraLookAtGUI.get()

//        dofPass.uFocusPoint = focusLen.get()
//        dofPass.uFocusScale = focusSz.get()
//        dofPass.uMaxBlurSize= maxBlurSize.get()
//        dofPass.uRadScale = radScale.get()
//        glEnable(GL_POLYGON_OFFSET_FILL);
//        glEnable(GL_POLYGON_OFFSET_LINE);

        var a = 0.0;
        a += 0.0f;
        a += 1;

        // Setting
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDepthFunc(GL_LESS)
        glDisable(GL_STENCIL_TEST)
        glEnable(GL_DEPTH_TEST)


        drawFB.clearAllAttachments()
        bind(FB.Target.DRAW, drawFB)

        // Draw stuff
        cubeModelThing.depthTest = true // inconveneint ADD TO RENDER()

        cubeModelThing.renderOutlines(
            lineProgram,
            _primitiveType = VB.PrimitiveType.LINES_STRIP
        )

        cubeModelThing.render(
            pointsProgram,
            _primitiveType = VB.PrimitiveType.POINTS
        )
//        glPolygonOffset(	1.0f, 1.0f)
        cubeModelThing.render(
            polyProgram,
            _primitiveType = VB.PrimitiveType.TRIANGLES
        )

//        ssaoPass.run(drawFB.textures[0], drawFB.depthTexture!!)
        dofPass.run(ssaoPass.fb.textures[0], drawFB.depthTexture!!)
        fxaaPass.run(dofPass.fb)
        postPass.run(fxaaPass.fb, defaultFB)

//        FB.blit(fxaaPass.fb, defaultFB)

        glFinish()

    }


    override fun drawImGui() {
    }



}