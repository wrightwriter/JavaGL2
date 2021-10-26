package _0_1.wrightglPackage

import _0_1.mathPackage.IVec2
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL42
import org.lwjgl.opengl.GL43
import java.util.function.Consumer
import kotlin.math.sqrt

class ComputeProgram(_fileNameComp: String) : Program() {
    var compShader: Shader? = null
    var texture: Texture? = null
    var workGroupRes: IVec2 = IVec2(32)
    var workGroupCount: IVec2 = IVec2(32)
    init {
        compShader = Shader(_fileNameComp, Shader.Type.COMPUTE_SHADER, this)
        link()
    }
    override fun link(): Boolean{
        val successfulLinking: Boolean

        val newPid = GL20.glCreateProgram()
        if (newPid <= 0) {
            // TODO Error
            successfulLinking = false
            errorLog = "can't glCreateProgram"
        } else {
            // What if shader PIDs were less than 0?
            GL20.glAttachShader(newPid, compShader!!.pid)
            GL20.glLinkProgram(newPid)

            if (GL20.glGetProgrami(newPid, GL20.GL_LINK_STATUS) == 0) {
                successfulLinking = false
                errorLog = GL20.glGetProgramInfoLog(newPid, 1024)
                System.err.println(errorLog)
            } else {
                GL20.glValidateProgram(newPid)
                if (GL20.glGetProgrami(newPid, GL20.GL_VALIDATE_STATUS) == 0) {
                    // TODO: throw error;
                    successfulLinking = false
                    errorLog = GL20.glGetProgramInfoLog(newPid, 1024)
                    System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(newPid, 1024))
                } else {
                    successfulLinking = true
                }
            }
        }
        if (!successfulLinking) {
            // Delete new program.
            if (newPid > 0) GL20.glDeleteProgram(newPid)
        } else {
            // Replace old pid with new pid.
            GL20.glDeleteProgram(pid)
            pid = newPid
            // Delete new shaders.
            GL20.glDetachShader(pid, compShader!!.pid)
            if (geomShader != null)
                GL20.glDetachShader(newPid, geomShader!!.pid)
        }
        return successfulLinking

    }

    fun dispatch(
        callback: Consumer<ComputeProgram>? = null,
        texture: Texture? = null,
        frameBuffer: FB? = null,
        vertexBuffer: VB? = null,
    ) {

        GL20.glUseProgram(this.pid);

        if (callback != null)
            callback.accept(this)

        if (frameBuffer != null) {
            val tex: Texture = frameBuffer.textures[0]
            tex.setUniform(31, "computeTex", this)
//                Global.engine!!.wgl.setUniformTexture(tex, true)
//                OpSetUniformTextureFramebuffer(frameBuffer, true, 0);
            GL43.glDispatchCompute(
                frameBuffer.textures[0].res.x / workGroupRes.x,
                frameBuffer.textures[0].res.y / workGroupRes.y, 1
            );
        }else if (texture != null) {
            // TODO remove this expensive call
            texture.setUniform(31, "computeTex", this)

            GL43.glDispatchCompute(
                texture.res.x / workGroupRes.x,
                texture.res.y / workGroupRes.y, 1
            );
        } else if (vertexBuffer != null) {
            // TODO: make this shit way cheaper AND BIND SSBO
            val vertCnt: Int = vertexBuffer!!.vertCnt;
            // THIS SHIT IS PROB WRONG
            val szx: Int = kotlin.math.ceil(sqrt((vertCnt / workGroupRes.x) as Float)) as Int;
            val szy: Int = kotlin.math.ceil(sqrt((vertCnt / workGroupRes.y) as Float)) as Int;
            GL43.glDispatchCompute(szx, szy, 1);
        }

        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
    }
}