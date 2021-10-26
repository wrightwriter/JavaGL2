package _0_1.wrightglPackage

import _0_1.enginePackage.Engine
import _0_1.mainPackage.Global
import org.lwjgl.nanovg.NVGColor
import _0_1.mathPackage.Mat4
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.*

//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
class WrightGL(private val engine: Engine) {
    var currProgram: Program? = null
        internal set
    var currReadFB: FB
        internal set
    var currDrawFB: FB
        internal set
    var currTexBindNumber = 0
        internal set
    var nvg: Long
        private set
    var nvgColor: NVGColor
        private set


    private var renderdoc: Renderdoc? = null


    init {

        if (engine.engineSettings.renderdocEnabled) renderdoc = Renderdoc()

        currDrawFB = FB.defaultFB
        currReadFB = FB.defaultFB

        // Setup
        GL.getCapabilities()


        // Nanovg
//        engine.wgl.gl
        nvg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_STENCIL_STROKES or NanoVGGL3.NVG_DEBUG or NanoVGGL3.NVG_ANTIALIAS)
        if (nvg == 0L) {
            assert(false)
        }
        nvgColor = NVGColor.create()

        // Debug
        if (engine.engineSettings.openGLDebugEnabled){
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT)
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS)
            GL43.glDebugMessageCallback({ source: Int, type: Int, id: Int, severity: Int, length: Int, message: Long, userParam: Long ->
                assert(false)
                System.out.printf("ERROOOOOOOOOORRR")
            }, 0)
            GL43.glDebugMessageControl(GL11.GL_DONT_CARE, GL11.GL_DONT_CARE, GL11.GL_DONT_CARE, 0, true)
        }

        // Create VAO
//        int vao = glCreateVertexArrays();
        val vao = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vao)

        // Settings
        GL11.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_STENCIL_TEST)


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


    fun updateMatrices() {
        // Update view matrix
        Global.engine?.viewMatrix = Mat4.identityMatrix

        // Update projection matrix
//        float aspectRatio = (Global.engine!!.resx/Global.engine!!.resy);
//        aspectRatio = aspectRatio/1.0f;
        Global.engine!!.projectionMatrix = Mat4.getPerspectiveMatrix(50f, 0.001f, 1024.0f, engine.resX.toFloat(), engine.resY.toFloat())
    }

    fun setSharedUniforms() {
        // Set uniforms
        val Time = (System.currentTimeMillis() - Global.engine!!.timeStart).toFloat() / 1000f
        Global.engine!!.wgl.setUniform("Time", Time, UniformType.Float)
        val res = floatArrayOf(Global.engine!!.resX.toFloat(), Global.engine!!.resY.toFloat())
        Global.engine!!.wgl.setUniform("R", res, UniformType.Float)
        Global.engine!!.wgl.setUniform("V", Global.engine!!.viewMatrix!!.vals, UniformType.Matrix)
        Global.engine!!.wgl.setUniform("P", Global.engine!!.projectionMatrix!!.vals, UniformType.Matrix)
    }

    enum class UniformType {
        Float, Int, Matrix
    }

    fun useProgram(_program: Program) {
        GL20.glUseProgram(_program.pid)
        currProgram = _program
    }

    fun setUniform(_name: String?, _value: FloatArray, _type: UniformType) {
        val loc = GL20.glGetUniformLocation(currProgram!!.pid, _name)
        if (loc < 0) {
//            System.out.println("Error: loc location not found.");
        }
        val len = _value.size
        if (_type == UniformType.Matrix) {
            GL20.glUniformMatrix4fv(loc, false, _value)
        } else if (_type == UniformType.Int) {
            if (len == 1) {
                GL20.glUniform1i(loc, _value[0].toInt())
            } else if (len == 2) {
                GL20.glUniform2i(loc, _value[0].toInt(), _value[1].toInt())
            } else if (len == 3) {
                GL20.glUniform3i(loc, _value[0].toInt(), _value[1].toInt(), _value[2].toInt())
            }
        } else if (_type == UniformType.Float) {
            if (len == 1) {
                GL20.glUniform1f(loc, _value[0])
            } else if (len == 2) {
                GL20.glUniform2f(loc, _value[0], _value[1])
            } else if (len == 3) {
                GL20.glUniform3f(loc, _value[0], _value[1], _value[2])
            }
        }
    }

    fun setUniform(_name: String?, _value: Float, _type: UniformType) {
        val loc = GL20.glGetUniformLocation(currProgram!!.pid, _name)
        if (loc < 0) {
//            System.out.println("Error: loc location not found.");
        }
        if (_type == UniformType.Matrix) {
            // TODO: THROW ERROR
        } else if (_type == UniformType.Int) {
            GL20.glUniform1i(loc, _value.toInt())
        } else if (_type == UniformType.Float) {
            GL20.glUniform1f(loc, _value)
        }
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