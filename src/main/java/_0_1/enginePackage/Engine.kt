package _0_1.enginePackage

//import _0_1.wrightglPackage.WrightGL.FB.clear
//import _0_1.wrightglPackage.WrightGL.Shader.errorLog
//import _0_1.wrightglPackage.WrightGL.Program.errorLog
//import _0_1.wrightglPackage.WrightGL.Shader.compile
//import _0_1.wrightglPackage.WrightGL.Program.link
import _0_1.wrightglPackage.WrightGL
import _0_1.mathPackage.Mat4
import imgui.ImGui
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiColorEditFlags
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiWindowFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.type.ImBoolean
import java.lang.RuntimeException
import _0_1.mathPackage.Vec4
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL32
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import _0_1.wrightglPackage.FB
import _0_1.wrightglPackage.Program
import _0_1.wrightglPackage.Shader
import _0_1.wrightglPackage.Texture
import java.lang.Exception
import java.util.ArrayList
import java.util.LinkedHashSet
import java.util.Objects
import kotlin.io.path.pathString

//import framework.Semantic;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
abstract class Engine {
    // ----------------- VARS ----------------- //
    inner class Settings {
        var resx = 1024
        var resy = 768
        var renderdocEnabled = true
        var openGLDebugEnabled = true
        var nanoVGEnabled: Boolean = true
        var liveShaderReloading = true
    }
    var engineSettings = Settings()
        protected set

//    companion object {
    var glfwWindow: Long = 0
        protected set
    private val keysToUnpress = ArrayList<IO.Key.State?>()
    private val keysToUnrelease = ArrayList<IO.Key.State?>()
//    }

    var resX: Int
        protected set
    var resY: Int
        protected set

    var timeStart: Long = 0
        private set
    var time = 0.0
        private set
    var deltaTime = 0.0
        private set


    var io = IO()
        private set;

    var fileSystem: FileSystem
        private set


    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()

    var wgl: WrightGL
        private set

    var globalResourcesFolder: String
    protected abstract var sketchResourcesFolder: String

    var viewMatrix: Mat4? = null
    var projectionMatrix: Mat4? = null

    protected var thingQueue: ArrayList<Thing> = ArrayList()

    // ----------------- INIT ----------------- //
    init {
        print(
            this.javaClass.getResource(this.javaClass.simpleName + ".class").toString().replace(
        "out/production/classes",
        "src/main/java"
            )
//    this.class.getResource(Test.class.getSimpleName() + ".class")

        )
        print("Initialized engine \n")

        globalResourcesFolder = System.getProperty("user.dir") + "/src/res/"

        initSettings()

        resX = engineSettings.resx
        resY = engineSettings.resy

        fileSystem = FileSystem(this)
        fileSystem.start()

        setupGLFWImGuiNVG()
        wgl = WrightGL(this)
    }

    fun start() {
        timeStart = System.currentTimeMillis()
        FB.defaultFB.clear(
            Vec4(0.0f, 0.0f, 0.0f, 1.0f),
            arrayOf(
                FB.FbBitmask.COLOR_BUFFER_BIT,
                FB.FbBitmask.STENCIL_BUFFER_BIT,
                FB.FbBitmask.DEPTH_BUFFER_BIT
            )
        )
        setup()
        loop()
    }

    protected abstract fun initSettings()
    protected abstract fun setup()
    protected abstract fun display()

    protected open fun drawNanoVG() {}
    protected open fun drawImGui() {}

    private fun startFrame() {
        FB.bind(FB.Target.FRAMEBUFFER, FB.defaultFB)
        GL32.glClearColor(0.05f, 0.1f, 0f, 1f)
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT or GL32.GL_DEPTH_BUFFER_BIT or GL32.GL_STENCIL_BUFFER_BIT)

        imGuiGlfw.newFrame()
        ImGui.newFrame()
    }

    // ----------------- DRAW ----------------- //
    fun loop() {
        val secsPerUpdate = 1.0 / 60.0
        var previousT = GLFW.glfwGetTime()
        var secsToCatchUp = 0.0
        while (true) {
            startFrame()
            time = GLFW.glfwGetTime()
            deltaTime = time - previousT
            previousT = time
            secsToCatchUp += deltaTime

            // input
//            while (secsToCatchUp >= secsPerUpdate){
//                // logic here i guess?
//                secsToCatchUp -= secsPerUpdate;
//            }
            display()
            drawImGui()
            showErrorsInImGui()

            if (engineSettings.nanoVGEnabled)
                drawNanoVG();

            finishFrame()
            // TODO: implement sync.
//            sync(currentT);
        }
    }

    private fun finishFrame() {
        // Input
        for (keyState in keysToUnpress) {
            keyState!!.Press = false
        }
        for (keyState in keysToUnrelease) {
            keyState!!.Release = false
        }
        io.LMBPress = false
        io.RMBPress = false
        io.LMBRelease = false
        io.RMBRelease = false

        // ImGui
        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupWindowPtr = GLFW.glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(backupWindowPtr)
        }
        // GLFW
        GLFW.glfwSwapBuffers(glfwWindow)
        GLFW.glfwPollEvents()


        // Get a list of changed shaders and texture
        if (fileSystem.foundChangedFiles.get() == true) {
            for (path in fileSystem.changedFiles) {
                for (shader in Shader.shadersList) {

                    if (path.endsWith(shader.fileName) && path.pathString.contains(shader.folderPath)) {
                        println("A shader file changed")
                        fileSystem.shadersToRecompile.add(shader)
                    }
                }
            }
            for (path in fileSystem.changedFiles) {
                for (texture in Texture.fileTexturesList) {
                    if (texture.fileName != null){
                        if (
                            path.endsWith(texture.fileName!!)
                            && path.pathString.contains(texture.folderPath.toString())
                        ) {
                            println("A texture changed")
                            fileSystem.texturesToReload.add(texture)
                        }
                    }
                }
            }
            fileSystem.foundChangedFiles.set(false)
            fileSystem.changedFiles.clear()
        }

        // Reload textures.
        for (texture in fileSystem.texturesToReload) {
            texture.delete()
            texture.loadFile(texture.fileName!!, texture.folderPath!!)
        }
        fileSystem.texturesToReload.clear()

        // Recompile shaders.
        val shadersFailedCompilationList = Shader.shadersFailedCompilationList

        // There must be NO duplicates in shadersToRecompile
        for (shader in fileSystem.shadersToRecompile) {
            val successfulCompilation = shader.compile()
            if (successfulCompilation) {
                // remove successfully compiled shaders from error log list.
                for (failedShader in shadersFailedCompilationList) {
                    if (failedShader == shader) {
                        shadersFailedCompilationList.remove(failedShader)
                        break
                    }
                }
                // Try to relink parent program.
                // TODO: move this a bit down
            }
            for (program in shader.programs) {
                program.link()
            }
        }

        // Flush shadersToRecompile
        fileSystem.shadersToRecompile.clear()
    }


    private fun showErrorsInImGui() {
        val thereAreShaderErrors: Boolean = Shader.shadersFailedCompilationList.size > 0
        val thereAreTextureErrors: Boolean = Texture.fileTexturesFailedLoading.size > 0
        if (thereAreTextureErrors || thereAreShaderErrors) {
            ImGui.setNextWindowPos(0f, 0f)
            val open_ptr = ImBoolean()
            open_ptr.set(true)
            ImGui.begin(
                "0 ",
                open_ptr,
                ImGuiWindowFlags.NoBackground or ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.AlwaysAutoResize or
                        ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoTitleBar
            )
            ImGui.colorEdit3(
                " ",
                floatArrayOf(1f, 0f, 0f),
                ImGuiColorEditFlags.NoInputs or ImGuiColorEditFlags.NoPicker
            )

            // Shader errors
            if(thereAreShaderErrors){
                val programsWithFailedCompilation = LinkedHashSet<Program>()
                for (sh in Shader.shadersFailedCompilationList) {
                    val title = sh.fileName
                    for (program in sh.programs) {
                        programsWithFailedCompilation.add(program)
                    }
                    //                title += itrShader->first->name;
                    ImGui.text(title)
                    ImGui.text(sh.errorLog)
                }
                for (program in programsWithFailedCompilation) {
                    val err = program.errorLog
                    if (err != null) ImGui.text(err)
                }
            }

            // Texture errors
            if(thereAreTextureErrors){
                for (texture in Texture.fileTexturesFailedLoading) {
                    val title = texture.fileName
                    ImGui.text(title)
                    ImGui.text(texture.errorLog)
                }
            }

            ImGui.setWindowCollapsed(true)
            ImGui.end()
            //            std::map<Shader*, std::string>::iterator itrProgram;
//            for (itrProgram = shaderCompilationErrors.begin(); itrProgram != shaderCompilationErrors.end(); ++itrProgram) {
//                std::string title = "Program";
//                title += itrProgram->first->name;
//                ImGui::Text( title.c_str());
//                ImGui::Text( itrProgram->second.c_str());
//            }
//
//            ImGui::SetWindowCollapsed(true);
//            ImGui::End();
        }
    }


    private fun setupGLFWImGuiNVG() {
//        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        GLFWErrorCallback.createPrint(System.err).set()

//        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        glfwWindow = GLFW.glfwCreateWindow(resX, resY, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL)
        if (glfwWindow == MemoryUtil.NULL)
            throw RuntimeException("Failed to create GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(glfwWindow, object : GLFWKeyCallback() {
            override fun invoke(window: Long, glfwKey: Int, scancode: Int, action: Int, mods: Int) {
                try {
                    if (action == GLFW.GLFW_REPEAT) {
                        return
                    }
                    val keyIdx = IO.Key.getKey(glfwKey)
                    val key = io.keyboard[keyIdx]
                    if (action == GLFW.GLFW_PRESS) {
                        if (key!!.Down == false) {
                            key.Press = true
                            keysToUnpress.add(key)
                        }
                        key.Up = false
                        key.Down = true
                    } else if (action == GLFW.GLFW_RELEASE) {
                        key!!.Release = true
                        keysToUnrelease.add(key)
                        key.Up = true
                        key.Down = false
                    }
                } catch (e: Exception) {
                    assert(false)
                }
            }
        })
        GLFW.glfwSetWindowSizeCallback(glfwWindow) { wnd: Long, _sizex: Int, _sizey: Int ->
            resX = _sizex
            resY = _sizey
            GL11.glViewport(0, 0, _sizex, _sizey)
        }
        GLFW.glfwSetCursorPosCallback(glfwWindow) { wnd: Long, xpos: Double, ypos: Double ->
            val oldMousePos = io.mousePos.copy()
            io.mousePos.`val`[0] = xpos.toFloat()
            io.mousePos.`val`[1] = ypos.toFloat()
            io.deltaMousePos = io.mousePos.sub(oldMousePos)
        }
        GLFW.glfwSetMouseButtonCallback(glfwWindow) { wnd: Long, btn: Int, action: Int, mode: Int ->
            if (action == GLFW.GLFW_PRESS) {
                io.LMBPress = if (btn == GLFW.GLFW_MOUSE_BUTTON_1) true else io.LMBPress
                io.RMBPress = if (btn == GLFW.GLFW_MOUSE_BUTTON_2) true else io.RMBPress
                io.LMBDown = if (btn == GLFW.GLFW_MOUSE_BUTTON_1) true else io.RMBDown
                io.RMBDown = if (btn == GLFW.GLFW_MOUSE_BUTTON_2) true else io.RMBDown
            } else if (action == GLFW.GLFW_RELEASE) {
                io.LMBRelease = if (btn == GLFW.GLFW_MOUSE_BUTTON_1) true else io.LMBRelease
                io.RMBRelease = if (btn == GLFW.GLFW_MOUSE_BUTTON_2) true else io.RMBRelease
                io.LMBUp = if (btn == GLFW.GLFW_MOUSE_BUTTON_1) true else io.LMBUp
                io.RMBUp = if (btn == GLFW.GLFW_MOUSE_BUTTON_2) true else io.RMBUp
            }
        }
        // Get the resolution of the primary monitor
        val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(glfwWindow)
        // Enable v-sync
        GLFW.glfwSwapInterval(1)
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(glfwWindow, pWidth, pHeight)


            // Center the window
            GLFW.glfwSetWindowPos(
                glfwWindow,
                (vidmode!!.width() - pWidth[0]) / 2,
                (vidmode.height() - pHeight[0]) / 2
            )
        }
        GL.createCapabilities()

        // Make the window visible
        GLFW.glfwShowWindow(glfwWindow)
        ImGui.createContext()
        imGuiGlfw.init(glfwWindow, true)
        imGuiGl3.init("#version 130")
        ImGui.styleColorsDark()
        val style = ImGui.getStyle()
        style.windowRounding = 0.0f

        // ImGui theme.
        val colors = ImGui.getStyle().colors
        run {
            colors[ImGuiCol.Text] = floatArrayOf(1.00f, 1.00f, 1.00f, 0.95f)
            colors[ImGuiCol.TextDisabled] = floatArrayOf(0.50f, 0.50f, 0.50f, 1.00f)
            colors[ImGuiCol.WindowBg] = floatArrayOf(0.13f, 0.12f, 0.12f, 1.00f)
            colors[ImGuiCol.ChildBg] = floatArrayOf(1.00f, 1.00f, 1.00f, 0.00f)
            colors[ImGuiCol.PopupBg] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.94f)
            colors[ImGuiCol.Border] = floatArrayOf(0.53f, 0.53f, 0.53f, 0.46f)
            colors[ImGuiCol.BorderShadow] = floatArrayOf(0.00f, 0.00f, 0.00f, 0.00f)
            colors[ImGuiCol.FrameBg] = floatArrayOf(0.00f, 0.00f, 0.00f, 0.85f)
            colors[ImGuiCol.FrameBgHovered] = floatArrayOf(0.22f, 0.22f, 0.22f, 0.40f)
            colors[ImGuiCol.FrameBgActive] = floatArrayOf(0.16f, 0.16f, 0.16f, 0.53f)
            colors[ImGuiCol.TitleBg] = floatArrayOf(0.00f, 0.00f, 0.00f, 1.00f)
            colors[ImGuiCol.TitleBgActive] = floatArrayOf(0.00f, 0.00f, 0.00f, 1.00f)
            colors[ImGuiCol.TitleBgCollapsed] = floatArrayOf(0.00f, 0.00f, 0.00f, 0.51f)
            colors[ImGuiCol.MenuBarBg] = floatArrayOf(0.12f, 0.12f, 0.12f, 1.00f)
            colors[ImGuiCol.ScrollbarBg] = floatArrayOf(0.02f, 0.02f, 0.02f, 0.53f)
            colors[ImGuiCol.ScrollbarGrab] = floatArrayOf(0.31f, 0.31f, 0.31f, 1.00f)
            colors[ImGuiCol.ScrollbarGrabHovered] = floatArrayOf(0.41f, 0.41f, 0.41f, 1.00f)
            colors[ImGuiCol.ScrollbarGrabActive] = floatArrayOf(0.48f, 0.48f, 0.48f, 1.00f)
            colors[ImGuiCol.CheckMark] = floatArrayOf(0.79f, 0.79f, 0.79f, 1.00f)
            colors[ImGuiCol.SliderGrab] = floatArrayOf(0.48f, 0.47f, 0.47f, 0.91f)
            colors[ImGuiCol.SliderGrabActive] = floatArrayOf(0.56f, 0.55f, 0.55f, 0.62f)
            colors[ImGuiCol.Button] = floatArrayOf(0.50f, 0.50f, 0.50f, 0.63f)
            colors[ImGuiCol.ButtonHovered] = floatArrayOf(0.67f, 0.67f, 0.68f, 0.63f)
            colors[ImGuiCol.ButtonActive] = floatArrayOf(0.4f, 0.26f, 0.26f, 0.63f)
            colors[ImGuiCol.Header] = floatArrayOf(0.54f, 0.54f, 0.54f, 0.58f)
            colors[ImGuiCol.HeaderHovered] = floatArrayOf(0.64f, 0.65f, 0.65f, 0.80f)
            colors[ImGuiCol.HeaderActive] = floatArrayOf(0.25f, 0.25f, 0.25f, 0.80f)
            colors[ImGuiCol.Separator] = floatArrayOf(0.58f, 0.58f, 0.58f, 0.50f)
            colors[ImGuiCol.SeparatorHovered] = floatArrayOf(0.81f, 0.81f, 0.81f, 0.64f)
            colors[ImGuiCol.SeparatorActive] = floatArrayOf(0.81f, 0.81f, 0.81f, 0.64f)
            colors[ImGuiCol.ResizeGrip] = floatArrayOf(0.87f, 0.87f, 0.87f, 0.53f)
            colors[ImGuiCol.ResizeGripHovered] = floatArrayOf(0.87f, 0.87f, 0.87f, 0.74f)
            colors[ImGuiCol.ResizeGripActive] = floatArrayOf(0.87f, 0.87f, 0.87f, 0.74f)
            colors[ImGuiCol.Tab] = floatArrayOf(0.01f, 0.01f, 0.01f, 0.86f)
            colors[ImGuiCol.TabHovered] = floatArrayOf(0.29f, 0.29f, 0.29f, 1.00f)
            colors[ImGuiCol.TabActive] = floatArrayOf(0.31f, 0.31f, 0.31f, 1.00f)
            colors[ImGuiCol.TabUnfocused] = floatArrayOf(0.02f, 0.02f, 0.02f, 1.00f)
            colors[ImGuiCol.TabUnfocusedActive] = floatArrayOf(0.19f, 0.19f, 0.19f, 1.00f)
            //colors[ImGuiCol.DockingPreview] = new float[]{0.38f, 0.48f, 0.60f, 1.00f};
            //colors[ImGuiCol.DockingEmptyBg] = new float[]{0.20f, 0.20f, 0.20f, 1.00f};
            colors[ImGuiCol.PlotLines] = floatArrayOf(0.61f, 0.61f, 0.61f, 1.00f)
            colors[ImGuiCol.PlotLinesHovered] = floatArrayOf(0.68f, 0.68f, 0.68f, 1.00f)
            colors[ImGuiCol.PlotHistogram] = floatArrayOf(0.90f, 0.77f, 0.33f, 1.00f)
            colors[ImGuiCol.PlotHistogramHovered] = floatArrayOf(0.87f, 0.55f, 0.08f, 1.00f)
            colors[ImGuiCol.TextSelectedBg] = floatArrayOf(0.47f, 0.60f, 0.76f, 0.47f)
            colors[ImGuiCol.DragDropTarget] = floatArrayOf(0.58f, 0.58f, 0.58f, 0.90f)
            colors[ImGuiCol.NavHighlight] = floatArrayOf(0.60f, 0.60f, 0.60f, 1.00f)
            colors[ImGuiCol.NavWindowingHighlight] = floatArrayOf(1.00f, 1.00f, 1.00f, 0.70f)
            colors[ImGuiCol.NavWindowingDimBg] = floatArrayOf(0.80f, 0.80f, 0.80f, 0.20f)
            colors[ImGuiCol.ModalWindowDimBg] = floatArrayOf(0.80f, 0.80f, 0.80f, 0.35f)
            colors[ImGuiCol.WindowBg] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.5f)
            colors[ImGuiCol.PopupBg] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.5f)
            colors[ImGuiCol.TitleBg] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.5f)
            colors[ImGuiCol.TitleBgActive] = floatArrayOf(0.05f, 0.05f, 0.05f, 0.5f)
            colors[ImGuiCol.Border][3] = 0.0f
        }
    }

    protected fun destroy() {
        ImGui.destroyContext()
        Callbacks.glfwFreeCallbacks(glfwWindow)
        GLFW.glfwDestroyWindow(glfwWindow)
        GLFW.glfwTerminate()
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null))!!.free()
    }


}