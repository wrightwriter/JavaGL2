package sketches._2021_10

//import _0_1.wrightglPackage.WrightGL.updateMatrices
//import _0_1.wrightglPackage.WrightGL.FB.clear

import _0_1.engine.*
import _0_1.engine.gui.GUISliderFloat
import _0_1.engine.gui.GUISliderVec3
import _0_1.engine.gui.GUISliderVec4
import _0_1.main.Glob
import _0_1.math.Geometry
import _0_1.math.Mat4
import _0_1.math.vector.IVec2
import _0_1.math.vector.Vec2
import _0_1.math.vector.Vec3
import _0_1.wrightgl.*
import _0_1.wrightgl.Pass.FXPass
import _0_1.wrightgl.Pass.FXPassWithTex
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FB.Companion.bind
import _0_1.wrightgl.fb.FB.Companion.defaultFB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgRender
import _0_1.wrightgl.thing.Thing
import _0_1.wrightgl.thing.ThingInstanced
import imgui.ImGui
import imgui.extension.imguizmo.ImGuizmo
import imgui.type.ImBoolean
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*


//import framework.Semantic;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class Sketch() : Engine() {

    override fun initSettings() {
        engineSettings.res.x = 1024
        engineSettings.res.y = 768
        engineSettings.renderdocEnabled = true
        engineSettings.openGLDebugEnabled = true
        engineSettings.nanoVGEnabled = false
        engineSettings.liveShaderReloading = true
    }

    // Settings
    lateinit var cameraPosSetting: GUISliderVec3
    lateinit var cameraLookAtSetting: GUISliderVec3
    lateinit var cameraRotSetting: GUISliderFloat
    lateinit var settingB: GUISliderFloat

    // Programs
    lateinit var cubeProgram: ProgRender
    lateinit var compositeProgram: ProgRender
    lateinit var postProgram: ProgRender

    // FBs
    lateinit var drawFB: FB
    lateinit var compositeFB: FB
    lateinit var postFB: FB

    // Textures
    lateinit var potatoTexture: Texture

    // Things
    lateinit var postFX: FXPass
    lateinit var raymarchFX: FXPass
    lateinit var compositeFXFB: FXPassWithTex

    // Things

    lateinit var cube: Thing
    lateinit var instancedThing: ThingInstanced

    init{

    }


    override fun setup() {
        // ---------------- SETTINGS ---------------- //
        cameraRotSetting = GUISliderFloat("Camera Rotation")
        cameraPosSetting = GUISliderVec3(
            "Camera Position",
            -10.0f,
            10.0f,
            Vec3(0.0f,0.0f,-5.0f)
        )
        cameraLookAtSetting = GUISliderVec3(
            "Camera Look At",
            -5.0f,
            5.0f,
            Vec3(0.0f,0.0f,0.0f)
        )
        settingB = GUISliderFloat("Aaaa")


        // ---------------- FRAMEBUFFERS ---------------- //
        drawFB = FB(hasDepth = true)
        compositeFB = FB(hasDepth = true)
        postFB = FB(hasDepth = true)

        // ---------------- TEXTURES ---------------- //
        potatoTexture = Texture("potato.png")

        // ---------------- SHADERS ---------------- //
//        cubeProgram = ShaderProgram(
//            "cubeGeom.vp",
//            "cubeGeom.fp",
//            "cubeGeom.gp",
//            _folderPath = fileSystem.globalResourcesFolder
//        )
        cubeProgram = ProgRender(
            "cube.vp",
            "cube.fp",
//            "cubeGeom.gp",
            _folderPath = fileSystem.globalResourcesFolder
        )
        cubeProgram.setUniformTexture(potatoTexture, "potato")



        // ---------------- BUFFERS ---------------- //
        // Vertex Buffers
        val cubeVB = VB( Geometry.cubeNormals, intArrayOf(3, 3), VB.PrimitiveType.TRIANGLES )
        //		cubeVertexBuffer.culling = VertexBuffer.VertexCulling.FRONT;
//        val quadVB = VB( Geometry.quadTriangleStrip, intArrayOf(2), VB.PrimitiveType.TRIANGLES_STRIP )

        // ---------------- THINGS ---------------- //
        cube = Thing(cubeProgram, cubeVB)
        cube.primitiveType = VB.PrimitiveType.LINES
        thingQueue.add(cube)

        instancedThing = ThingInstanced(
            ProgRender(
                "cube.vp",
                "cube.fp",
                _folderPath = fileSystem.globalResourcesFolder
            ),
            VB( Geometry.cubeNormals, intArrayOf(3, 3), VB.PrimitiveType.TRIANGLES ),
            100
        )

        // ---------------- FX ---------------- //

        compositeFXFB = FXPassWithTex( "composite.fp", fileSystem.globalResourcesFolder,_isPingPong = true)
        postFX = FXPass("post.fp", fileSystem.globalResourcesFolder )
        raymarchFX = FXPass("raymarch.fp" )


        camera = CameraLookAt()

    }
    override fun display() {

        camera.eyePos = cameraPosSetting.get()
        (camera as CameraLookAt).lookAt = cameraLookAtSetting.get()
//        camera.eyePos.x = cameraPosSetting.get().x
//        camera.eyePos.y = cameraPosSetting.get().y
//        camera.eyePos.z = cameraPosSetting.get().z

        cube.modelMatrix *= Mat4.getRotationMatrix(Mat4.Axis.Y,deltaTime)
        // Settings
        glEnable(GL_BLEND)
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//        glDepthFunc(GL_LES)
        glDisable(GL_STENCIL_TEST)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        // Framebuffer


        drawFB.clearAllAttachments()
        bind(FB.Target.DRAW, drawFB)

        // Draw stuff
        for (thing in thingQueue) {
            thing.render()
        }

        instancedThing.render()
//        raymarchFX.run()


        compositeFXFB.run(drawFB)
        postFX.run(compositeFXFB.fb, defaultFB)


    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Glob.engine = Sketch()
            Glob.engine.start()
        }
//        val showDemo = ImBoolean(true)
//        val xs = arrayOf(0, 1, 2, 3, 4, 5)
//        val ys = arrayOf(0, 1, 2, 3, 4, 5)
//        val ys1 = arrayOf(0, 0, 1, 2, 3, 4)
//        val ys2 = arrayOf(1, 2, 3, 4, 5, 6)
    }
    override fun drawImGui() {
//        if (ImGui.getIO().configFlags.and(ImGuiConfigFlags.ViewportsEnable) == 1) {
//            ImGui.updatePlatformWindows();
//            ImGui.renderPlatformWindowsDefault();
//        }

        ImGuizmo.beginFrame();

        if (io.keyboard[IO.Key.LAlt]!!.Down) {
//            if (ImGui.button("aaaaa")) { }
//            ImGui.sliderFloat("zDepth",)
//            ImGui.sliderFloat("zPos", zKnob, -7.0f, 17.0f);



//            ImGui::GetWindowSize()

            // ---------------- SHOW TEXTURES ---------------- //
            ImGui.begin("FB Textures")
                val imWinSz = IVec2(ImGui.getWindowSize().x, ImGui.getWindowSize().y)
                for(fb in FB.frameBuffersList) {

                    if (fb.name != null)
                        ImGui.text(fb.name)

                    for(tex in fb.textures) {
                        if (tex.res.z == 1){
    //                        val
                            val texRes = tex.res.xy
    //                        val texRatio = tex.res.x/tex.res.y
                            var newTexRes = texRes
                            val maxSz = imWinSz.x
                            if (texRes.x > maxSz){
                                val ratioImWinxTexSzX = texRes.x/maxSz
                                newTexRes /= ratioImWinxTexSzX
                                newTexRes /= 2
                            }
    //                        val bigger
                            ImGui.image(tex.pid,newTexRes.x.toFloat(),newTexRes.y.toFloat())

                        }
                    }
                }
            ImGui.end()

            // ---------------- SHOW GUI ---------------- //
            ImGui.begin("Settings")
                for(guiSetting in gui.settings){
                    if (guiSetting is GUISliderFloat){
    //                    ImGui.sliderFloat("zPos", guiSetting.value as ImFloat, -7.0f, 17.0f);
                        ImGui.sliderFloat(
                            guiSetting.name,
                            guiSetting.value as FloatArray,
                            guiSetting.min,
                            guiSetting.max
                        )
                    } else if (guiSetting is GUISliderVec3){
                        ImGui.sliderFloat3(
                            guiSetting.name,
                            guiSetting.value as FloatArray,
                            guiSetting.min,
                            guiSetting.max
                        )
                    } else if (guiSetting is GUISliderVec4){
                        ImGui.sliderFloat4(
                            guiSetting.name,
                            guiSetting.value as FloatArray,
                            guiSetting.min,
                            guiSetting.max
                        )
                    } else if (guiSetting is GUISliderVec4){
                        ImGui.checkbox(
                            guiSetting.name,
                            (guiSetting.value as ImBoolean)
                        )
                    }
                }
            ImGui.end()




//            ImGui.setNextWindowPos(ImGui.getMainViewport().posX + 100, ImGui.getMainViewport().posY + 100, ImGuiCond.Once)
//            if (ImGui.begin("ImPlot Demo")) {
//                ImGui.text("This a demo for ImPlot")
//                ImGui.alignTextToFramePadding()
//                ImGui.text("Repo:")
//                ImGui.sameLine()
//                if (ImGui.button("")) {
////                try {
////                    Desktop.getDesktop().browse(URI(URL))
////                } catch (e: Exception) {
////                    e.printStackTrace()
////                }
//                }
//                ImGui.checkbox("Show ImPlot Built-In Demo", showDemo)
//                if (ImPlot.beginPlot("Example Plot")) {
//                    ImPlot.plotShaded<Number>("Shaded", xs, ys1, ys2)
//                    ImPlot.plotLine<Number>("Line", xs, ys)
//                    ImPlot.plotBars<Number>("Bars", xs, ys)
//                    ImPlot.endPlot()
//                }
//                if (ImPlot.beginPlot("Example Scatterplot")) {
//                    ImPlot.plotScatter<Number>("Scatter", xs, ys)
//                    ImPlot.endPlot()
//                }
//                if (showDemo.get()) {
//                    ImPlot.showDemoWindow(showDemo)
//                }
//            }

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
        NanoVG.nvgBeginFrame(wgl.nvg, res.y.toFloat(), res.y.toFloat(), 1f)
        NanoVG.nvgBeginPath(wgl.nvg)
        //		nvgRect(wgl.nvg, 0, 100, 100, 50);
        NanoVG.nvgCircle(wgl.nvg, (res.x / 2).toFloat(), (res.y / 2).toFloat(), 100.0f)
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

//    override var sketchResourcesFolder: String = System.getProperty("user.dir") + "/src/res/"

    init{
        //        local = System.getProperty("user.dir") + "/src/res/"
    }

}