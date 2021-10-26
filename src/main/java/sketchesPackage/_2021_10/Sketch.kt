package sketchesPackage._2021_10

//import _0_1.wrightglPackage.WrightGL.updateMatrices
//import _0_1.wrightglPackage.WrightGL.FB.clear
import _0_1.wrightglPackage.FB
import _0_1.wrightglPackage.Texture
import _0_1.wrightglPackage.Program
import _0_1.wrightglPackage.FB.Companion.bind
import _0_1.enginePackage.Engine
import _0_1.enginePackage.Thing
import _0_1.mathPackage.Mat4
import _0_1.mathPackage.Geometry
import _0_1.mathPackage.Vec3
import _0_1.enginePackage.IO
import org.lwjgl.nanovg.NanoVG
import kotlin.jvm.JvmStatic
import imgui.ImGui
import _0_1.mainPackage.Global
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import _0_1.wrightglPackage.FB.Companion.defaultFB
import _0_1.wrightglPackage.VB

//import framework.Semantic;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class Sketch() : Engine() {

    override fun initSettings() {
        engineSettings.resx = 1024
        engineSettings.resy = 768
        engineSettings.renderdocEnabled = true
        engineSettings.openGLDebugEnabled = true
        engineSettings.nanoVGEnabled = false
        engineSettings.liveShaderReloading = true
    }


    // Programs
    var cubeProgram: Program? = null
    var compositeProgram: Program? = null
    var postProgram: Program? = null

    // FBs
    var mainFB: FB? = null
    var compositeFB: FB? = null
    var postFB: FB? = null

    // Textures
    var potatoTexture: Texture? = null

    // Things
    var postThing: Thing? = null
    var compositeThing: Thing? = null



    override fun setup() {
        // Framebuffers
        mainFB = FB(hasDepth = true)
        compositeFB = FB(hasDepth = true)
        postFB = FB(hasDepth = true)

        // Matrices
        viewMatrix = Mat4.identityMatrix
        projectionMatrix = Mat4.identityMatrix

        // Shaders
        cubeProgram = Program(
            "cube.vp",
            "cube.fp",
            "cube.gp",
            _folderPath = fileSystem.globalResourcesFolder
        )
        compositeProgram = Program("quad.vp", "composite.fp",
            _folderPath = fileSystem.globalResourcesFolder
        )
        postProgram = Program("quad.vp", "post.fp",
            _folderPath = fileSystem.globalResourcesFolder
            )

        // Texture
        potatoTexture = Texture("potato.png")

        // Vertex Buffers
        val cubeVB = VB(
            Geometry.cubeNormals, intArrayOf(3, 3), VB.PrimitiveType.TRIANGLES
        )
        //		cubeVertexBuffer.culling = VertexBuffer.VertexCulling.FRONT;
        val quadVB = VB(
            Geometry.triangleStripQuad, intArrayOf(2), VB.PrimitiveType.TRIANGLES_STRIP
        )

        // Things
        val cube = Thing(cubeProgram!!, cubeVB)
        thingQueue.add(cube)

        cube.callback = fun(thing) {
            // Update model matrix
            val scale = Mat4.getScaleMatrix(Vec3(.5f, 0.5f, 0.5f))
            val translate = Mat4.getTranslationMatrix(Vec3(0.0f, 0.0f, 6.0f))
            val rotateZ = Mat4.getRotationMatrix(Mat4.Axis.Y, time.toFloat())

            thing.modelMatrix = translate * rotateZ
        }
//        cube.callback = (t: Thing ) ->{


        compositeThing = Thing(compositeProgram!!, quadVB)
        postThing = Thing(postProgram!!, quadVB)

    }
    override fun display() {
        wgl.updateMatrices()

        // Settings
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_CULL_FACE)
        glDepthFunc(GL_LESS)
        glCullFace(GL_BACK)
        glEnable(GL_DEPTH_TEST)

        // Framebuffer
        mainFB!!.clear()

        bind(FB.Target.DRAW_FRAMEBUFFER, mainFB!!)


        // Draw stuff
        for (thing in thingQueue) {
            thing.render()
        }


        // Post
//		WrightGL.FB.bind(WrightGL.FB.Target.DRAW_FRAMEBUFFER, defaultFB!!)
//		glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
//		glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
//
//		compositeThing!!.render(
//            fun(Thing: Thing){
//                mainFB!!.setUniformTextures("g")
//            }
//		)


//		// Blit framebuffer
        FB.blit(mainFB!!, defaultFB );

        FB.bind(FB.Target.FRAMEBUFFER, defaultFB)
    }

    override fun drawImGui() {
        if (io.keyboard[IO.Key.Tab]!!.Down) {
            ImGui.begin("aa")
            if (ImGui.button("aaaaa")) {
            }
            ImGui.end()
        }
    }

    override fun drawNanoVG() {
//		glReadBuffer(GL_BACK);
//
//		glEnable(GL_STENCIL_TEST);
//		glClearColor(0, 0, 0, 1);
//		glColorMask(false, false, false, false);
//
//		glClearStencil(0);
//		glClear(GL_STENCIL_BUFFER_BIT);
//
//		glDisable(GL_BLEND);
//		glDisable(GL_CULL_FACE);
//		glCullFace(GL_BACK);
        NanoVG.nvgBeginFrame(wgl.nvg, resX.toFloat(), resY.toFloat(), 1f)
        NanoVG.nvgBeginPath(wgl.nvg)
        //		nvgRect(wgl.nvg, 0, 100, 100, 50);
        NanoVG.nvgCircle(wgl.nvg, (resX / 2).toFloat(), (resY / 2).toFloat(), 100.0f)
        NanoVG.nvgRGBAf(0.01f, 1.0f, 0.4f, 0.9f, wgl.nvgColor)
        NanoVG.nvgFillColor(wgl.nvg, wgl.nvgColor)
        NanoVG.nvgFill(wgl.nvg)

//		nvgClosePath(wgl.nvg);
        NanoVG.nvgEndFrame(wgl.nvg)
        //		nvgRestore(wgl.nvg);
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_STENCIL_TEST)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
    }

    override var sketchResourcesFolder: String = System.getProperty("user.dir") + "/src/res/"

    init{
        //        local = System.getProperty("user.dir") + "/src/res/"
    }
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Global.engine = Sketch()
            Global.engine!!.start()
        }
    }

}