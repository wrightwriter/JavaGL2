package _0_1.wrightgl

import _0_1.engine.Constants
import _0_1.engine.Engine
import _0_1.math.Geometry
import _0_1.math.Mat4
import _0_1.math.vector.IVec
import _0_1.math.vector.Vec
import _0_1.wrightgl.buffer.UBO
import org.lwjgl.nanovg.NVGColor
import _0_1.wrightgl.buffer.VB
import _0_1.wrightgl.fb.FB
import _0_1.wrightgl.shader.AbstractProgram
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE
import org.lwjgl.opengl.GL43.*

//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class WrightGL(
    private val engine: Engine
    ) {
    var currProgram: AbstractProgram? = null
        internal set
    var currReadFB: FB
        internal set
    var currDrawFB: FB
        internal set
    var currTexBindNumber = 0
        internal set
    var currImageBindNumber = 0
        internal set
    var currSSBOBindNumber = 0
        internal set

    var blendEnabled = true
        internal set
    var currBlendFunc = arrayOf(BlendMode.SRC_ALPHA, BlendMode.ONE_MINUS_SRC_ALPHA)
        internal set

    var nvg: Long
        private set
    var nvgColor: NVGColor
        private set

    lateinit var uboShared: UBO
        private set




    init {


        FB.defaultFB

        currDrawFB = FB.defaultFB
        currReadFB = FB.defaultFB

        // Setup
        GL.getCapabilities()

        // Debug
        if (engine.engineSettings.openGLDebugEnabled){
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT)
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS)
            GL43.glDebugMessageCallback(
                { source: Int, type: Int, id: Int, severity: Int, length: Int, message: Long, userParam: Long ->
                    assert(false)
                    System.out.printf("ERROOOOOOOOOORRR")
                }, 0)
//            GL43.glDebugMessageControl(GL11.GL_DONT_CARE, GL11.GL_DONT_CARE, GL11.GL_DONT_CARE, 0, true)
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_NOTIFICATION, 0,  true);
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_LOW, 0,  true);
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_MEDIUM, 0, true);
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_HIGH, 0,  true);
        }

        // Nanovg
//        engine.wgl.gl
        nvg = NanoVGGL3.nvgCreate(
            NanoVGGL3.NVG_STENCIL_STROKES or
                NanoVGGL3.NVG_DEBUG )
//                NanoVGGL3.NVG_ANTIALIAS)
        if (nvg == 0L) {
            assert(false)
        }
        nvgColor = NVGColor.create()


        // Create VAO
//        int vao = glCreateVertexArrays();
        val vao = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vao)

        VB.quadVB = VB(Geometry.quadTriangleStrip, intArrayOf(2), VB.PrimitiveType.TRIANGLES_STRIP)

        // Settings
        GL11.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

        glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_STENCIL_TEST)

        if (blendEnabled)
            glEnable(GL_BLEND)
        else
            glDisable(GL_BLEND)

        glBlendFunc(currBlendFunc[0].value, currBlendFunc[1].value)
//            GL11.glEnable(GL_TESS_CONTROL_SHADER)

        glPrimitiveRestartIndex(Constants.primitiveRestartIndex)
        glEnable(GL_PRIMITIVE_RESTART)

        glEnable(GL_PROGRAM_POINT_SIZE);

        uboShared = UBO("uboShared")


//        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.POSITION, Semantic.Stream.A);
//        gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.COLOR, Semantic.Stream.A);
//
//        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.POSITION, 2, GL_FLOAT, false, 0);
//        gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.COLOR, 3, GL_FLOAT, false, 2 * 4);
//
//        gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.POSITION);
//        gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.COLOR);
//
//        gl.glVertexArrayElementBuffer(vertexArrayName.get(0), bufferName.get(Buffer.ELEMENT));
//        gl.glVertexArrayVertexBuffer(vertexArrayName.get(0), Semantic.Stream.A, bufferName.get(Buffer.VERTEX), 0, (2 + 3) * 4);


//        IntBuffer vertexBuffer = GLBuffers.newDirectIntBuffer(1);
//        // map the transform buffers and keep them mapped
//        ByteBuffer globalMatricesPointer = gl.glMapNamedBufferRange(
//                bufferName.get(Buffer.GLOBAL_MATRICES),
//                0,
//                16 * 4 * 2,
//                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT); // flags
//
//        modelMatrixPointer = gl.glMapNamedBufferRange(
//                bufferName.get(Buffer.MODEL_MATRIX),
//                0,
//                16 * 4,
//                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
    }

    enum class BlendMode(val value: Int){
        SRC_ALPHA(GL_SRC_ALPHA),
        DST_ALPHA(GL_DST_ALPHA),
        SRC_COLOR(GL_SRC_COLOR),
        DST_COLOR(GL_DST_COLOR),
        ONE_MINUS_SRC_ALPHA(GL_ONE_MINUS_SRC_ALPHA),
        ONE(GL_ONE),
        ZERO(GL_ZERO),
    }

    fun enableBlend(src: BlendMode = BlendMode.SRC_ALPHA, dst: BlendMode = BlendMode.ONE_MINUS_SRC_ALPHA){
        blendEnabled = true;
        glEnable(GL_BLEND)
        currBlendFunc[0] = src;
        currBlendFunc[1] = dst;
        glBlendFunc(src.value, dst.value)
    }
    fun disableBlend(){
        blendEnabled = false;
        glDisable(GL_BLEND)
    }

    fun useProgram(_program: AbstractProgram) {
        GL20.glUseProgram(_program.pid)
        currProgram = _program
    }
    fun setUniform(_name: String?, _value: IntArray) {
        val loc = GL20.glGetUniformLocation(currProgram!!.pid, _name)
        val len = _value.size
        if (len == 1) {
            GL20.glUniform1i(loc, _value[0])
        } else if (len == 2) {
            GL20.glUniform2i(loc, _value[0], _value[1])
        } else if (len == 3) {
            GL20.glUniform3i(loc, _value[0], _value[1], _value[2])
        } else if (len == 4) {
            GL20.glUniform4i(loc, _value[0], _value[1], _value[2], _value[3])
        }

    }
    fun setUniform(_name: String?, _value: FloatArray) {
        val loc = GL20.glGetUniformLocation(currProgram!!.pid, _name)
        val len = _value.size
        if (len == 1) {
            GL20.glUniform1f(loc, _value[0])
        } else if (len == 2) {
            GL20.glUniform2f(loc, _value[0], _value[1])
        } else if (len == 3) {
            GL20.glUniform3f(loc, _value[0], _value[1], _value[2])
        } else if (len == 4) {
            GL20.glUniform4f(loc, _value[0], _value[1], _value[2], _value[3])
        }
    }

    fun setUniform(_name: String?, _value: Boolean) {
        val loc = GL20.glGetUniformLocation(currProgram!!.pid, _name)
        GL20.glUniform1i(loc, if (_value == true) 1 else 0)
    }
    fun setUniform(_name: String?, _value: Vec) {
        setUniform(_name, _value.vals)
    }
    fun setUniform(_name: String?, _value: IVec) {
        setUniform(_name , _value.vals)
    }

    fun setUniform(_name: String?, _value: Mat4) {
        val loc = GL20.glGetUniformLocation(currProgram!!.pid, _name)
        GL46.glUniformMatrix4fv(loc, false, _value.vals)
    }

    fun setUniform(_name: String?, _value: Float) {
        val loc = GL20.glGetUniformLocation(currProgram!!.pid, _name)
        GL20.glUniform1f(loc, _value)
    }
    fun setUniform(_name: String?, _value: Int) {
        val loc = GL20.glGetUniformLocation(currProgram!!.pid, _name)
        GL20.glUniform1i(loc, _value)
    }

//    fun setUniformTexture(
//        texture: Texture,
//        isWritable: Boolean = false,
//        bindNumber: Int = -1,
//        uniformName: String?) {
//
//        val loc = GL20.glGetUniformLocation(currProgram!!.pid, uniformName)
//        GL42.glBindImageTexture(bindNumber, texture.pid, 0, false, 0, GL15.GL_READ_WRITE, texture.format!!.value)
//        GL13.glActiveTexture(texture.pid)
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.pid)
//        GL20.glUniform1i(loc, bindNumber)
//    }


}