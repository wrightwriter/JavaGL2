package sketches.outlineart

//import _0_1.wrightglPackage.WrightGL.updateMatrices
//import _0_1.wrightglPackage.WrightGL.FB.clear

import _0_1.engine.Engine
import _0_1.engine.Sketch
import _0_1.engine.gui.GUISliderFloat
import _0_1.engine.gui.GUISliderVec3Infinite
import _0_1.math.MathUtils
import _0_1.math.Random
import _0_1.math.vector.Vec3
import _0_1.wrightgl.CameraPilot
import _0_1.wrightgl.Pass.PassCompute
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.Pass.post.*
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.buffer.VBEditable
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FB.Companion.bind
import _0_1.wrightgl.fb.FB.Companion.defaultFB
import _0_1.wrightgl.shader.ProgCompute
import _0_1.wrightgl.shader.ProgRender
import _0_1.wrightgl.thing.Thing
import _0_1.wrightgl.thing.ThingInstanced
import org.lwjgl.opengl.GL32.*
import kotlin.math.cos
import kotlin.math.sin


//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class OutlineArt(
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
            Engine.startSketch(OutlineArt.javaClass, engineSettings)
        }
    }

    // ---------------- Programs ---------------- //
    lateinit var compositeProgram: ProgRender
    lateinit var postProgram: ProgRender


    // ---------------- FRAMEBUFFERS ---------------- //
    var drawFB: FB = FB(hasDepth = true)

    // ---------------- SHADERS ---------------- //
    val lineProgram = ProgRender( "line.vp", "line.fp", "lineStrip.gp" , _deferred = true, _instanced = true)
    val normalsProgram = ProgRender( "line.vp", "line.fp", "normalsStrip.gp" , _deferred = true, _instanced = true)
    val pointsProgram = ProgRender( "line.vp", "points.fp", "points.gp", _instanced = true)


    val polyProgram = ProgRender( "cube.vp", "cube.fp", _deferred = false)

    // ---------------- VERT BUFFERS ---------------- //
    val lineVBEditable = VBEditable( )
    val icosaHedronVB = MathUtils.getIcoSphere(2)
    val polyVBEditable = VBEditable( )
//        val quadVB = VB( Geometry.quadTriangleStrip, intArrayOf(2), VB.PrimitiveType.TRIANGLES_STRIP )

    val particlesPass = PassCompute("particles.cp")

    // ---------------- THINGS ---------------- //

//    val cubeThing = Thing(cubeProgram, cubeVB)
    val lineThing = Thing(lineProgram, lineVBEditable)
    val icosaThing = ThingInstanced(lineProgram, icosaHedronVB, 5)
    val polyThing = Thing(polyProgram, polyVBEditable)

    var dofPassTeardown = DOFTeardown()
    var postPass = PassFXFB("post.fp")

    var fxaaPass = FXAA()
//    var gaussianApproxPass = GaussianApprox()
//    var gaussianPass = Gaussian()
//    var dofPass = DOF()
//    var doOUv - gl_FragCoord.xy/RassScatter = DOFScatter()
//    var dofPassNoGeom = DOFNoGeom()

//    var video = Video()


    override fun setup() {

        lineThing.primitiveType = VB.PrimitiveType.LINES_STRIP
        lineThing.depthTest = true // inconveneint ADD TO RENDER()

        dofPassTeardown.uFocusPoint.set(3.5f)
        dofPassTeardown.uFocusScale.set(3.5f)
        dofPassTeardown.uMaxBlurSize.set(20.0f)
        dofPassTeardown.uRadScale.set(1.2f)



        val iters = 25.0f
        for(i in 0 until iters.toInt()){
            val iter = i.toFloat()
            lineVBEditable.pushOne(
                Random.Companion.XOR.getRandomRange(-1.0f,1.0f),
                Random.Companion.XOR.getRandomRange(-1.0f,1.0f),
                Random.Companion.XOR.getRandomRange(-1.0f,1.0f),
            )
        }

        camera = CameraPilot()
        dofPassTeardown.exposeUniformsToGUI()



//        camera = CameraLookAt()
    }
    override fun display() {
/*
        (camera as CameraLookAt).lookAt = cameraLookAtGUI.get()
*/

        // Setting
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDepthFunc(GL_LESS)
        glDisable(GL_STENCIL_TEST)
        glEnable(GL_DEPTH_TEST)

//        glAlphaFunc ( GL_GREATER, 0.1f ) ;
//        glEnable ( GL_ALPHA_TEST ) ;

//        if(Random.Companion.XOR.getRandom() < 0.8 && time < 2.0f){
//            lineVBEditable.pushOne(
//                Random.Companion.XOR.getRandomRange(-1.0f, 1.0f),
//                Random.Companion.XOR.getRandomRange(-1.0f, 1.0f),
//                Random.Companion.XOR.getRandomRange(-1.0f, 1.0f),
//            )
//        }

        lineThing.position = Vec3(0,0,-7)

        for(i in 0 until lineVBEditable.totalVertCnt - 1){
            val iter = i.toFloat()
//            lineVBEditable.pushOne(
//                cos(iter/iters* MathUtils.Pi),
//                sin(iter/iters*MathUtils.Pi),
//                0.0f,
//            )
            val prevVert = lineVBEditable.getVertAt(
                i,
            )
            lineVBEditable.setAt(
                i,
                sin(iter/lineVBEditable.totalVertCnt*MathUtils.Tau).toFloat()
                    + sin((iter*3.2) + time*3.0f).toFloat()*0.1f
                ,
                cos(iter/lineVBEditable.totalVertCnt*MathUtils.Tau).toFloat()
                    + sin((iter*3.2) + time*3.0f).toFloat()*0.1f
                ,
                sin((iter*2.2) + time*3.0f).toFloat()*0.1f,
//                0.0f,
            )
        }

        lineVBEditable.setAt(
            lineVBEditable.totalVertCnt - 1,
            lineVBEditable.getVertAt(0)
        )


        drawFB.clearAllAttachments()
        bind(FB.Target.DRAW, drawFB)

        // Draw stuff
//        lineThing.render(
//        )

//        lineThing.render(
//            lineProgram,
//            _primitiveType = VB.PrimitiveType.LINES_STRIP,
//            _culling = VB.CullMode.DISABLED
//        )
//
//        lineThing.triangulate()
//
//        lineThing.renderTriangulated(
//            polyProgram,
//            _culling = VB.CullMode.DISABLED
//        )
        particlesPass.dispatch(icosaThing.instancesBuffer)

        icosaThing.render(
            polyProgram,
            _primitiveType = VB.PrimitiveType.TRIANGLES
        )

        icosaThing.render(
            lineProgram,
            _primitiveType = VB.PrimitiveType.LINES_STRIP,
            _culling = VB.CullMode.DISABLED
        )
        icosaThing.render(
            normalsProgram,
            _primitiveType = VB.PrimitiveType.LINES_STRIP,
            _culling = VB.CullMode.DISABLED
        )

//        dofPass.run(drawFB)
        dofPassTeardown.run(drawFB)
        fxaaPass.run(dofPassTeardown.fb)
        postPass.run(fxaaPass.fb)

//        gaussianApproxPass.run(dofPass.fb)
//        gaussianPass.run(dofPass.fb)


        FB.blit(postPass.fb, defaultFB)


        glFinish()

    }


    override fun drawImGui() {
    }



}