package enginePackage;


import imgui.ImGui;

import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import mathPackage.Vec2;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import wrightglPackage.Renderdoc;
import wrightglPackage.WrightGL;
import mathPackage.Mat4;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//import framework.Semantic;

import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.*;


abstract public class Engine {
    // ----------------- VARS ----------------- //
    final protected class Settings{
        public int resx = 1024;
        public int resy = 768;
        public boolean renderdocEnabled = true;

    }
    protected Settings engineSettings;

    public static long glfwWindow;
    protected int resx;
    final public int getResX(){return resx;}
    protected int resy;
    final public int getResY(){return resy;}

    public long timeStart = 0;
    public double time = 0.0f;
    public double deltaTime = 0.0f;

    final protected IO io = new IO();

    protected final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    protected final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    static private final ArrayList<IO.Key.State> keysToUnpress = new ArrayList<>();
    static private final ArrayList<IO.Key.State> keysToUnrelease = new ArrayList<>();
    private Renderdoc renderdoc;
    public WrightGL wgl;

    public Mat4 viewMatrix, projectionMatrix;

    protected List<Thing> thingQueue = new ArrayList<>();


    // ----------------- INIT ----------------- //
    public Engine() {
        initSettings();

        if (engineSettings.renderdocEnabled)
            renderdoc = new Renderdoc();
        resx = engineSettings.resx;
        resy = engineSettings.resy;


        setupGLFW();
        wgl = new WrightGL(this);
    }

    final public void start() {
        timeStart = System.currentTimeMillis();
        setup();
        loop();
    }

    abstract protected void initSettings();

    protected abstract void setup();

    abstract public void display();

    private void setupGLFW() {
//        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        GLFWErrorCallback.createPrint(System.err).set();

//        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindow = glfwCreateWindow(resx, resy, "Hello World!", NULL, NULL);
        if (glfwWindow == NULL)
            throw new RuntimeException("Failed to create GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(glfwWindow, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int glfwKey, int scancode, int action, int mods) {
                try {
                    if (action == GLFW_REPEAT) {
                        return;
                    }
                    IO.Key keyIdx = IO.Key.getKey(glfwKey);
                    IO.Key.State key = io.keyboard.get(keyIdx);
                    if (action == GLFW_PRESS) {
                        if (key.Down == false) {
                            key.Press = true;
                            keysToUnpress.add(key);
                        }
                        key.Up = false;
                        key.Down = true;
                    } else if (action == GLFW_RELEASE) {
                        key.Release = true;
                        keysToUnrelease.add(key);
                        key.Up = true;
                        key.Down = false;
                    }
                } catch (Exception e) {
                    assert false;
                }

            }
        });
        glfwSetWindowSizeCallback(glfwWindow, (wnd, _sizex, _sizey) -> {
            resx = _sizex;
            resy = _sizey;

             org.lwjgl.opengl.GL11.glViewport(0, 0, _sizex, _sizey);
        });
        glfwSetCursorPosCallback(glfwWindow, (wnd, xpos, ypos) -> {
            Vec2 oldMousePos = io.mousePos.copy();
            io.mousePos.val[0] = (float) xpos;
            io.mousePos.val[1] = (float) ypos;
            io.deltaMousePos = io.mousePos.sub(oldMousePos);
        });
        glfwSetMouseButtonCallback(glfwWindow, (wnd, btn, action, mode) -> {
            if (action == GLFW_PRESS) {
                io.LMBPress = btn == GLFW_MOUSE_BUTTON_1 ? true : io.LMBPress;
                io.RMBPress = btn == GLFW_MOUSE_BUTTON_2 ? true : io.RMBPress;
                io.LMBDown = btn == GLFW_MOUSE_BUTTON_1 ? true : io.RMBDown;
                io.RMBDown = btn == GLFW_MOUSE_BUTTON_2 ? true : io.RMBDown;
            } else if (action == GLFW_RELEASE) {
                io.LMBRelease = btn == GLFW_MOUSE_BUTTON_1 ? true : io.LMBRelease;
                io.RMBRelease = btn == GLFW_MOUSE_BUTTON_2 ? true : io.RMBRelease;
                io.LMBUp = btn == GLFW_MOUSE_BUTTON_1 ? true : io.LMBUp;
                io.RMBUp = btn == GLFW_MOUSE_BUTTON_2 ? true : io.RMBUp;
            }

        });
        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(glfwWindow, pWidth, pHeight);


            // Center the window
            glfwSetWindowPos(
                    glfwWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically
        GL.createCapabilities();

        // Make the window visible
        glfwShowWindow(glfwWindow);

        ImGui.createContext();
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init("#version 130" );

        ImGui.styleColorsDark();
        ImGuiStyle style = ImGui.getStyle();
        style.setWindowRounding(0.0f);

        // ImGui theme.
        float[][] colors = ImGui.getStyle().getColors();
        colors[ImGuiCol.Text] = new float[]{1.00f, 1.00f, 1.00f, 0.95f};
        colors[ImGuiCol.TextDisabled] = new float[]{0.50f, 0.50f, 0.50f, 1.00f};
        colors[ImGuiCol.WindowBg] = new float[]{0.13f, 0.12f, 0.12f, 1.00f};
        colors[ImGuiCol.ChildBg] = new float[]{1.00f, 1.00f, 1.00f, 0.00f};
        colors[ImGuiCol.PopupBg] = new float[]{0.05f, 0.05f, 0.05f, 0.94f};
        colors[ImGuiCol.Border] = new float[]{0.53f, 0.53f, 0.53f, 0.46f};
        colors[ImGuiCol.BorderShadow] = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
        colors[ImGuiCol.FrameBg] = new float[]{0.00f, 0.00f, 0.00f, 0.85f};
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.22f, 0.22f, 0.22f, 0.40f};
        colors[ImGuiCol.FrameBgActive] = new float[]{0.16f, 0.16f, 0.16f, 0.53f};
        colors[ImGuiCol.TitleBg] = new float[]{0.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.00f, 0.00f, 0.00f, 0.51f};
        colors[ImGuiCol.MenuBarBg] = new float[]{0.12f, 0.12f, 0.12f, 1.00f};
        colors[ImGuiCol.ScrollbarBg] = new float[]{0.02f, 0.02f, 0.02f, 0.53f};
        colors[ImGuiCol.ScrollbarGrab] = new float[]{0.31f, 0.31f, 0.31f, 1.00f};
        colors[ImGuiCol.ScrollbarGrabHovered] = new float[]{0.41f, 0.41f, 0.41f, 1.00f};
        colors[ImGuiCol.ScrollbarGrabActive] = new float[]{0.48f, 0.48f, 0.48f, 1.00f};
        colors[ImGuiCol.CheckMark] = new float[]{0.79f, 0.79f, 0.79f, 1.00f};
        colors[ImGuiCol.SliderGrab] = new float[]{0.48f, 0.47f, 0.47f, 0.91f};
        colors[ImGuiCol.SliderGrabActive] = new float[]{0.56f, 0.55f, 0.55f, 0.62f};
        colors[ImGuiCol.Button] = new float[]{0.50f, 0.50f, 0.50f, 0.63f};
        colors[ImGuiCol.ButtonHovered] = new float[]{0.67f, 0.67f, 0.68f, 0.63f};
        colors[ImGuiCol.ButtonActive] = new float[]{0.4f, 0.26f, 0.26f, 0.63f};
        colors[ImGuiCol.Header] = new float[]{0.54f, 0.54f, 0.54f, 0.58f};
        colors[ImGuiCol.HeaderHovered] = new float[]{0.64f, 0.65f, 0.65f, 0.80f};
        colors[ImGuiCol.HeaderActive] = new float[]{0.25f, 0.25f, 0.25f, 0.80f};
        colors[ImGuiCol.Separator] = new float[]{0.58f, 0.58f, 0.58f, 0.50f};
        colors[ImGuiCol.SeparatorHovered] = new float[]{0.81f, 0.81f, 0.81f, 0.64f};
        colors[ImGuiCol.SeparatorActive] = new float[]{0.81f, 0.81f, 0.81f, 0.64f};
        colors[ImGuiCol.ResizeGrip] = new float[]{0.87f, 0.87f, 0.87f, 0.53f};
        colors[ImGuiCol.ResizeGripHovered] = new float[]{0.87f, 0.87f, 0.87f, 0.74f};
        colors[ImGuiCol.ResizeGripActive] = new float[]{0.87f, 0.87f, 0.87f, 0.74f};
        colors[ImGuiCol.Tab] = new float[]{0.01f, 0.01f, 0.01f, 0.86f};
        colors[ImGuiCol.TabHovered] = new float[]{0.29f, 0.29f, 0.29f, 1.00f};
        colors[ImGuiCol.TabActive] = new float[]{0.31f, 0.31f, 0.31f, 1.00f};
        colors[ImGuiCol.TabUnfocused] = new float[]{0.02f, 0.02f, 0.02f, 1.00f};
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.19f, 0.19f, 0.19f, 1.00f};
        //colors[ImGuiCol.DockingPreview] = new float[]{0.38f, 0.48f, 0.60f, 1.00f};
        //colors[ImGuiCol.DockingEmptyBg] = new float[]{0.20f, 0.20f, 0.20f, 1.00f};
        colors[ImGuiCol.PlotLines] = new float[]{0.61f, 0.61f, 0.61f, 1.00f};
        colors[ImGuiCol.PlotLinesHovered] = new float[]{0.68f, 0.68f, 0.68f, 1.00f};
        colors[ImGuiCol.PlotHistogram] = new float[]{0.90f, 0.77f, 0.33f, 1.00f};
        colors[ImGuiCol.PlotHistogramHovered] = new float[]{0.87f, 0.55f, 0.08f, 1.00f};
        colors[ImGuiCol.TextSelectedBg] = new float[]{0.47f, 0.60f, 0.76f, 0.47f};
        colors[ImGuiCol.DragDropTarget] = new float[]{0.58f, 0.58f, 0.58f, 0.90f};
        colors[ImGuiCol.NavHighlight] = new float[]{0.60f, 0.60f, 0.60f, 1.00f};
        colors[ImGuiCol.NavWindowingHighlight] = new float[]{1.00f, 1.00f, 1.00f, 0.70f};
        colors[ImGuiCol.NavWindowingDimBg] = new float[]{0.80f, 0.80f, 0.80f, 0.20f};
        colors[ImGuiCol.ModalWindowDimBg] = new float[]{0.80f, 0.80f, 0.80f, 0.35f};
        colors[ImGuiCol.WindowBg] = new float[]{0.05f, 0.05f, 0.05f, 0.5f};
        colors[ImGuiCol.PopupBg] = new float[]{0.05f, 0.05f, 0.05f, 0.5f};
        colors[ImGuiCol.TitleBg] = new float[]{0.05f, 0.05f, 0.05f, 0.5f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.05f, 0.05f, 0.05f, 0.5f};
        colors[ImGuiCol.Border][3] = 0.0f;

    }


    private void startFrame() {
        GL32.glClearColor(0.05f,0.1f,0.f,1.f);
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    // ----------------- DRAW ----------------- //
    public void loop() {
        double secsPerUpdate = 1.0d / 60.0d;
        double previousT = glfwGetTime();
        double secsToCatchUp = 0.0;
        while (true) {
            startFrame();
            time = glfwGetTime();
            deltaTime = time - previousT;
            previousT = time;
            secsToCatchUp += deltaTime;

            // input
//            while (secsToCatchUp >= secsPerUpdate){
//                // logic here i guess?
//                secsToCatchUp -= secsPerUpdate;
//            }
            display();
            drawImGui();
            finishFrame();
            // TODO: implement sync.
//            sync(currentT);
        }
    }

    protected void drawImGui(){}


    private void finishFrame() {
        // Input
        for (IO.Key.State keyState : keysToUnpress) {
            keyState.Press = false;
        }
        for (IO.Key.State keyState : keysToUnrelease) {
            keyState.Release = false;
        }
        io.LMBPress = false;
        io.RMBPress = false;
        io.LMBRelease = false;
        io.RMBRelease = false;

        // ImGui
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
        // GLFW
        glfwSwapBuffers(glfwWindow);
        glfwPollEvents();
    }

    final protected void destroy(){
        ImGui.destroyContext();

        Callbacks.glfwFreeCallbacks(glfwWindow);
        GLFW.glfwDestroyWindow(glfwWindow);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

}
