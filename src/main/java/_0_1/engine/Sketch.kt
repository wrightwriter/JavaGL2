package _0_1.engine

import _0_1.math.vector.IVec2
import _0_1.wrightgl.Camera
import _0_1.wrightgl.WrightGL
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.thing.Thing
import imgui.ImGui
import imgui.extension.imguizmo.ImGuizmo
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import java.util.ArrayList
import kotlin.system.exitProcess

abstract class Sketch constructor(
    val engine: Engine
) {

    protected var thingQueue: ArrayList<Thing> = ArrayList()
    var camera: Camera
        get() = engine.camera
        set(v){engine.camera = v}

    val res
        get() = engine.res

    val time
        get() = engine.time
    val deltaTime
        get() = engine.deltaTime
    val frame
        get() = engine.frame


    val io
        get() = engine.io

    val fileSystem
        get() = engine.fileSystem

    val gui: GUI
        get() = engine.gui

    val wgl
        get() = engine.wgl

    val sketchFolder
        get() = engine.fileSystem.sketchResourcesFolder
    val resFolder
        get() = engine.fileSystem.globalResourcesFolder


    init{

    }
    fun start() {
        setup()
        loop()
    }

    // ----------------- DRAW ----------------- //
    private fun loop() {
//        val secsPerUpdate = 1.0 / 60.0
//        var previousT = GLFW.glfwGetTime().toFloat()
//        var secsToCatchUp = 0.0
        while (!glfwWindowShouldClose(engine.glfwWindow)) {
            engine.startFrame()
//            previousT = time
//            secsToCatchUp += deltaTime

            // input
//            while (secsToCatchUp >= secsPerUpdate){
//                // logic here i guess?
//                secsToCatchUp -= secsPerUpdate;
//            }
            display()
            drawImGui()

            ImGuizmo.beginFrame();
            engine.finishFrame()


            if (engine.engineSettings.nanoVGEnabled)
                drawNanoVG();

            // TODO: implement sync.
//            sync(currentT);
        }
        exitProcess(1)
    }
    //    protected abstract fun initSettings()
    protected abstract fun setup()
    protected abstract fun display()

    protected open fun drawNanoVG() {}
    protected open fun drawImGui() {}

}