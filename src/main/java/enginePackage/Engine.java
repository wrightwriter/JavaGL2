package enginePackage;


import wrightglPackage.Renderdoc;
import wrightglPackage.WrightGL;
import mathPackage.Mat4;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;


//import framework.Semantic;

import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.system.*;



abstract public class Engine {
    // ----------------- VARS ----------------- //
    public static long window;
    public int resx = 1024;
    public int resy = 768;
    public double time = 0.0f;
    public double deltaTime = 0.0f;

    private Renderdoc renderdoc;
    public WrightGL WGL;

    public Mat4 viewMatrix, projectionMatrix;
    public long start;



    protected List<Thing> things = new ArrayList<Thing>();


    // ----------------- INIT ----------------- //
    public Engine() {
        renderdoc = new Renderdoc();
        setupGLFW();
        WGL = new WrightGL(this);
    }
    public void start(){
        start = System.currentTimeMillis();
        setup();
        loop();
    }
    protected void settings(){

    };
    protected abstract void setup();
    public void setupGLFW(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(resx,resy, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create GLFW window");
// Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });
        glfwSetWindowSizeCallback(window,(wnd, _sizex, _sizey)->{
            resx = _sizex;
            resy = _sizey;
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }


    // ----------------- DRAW ----------------- //
    public void loop(){
        double secsPerUpdate = 1.0d / 60.0d;
        double previousT = glfwGetTime();
        double secsToCatchUp = 0.0;
        while(true){
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
            // TODO: implement sync.
//            sync(currentT);
        }
    }
    abstract public void display();
    // ----------------- INPUT ----------------- //

//    public void keyPressed(KeyEvent e) {
//        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
//            new Thread(() -> {
//                window.destroy();
//            }).start();
//        }
//    }
//    public void keyReleased(KeyEvent e) {
//    }
//    // ----------------- DESTROY ----------------- //
//    public void dispose(GLAutoDrawable drawable){
//
//    }
}
