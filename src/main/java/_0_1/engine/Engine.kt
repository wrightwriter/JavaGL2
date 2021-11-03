package _0_1.engine

//import _0_1.wrightglPackage.WrightGL.FB.clear
//import _0_1.wrightglPackage.WrightGL.Shader.errorLog
//import _0_1.wrightglPackage.WrightGL.Program.errorLog
//import _0_1.wrightglPackage.WrightGL.Shader.compile
//import _0_1.wrightglPackage.WrightGL.Program.link
import _0_1.main.Global
import _0_1.math.vector.IVec2
import _0_1.math.vector.Vec2
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import java.lang.RuntimeException
import _0_1.math.vector.Vec4
import _0_1.wrightgl.*
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.fb.FBPingPong
import _0_1.wrightgl.fb.Texture
import _0_1.wrightgl.shader.Shader
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import sketches._2021_10.Dayblabla
import java.io.File
import java.lang.Exception
import java.nio.file.Path
import java.util.ArrayList
import java.util.Objects
import kotlin.io.path.pathString

//import framework.Semantic;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class Engine private constructor(){
    // ----------------- VARS ----------------- //
    class Settings {
        var res = IVec2(1024, 768)
        var renderdocEnabled = true
        var openGLDebugEnabled = true
        var nanoVGEnabled: Boolean = true
        var liveShaderReloading = true
        var imguiKey = IO.Key.LAlt
    }

    var engineSettings = Settings()
        protected set

    var glfwWindow: Long = 0
        protected set
    private val keysToUnpress = ArrayList<IO.Key.State?>()
    private val keysToUnrelease = ArrayList<IO.Key.State?>()




    var res = IVec2(1024, 768)
        protected set

//    var timeStart = 0.0f
//        private set
    var time = 0.0f
        private set
    var deltaTime = 0.0f
        private set
    var frame: Int = 0
        private set


    var io = IO()
        private set;

    lateinit var fileSystem: FileSystem
        private set

    lateinit var gui: GUI
        private set;


    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()

    lateinit var wgl: WrightGL
        private set

    var camera: Camera = CameraPilot()


    private var renderdoc: Renderdoc? = null
    // ----------------- INIT ----------------- //

    companion object {
        fun startSketch(sketchClass: Class<Any>, _engineSettings: Settings){
            Global.engine = Engine(_engineSettings)
            Global.engine.init(sketchClass)
            val sketchClassName =  sketchClass.declaringClass.packageName +"."+ sketchClass.declaringClass.simpleName
            val classs = Class.forName(sketchClassName)
            val constructor = classs.getConstructor(Engine::class.java)
            val sketch = constructor.newInstance(Global.engine)
            (sketch as Sketch).start()
        }
    }

    constructor(
        _engineSettings: Settings
    ) : this(){
        engineSettings = _engineSettings
        print("Starting engine. \n")
        res = engineSettings.res.copy()

    }

    fun init(sketchClass: Class<Any>) {
        gui = GUI()

        if (engineSettings.renderdocEnabled)
            renderdoc = Renderdoc()

        setupGLFWImGuiNVG()
        wgl = WrightGL(this)


        fileSystem = FileSystem(this, sketchClass)
        fileSystem.start()

//        timeStart = System.currentTimeMillis()
        FB.defaultFB.clear(
            Vec4(0.0f, 0.0f, 0.0f, 1.0f),
            arrayOf(
                FB.FbBitmask.COLOR,
                FB.FbBitmask.STENCIL,
                FB.FbBitmask.DEPTH
            )
        )

    }


    internal fun startFrame() {

        FB.bind(FB.Target.FRAMEBUFFER, FB.defaultFB)
        FB.defaultFB.clearAllAttachments( )

        imGuiGlfw.newFrame()
        ImGui.newFrame()

        val previousTime = time
        time = GLFW.glfwGetTime().toFloat()
        deltaTime = time - previousTime
    }


    internal fun finishFrame() {
        // ImGui

        gui.showErrorsInImGui()
        if (io.keyboard[engineSettings.imguiKey]!!.Down){
            gui.showSettingsInImGui()
            gui.showTexturesInImGui()
        }

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

        // Ping Pong buffers
        for(pingPongFB: FBPingPong in FBPingPong.pingPongFBs){
            pingPongFB.ping()
        }

        // Get a list of changed shaders and textures
        if (fileSystem.foundChangedFiles.get()) {
            for (path in fileSystem.changedFiles) {
                for (shader in Shader.shadersList) {
                    val cleanedPath: Path =  Path.of(File(path.toString()).getCanonicalPath())
                    val cleanedShaderFolderPath: String =  File(shader.folderPath).getCanonicalPath()
                    if (cleanedPath.endsWith(shader.fileName) && cleanedPath.pathString.contains(cleanedShaderFolderPath)) {
                        println("A shader file changed: " +  cleanedShaderFolderPath + "\\"+ shader.fileName)
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

        camera.update()
        io.deltaMousePos = Vec2(0.0f)

        // Flush shadersToRecompile
        fileSystem.shadersToRecompile.clear()
        frame++;
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
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS , GLFW.GLFW_TRUE)
        glfwWindow = GLFW.glfwCreateWindow(res.x, res.y, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL)
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
            res.x = _sizex
            res.y = _sizey
            GL11.glViewport(0, 0, _sizex, _sizey)
        }
        GLFW.glfwSetCursorPosCallback(glfwWindow) { wnd: Long, xpos: Double, ypos: Double ->
            val oldMousePos = io.mousePos.copy()
            io.mousePos.vals[0] = xpos.toFloat()
            io.mousePos.vals[1] = ypos.toFloat()
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
                io.LMBDown = if (btn == GLFW.GLFW_MOUSE_BUTTON_1) false else io.RMBDown
                io.RMBDown = if (btn == GLFW.GLFW_MOUSE_BUTTON_2) false else io.RMBDown
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

        gui.setupImGui(glfwWindow)

    }

    protected fun destroy() {
        ImGui.destroyContext()
        Callbacks.glfwFreeCallbacks(glfwWindow)
        GLFW.glfwDestroyWindow(glfwWindow)
        GLFW.glfwTerminate()
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null))!!.free()
    }


}