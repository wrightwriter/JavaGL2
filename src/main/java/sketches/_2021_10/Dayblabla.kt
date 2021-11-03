package sketches._2021_10

//import _0_1.wrightglPackage.WrightGL.updateMatrices
//import _0_1.wrightglPackage.WrightGL.FB.clear

import _0_1.engine.Engine
import _0_1.engine.Sketch
import _0_1.engine.gui.GUISliderFloat
import _0_1.engine.gui.GUISliderVec3
import _0_1.math.Geometry
import _0_1.math.vector.Vec3
import _0_1.wrightgl.CameraPilot
import _0_1.wrightgl.Model
import _0_1.wrightgl.Pass.PassCompute
import _0_1.wrightgl.Pass.PassFX
import _0_1.wrightgl.Pass.PassFXFB
import _0_1.wrightgl.Pass.PassRaymarch
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.buffer.VBEditable
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FB.Companion.bind
import _0_1.wrightgl.fb.FB.Companion.defaultFB
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.ProgRender
import _0_1.wrightgl.thing.Thing
import _0_1.wrightgl.thing.ThingInstanced
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL32.*


//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class Dayblabla(
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
            Engine.startSketch(Dayblabla.javaClass, engineSettings)
        }
    }
    // ---------------- GUI ---------------- //
    var cameraRotGUI: GUISliderFloat = GUISliderFloat("Camera Rotation")
    var cameraPosGUI: GUISliderVec3 = GUISliderVec3(
        "Camera Position",
        -10.0f,
        10.0f,
        Vec3(0.0f,0.0f,-5.0f)
    )
    var cameraLookAtGUI: GUISliderVec3 = GUISliderVec3(
        "Camera Look At",
        -5.0f,
        5.0f,
        Vec3(0.0f,0.0f,0.0f)
    )

    // ---------------- Programs ---------------- //
    lateinit var compositeProgram: ProgRender
    lateinit var postProgram: ProgRender


    // ---------------- FRAMEBUFFERS ---------------- //
    var drawFB: FB = FB(hasDepth = true)
    var compositeFB: FB = FB(hasDepth = true)
    var postFB: FB = FB(hasDepth = true)

    // ---------------- TEXTURES ---------------- //
    val potatoTexture: Texture = Texture("potato.png")

    // ---------------- SHADERS ---------------- //
//        cubeProgram = ShaderProgram(
//            "cubeGeom.vp",
//            "cubeGeom.fp",
//            "cubeGeom.gp",
//            _folderPath = fileSystem.globalResourcesFolder
//        )
    val cubeProgram = ProgRender( "cube.vp", "cube.fp")
    //            "cubeGeom.gp", )
    val lineProgram = ProgRender( "line.vp", "line.fp" )

    // ---------------- VERT BUFFERS ---------------- //
    val cubeVB = VB( Geometry.cubeNormals, intArrayOf(3, 3), VB.PrimitiveType.TRIANGLES )
    val lineVBEditable = VBEditable( )
    val lineVB = VB( Geometry.cubeNormals, intArrayOf(3, 3), VB.PrimitiveType.TRIANGLES )
    val cubeModelThing = Thing(
        ProgRender("cubeModel.vp", "cubeModel.fp"),
        Model("cube.fbx")
    )
//        val quadVB = VB( Geometry.quadTriangleStrip, intArrayOf(2), VB.PrimitiveType.TRIANGLES_STRIP )

    // ---------------- THINGS ---------------- //

    val cubeThing = Thing(cubeProgram, cubeVB)
    val lineThing = Thing(lineProgram, lineVBEditable)

    var particlesCompute = PassCompute("particles.cp")
    var particleThing = ThingInstanced(
        ProgRender(
            "cube.vp",
            "cube.fp",
        ),
        VB( Geometry.cubeNormals, intArrayOf(3, 3), VB.PrimitiveType.TRIANGLES ),
        100
    )

    // ---------------- PASSES ---------------- //
    var compositeFXFB = PassFXFB( "composite.fp", _isPingPong = true)
    var postFX = PassFX("post.fp" )
    var raymarchFX = PassRaymarch("raymarch.fp" )


    override fun setup() {

        cubeProgram.setUniformTexture(potatoTexture, "potato")
        cubeThing.primitiveType = VB.PrimitiveType.LINES_STRIP
        cubeThing.culling = VB.CullMode.DISABLED

        lineThing.primitiveType = VB.PrimitiveType.LINES_STRIP

//        thingQueue.add(cubeThing)
        thingQueue.add(lineThing)



//        camera = CameraLookAt()
        camera = CameraPilot()
    }
    override fun display() {

//        camera.eyePos = cameraPosGUI.get()
//        (camera as CameraLookAt).lookAt = cameraLookAtGUI.get()

        cubeThing.modelMatrix.rotateY(deltaTime).translate(Vec3(0.04f*deltaTime,0.0f,0.0f))


        // Setting
        glEnable(GL_BLEND)
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//        glDepthFunc(GL_LES)
        glDisable(GL_STENCIL_TEST)
        glEnable(GL_DEPTH_TEST)



        drawFB.clearAllAttachments()
        bind(FB.Target.DRAW, drawFB)




        // Draw stuff
        for (thing in thingQueue) {
//            thing.render()
        }

        cubeModelThing.render(
            _primitiveType = VB.PrimitiveType.TRIANGLES,
        )
//        particlesCompute.dispatch(particleThing.instancesBuffer)
//        particleThing.render()


        glDepthFunc(GL_LESS);

        glDepthMask(true);

        raymarchFX.run(drawFB)


        compositeFXFB.run(drawFB)
        postFX.run(compositeFXFB.fb, defaultFB)
//        val mapFlags =

//        mapData = GL46.glMapBufferRange(pid, 0, totalVertSizeBytes.toLong(), mapFlags)!!
//        cubeThing.vertexBuffers[0].mapData = GL46.glMapNamedBuffer(cubeThing.vertexBuffers[0].pid, GL46.GL_MAP_WRITE_BIT or GL46.GL_MAP_PERSISTENT_BIT or GL46.GL_MAP_COHERENT_BIT, cubeThing.vertexBuffers[0].mapData)!!

//        void glFlushMappedBufferRange(GLenum target​, GLintptr offset​, GLsizeiptr length​);


//        vertexBuff.mapData = GL46.glMapNamedBuffer(
//            vertexBuff.pid,
//     GL46.GL_MAP_WRITE_BIT or GL46.GL_MAP_READ_BIT or GL46.GL_MAP_PERSISTENT_BIT or GL46.GL_MAP_COHERENT_BIT,
//            vertexBuff.mapData
//        )!!

        // ------ START SYNC ------ //
//        val syncObj = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);


//        println()
//        val vertexBuff = lineVBEditable
//        if(Random.Companion.XOR.gen() < 0.8){
//            vertexBuff.pushOne(
//                GLSL.map(Random.Companion.XOR.gen(), 0.0f,1.0f,-1f, 1f),
//                GLSL.map(Random.Companion.XOR.gen(), 0.0f,1.0f,-1f, 1f),
//                GLSL.map(Random.Companion.XOR.gen(), 0.0f,1.0f,-1f, 1f)
//            )
//        }



//            vertexBuff.gpuBuff!!.put(idx, ))
//
//        GL46.glFlushMappedNamedBufferRange(vertexBuff.pid,0,vertexBuff.totalSizeBytes.toLong())

//        glMemoryBarrier(GL_CLIENT_MAPPED_BUFFER_BARRIER_BIT)

        // ------ WAIT ------ //
//        while (true) {
//            val waitReturn = glClientWaitSync(syncObj, GL_SYNC_FLUSH_COMMANDS_BIT, 1);
//            if (waitReturn == GL_ALREADY_SIGNALED || waitReturn == GL_CONDITION_SATISFIED){
//                glDeleteSync(syncObj)
//                break;
//            }
//        }




    }


    override fun drawImGui() {
//        if (ImGui.getIO().configFlags.and(ImGuiConfigFlags.ViewportsEnable) == 1) {
//            ImGui.updatePlatformWindows();
//            ImGui.renderPlatformWindowsDefault();
//        }


        if (io.keyboard[engine.engineSettings.imguiKey]!!.Down) {
//            if (ImGui.button("aaaaa")) { }
//            ImGui.sliderFloat("zDepth",)
//            ImGui.sliderFloat("zPos", zKnob, -7.0f, 17.0f);

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


}