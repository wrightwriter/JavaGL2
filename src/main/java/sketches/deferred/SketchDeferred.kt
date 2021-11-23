package sketches.deferred

//import _0_1.wrightglPackage.WrightGL.updateMatrices
//import _0_1.wrightglPackage.WrightGL.FB.clear

import _0_1.engine.Engine
import _0_1.engine.Sketch
import _0_1.engine.gui.GUISliderFloat
import _0_1.engine.gui.GUISliderFloatInfinite
import _0_1.engine.gui.GUISliderVec3Infinite
import _0_1.math.vector.IVec2
import _0_1.math.vector.Vec3
import _0_1.math.vector.Vec4
import _0_1.wrightgl.CameraPilot
import _0_1.wrightgl.DirectionalLight
import _0_1.wrightgl.Light
import _0_1.wrightgl.Model
import _0_1.wrightgl.Pass.PassFX
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.Pass.post.*
import _0_1.wrightgl.buffer.UBO
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FB.Companion.bind
import _0_1.wrightgl.fb.FB.Companion.defaultFB
import _0_1.wrightgl.fb.FBGBuffer
import _0_1.wrightgl.shader.ProgRender
import _0_1.wrightgl.thing.Thing
import org.lwjgl.opengl.GL32.*
import kotlin.math.sin


//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class SketchDeferred(
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
            Engine.startSketch(SketchDeferred.javaClass, engineSettings)
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
    var deferredFB = FBGBuffer()


    val pointsProgram = ProgRender( "line.vp", "points.fp", "points.gp" , _deferred = true)
    val lineProgram = ProgRender( "line.vp", "line.fp", "lineStrip.gp" , _deferred = true)
    val polyProgram = ProgRender( "cube.vp", "cube.fp", _deferred = true)


    val lightA = DirectionalLight(
        this,
        _position = Vec3(0.6f,4.4f,-1.5f),
        _colour = Vec4(.0f,1.0f,0.2f,0.4f),
        _lookAt = Vec3(0,2,1),
        _res = IVec2(1000,1000)
    )
    val lightB = DirectionalLight(
        this,
        _position = Vec3(-0.5f,4.4f,-1.5f),
        _colour = Vec4(1.0f,0.4f,0.0f,0.5f),
        _lookAt = Vec3(0,2,1),
        _res = IVec2(1000,1000)
    )

    val lightC = DirectionalLight(
        this,
        _position = Vec3(0.2f,3.4f,1.5f),
        _colour = Vec4(.0f,0.4f,0.6f,0.5f),
        _lookAt = Vec3(0,2,1),
        _res = IVec2(1000,1000)
    )

    val cubeModelThing = Thing(
//        ProgRender("cubeModel.vp", "cubeModel.fp"),
        polyProgram,
        Model("cubeb.obj"),
//        Model("sponza.obj"),
        _shadowMapped = true
    )


    var fxaaPass = FXAA()
    var dofPass  = DOFTeardown()
    var ssaoPass = SSAO()
    var ssrPass = SSR()
    var chromabPass = ChromaticDispersion()
    var bloomPass = BoxBloom()
    var postPass = PassFX("post.fp")
    var compositePass = PassFXFB("composite.fp")


    override fun setup() {
        bloomPass.exposeUniformsToGUI()
        ssaoPass.exposeUniformsToGUI()
        ssrPass.exposeUniformsToGUI()
        chromabPass.exposeUniformsToGUI()
        dofPass.exposeUniformsToGUI()

        compositePass.setUniformTexture("s_Depth", deferredFB.depthTexture!!)
        compositePass.setUniformLightTextures(lights)

        cubeModelThing.depthTest = true // inconveneint ADD TO RENDER()
        thingQueue.add(cubeModelThing)

        camera = CameraPilot()
    }
    override fun display() {

//        camera.eyePos = cameraPosGUI.get()
//        (camera as CameraLookAt).lookAt = cameraLookAtGUI.get()



//        lightA.fb.clearDepth()
//        lightA.position = Vec3(0.0f+ sin(time)*2.0f,2.2f ,-1.5f)

        // Setting
        glDepthFunc(GL_LESS)
        glDisable(GL_STENCIL_TEST)
        glEnable(GL_DEPTH_TEST)


        deferredFB.clearAllAttachments()
        deferredFB.bindDraw()

        // Draw stuff


//        cubeModelThing.renderOutlines(
//            lineProgram,
//            _primitiveType = VB.PrimitiveType.LINES_STRIP
//        )
//        cubeModelThing.position = Vec3(0.05f,0.05f,cubeModelThing.position.z + deltaTime);
        cubeModelThing.position = Vec3(0.05f,0.05f,-4.0f);

        cubeModelThing.render(
            pointsProgram,
            _primitiveType = VB.PrimitiveType.POINTS
        )
        cubeModelThing.render(
            polyProgram,
            _primitiveType = VB.PrimitiveType.TRIANGLES
        )

        for(light in lights){
            light.fb.clearDepth()
        }

        for(light in lights){
            for(thing in thingQueue){
                light.run(thing)
            }
        }


        compositePass.run( deferredFB )
        ssrPass.run(  compositePass.fb.textures[0] , deferredFB.depthTexture!!, deferredFB.positionTexture, deferredFB.normalsTexture )
        ssaoPass.run( ssrPass.fb.textures[0] , deferredFB.depthTexture!!, deferredFB.positionTexture, deferredFB.normalsTexture )
        dofPass.run(ssaoPass.fb.textures[0], deferredFB.depthTexture!!)
        bloomPass.run( dofPass.fb.textures[0], false, null )
        chromabPass.run(bloomPass.fb.textures[0])
        fxaaPass.run(chromabPass.fb)
        postPass.run(fxaaPass.fb, defaultFB)

    }


    override fun drawImGui() {
    }



}