package mainPackage;


import WrightGL.Geometry;
import WrightGL.WrightGL;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2ES3.GL_DEPTH;

//import framework.Semantic;


public class Engine implements GLEventListener, KeyListener {
    // ----------------- VARS ----------------- //
    public static GLWindow window;

    public WrightGL WGL;
    private Animator animator;

    public float[] viewMatrix, projectionMatrix;
    public long start;

    public WrightGL.Framebuffer screenFramebuffer;
    public WrightGL.Framebuffer compositeFramebuffer;
    public WrightGL.Framebuffer postFramebuffer;

    private WrightGL.Program cubeProgram, compositeProgram, postProgram;

    List<Thing> things = new ArrayList<Thing>();


    // ----------------- INIT ----------------- //
    public Engine() {
    }
    public void setup(){
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        window = GLWindow.create(glCapabilities);
        window.setTitle("JavaWright");
        window.setSize(1024,768);
        window.setContextCreationFlags(GLContext.CTX_OPTION_DEBUG);
        window.setVisible(true);
        window.addGLEventListener(this);
        window.addKeyListener(this);

        animator = new Animator(window);
        animator.start();

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(WindowEvent e) {
                animator.stop();
                System.exit(1);
            }
        });

    }
    @Override
    public void init(GLAutoDrawable drawable){
        // GL stuff
        Global.gl = drawable.getGL().getGL4();
        WGL = new WrightGL();

        // Framebuffers
        screenFramebuffer = new WrightGL.Framebuffer(
                window.getWidth(), window.getHeight(), 2, true,
                null, null, null, false);
        compositeFramebuffer = new WrightGL.Framebuffer(
                window.getWidth(), window.getHeight(), 2, true,
                null, null, null, false);
        postFramebuffer = new WrightGL.Framebuffer(
                window.getWidth(), window.getHeight(), 2, true,
                null, null, null, false);

        // Matrices
        viewMatrix = new float[16];
        projectionMatrix = new float[16];

        // Shaders
        cubeProgram = new WrightGL.Program("cube", "cube");
        compositeProgram = new WrightGL.Program("quad", "composite");
        postProgram = new WrightGL.Program("quad", "post");

        // Vertex Buffers
        int cubeSignature[] = {3,3};
        WrightGL.VertexBuffer cubeVertexBuffer = new WrightGL.VertexBuffer(
                Geometry.cubeNormals, cubeSignature, WrightGL.VertexBuffer.PrimitiveType.TRIANGLES_STRIP);
        int quadSignature[] = {3};
        WrightGL.VertexBuffer quadVertexBuffer = new WrightGL.VertexBuffer(
                Geometry.triangleStripQuad, quadSignature, WrightGL.VertexBuffer.PrimitiveType.TRIANGLES_STRIP
        );

        // Things

        Thing cube = new Thing(cubeProgram, cubeVertexBuffer );
        things.add(cube);
        cube.setCallback((thing) -> {
            // Update model matrix
            long now = System.currentTimeMillis();
            float diff = (float) (now - Global.engine.start) / 1_000;

            float[] scale = FloatUtil.makeScale(new float[16], true, 0.5f, 0.5f, 0.5f);
            float[] translate = FloatUtil.makeTranslation(new float[16],0, false, 0,0, -4 );
            float[] rotateZ = FloatUtil.makeRotationAxis(new float[16], 0, diff, 0f, 0f, 1f, new float[3]);
            rotateZ = FloatUtil.multMatrix(
                    FloatUtil.makeRotationAxis(new float[16], 0, 0f, 0f, diff, 1f, new float[3]),
                    rotateZ);


            float[] model = FloatUtil.multMatrix(rotateZ, translate);
            model = FloatUtil.multMatrix(scale, model);

            for (int i = 0; i < 16; i++)
                thing.modelMatrix[i] = model[i];
        });

        Thing composite = new Thing( compositeProgram, quadVertexBuffer);
        Thing post = new Thing( postProgram, quadVertexBuffer);


        start = System.currentTimeMillis();
    }



    // ----------------- DRAW ----------------- //
    @Override
    public void display(GLAutoDrawable drawable){
        GL4 gl = drawable.getGL().getGL4();

        WGL.updateMatrices();

        // Bind framebuffer
        gl.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, screenFramebuffer.pid);
        final int texAttachmentsCnt = screenFramebuffer.textures.size();
        final int[] attachments = new int[texAttachmentsCnt];
        for (int i = 0; i < texAttachmentsCnt; i++){
            attachments[i] = GL_COLOR_ATTACHMENT0 + i;
        }
        gl.glDrawBuffers(1,attachments, 0);

        // Clear screen
        FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(4);
        FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(1);
        gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, 1f).put(1, .5f).put(2, 0f).put(3, 1f));
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));

        // Draw stuff
        for(Thing thing: things){
            thing.render(null);
        }

        // Blit framebuffer
        WrightGL.Framebuffer.blit(screenFramebuffer, null);
    }
    // ----------------- INPUT ----------------- //
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height){

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            new Thread(() -> {
                window.destroy();
            }).start();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
    // ----------------- DESTROY ----------------- //
    @Override
    public void dispose(GLAutoDrawable drawable){

    }
}
