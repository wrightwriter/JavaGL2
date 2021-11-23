package _0_1.wrightgl.shader

import _0_1.main.Global
import _0_1.math.vector.IVec3
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL46

class ProgCompute(
    _fileNameComp: String,
    _folderPath: String = Global.engine!!.fileSystem.sketchResourcesFolder
) : AbstractProgram() {
    var localGroupSize: IVec3 = IVec3(0,0,0)

    var compShader: Shader? = null
    init {
        compShader = Shader(_fileNameComp, Shader.Type.COMPUTE_SHADER, this, _folderPath)
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
                successfulLinking = true
                if (GL20.glGetProgrami(newPid, GL20.GL_VALIDATE_STATUS) == 0) {
                    errorLog = GL20.glGetProgramInfoLog(newPid, 1024)
                    System.err.println("Warning validating Shader code: \n " + GL20.glGetProgramInfoLog(newPid, 1024) + "\n")
                }
            }
        }
        if (successfulLinking) {
            // Replace old pid with new pid.
            GL20.glDeleteProgram(pid)
            pid = newPid
            // Delete new shaders.
            GL20.glDetachShader(pid, compShader!!.pid)
            // Get local group sz.
            GL46.glGetProgramiv(pid, GL46.GL_COMPUTE_WORK_GROUP_SIZE, localGroupSize.vals);
        } else {
            // Delete new program.
            if (newPid > 0) GL20.glDeleteProgram(newPid)
        }
        return successfulLinking

    }
}