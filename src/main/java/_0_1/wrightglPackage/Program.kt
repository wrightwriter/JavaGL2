package _0_1.wrightglPackage

import _0_1.mainPackage.Global
import org.lwjgl.opengl.GL20

open class Program protected constructor() {
    var pid = 0
        protected set

    init {
    }
    constructor(
        _fileNameVert: String,
        _fileNameFrag: String,
        _fileNameGeom: String? = null,
        _folderPath: String = Global.engine!!.fileSystem.sketchResourcesFolder
    ) : this() {
        fragShader = Shader(_fileNameFrag, Shader.Type.FRAGMENT_SHADER, this, _folderPath)
        vertShader = Shader(_fileNameVert, Shader.Type.VERTEX_SHADER, this, _folderPath)
        if (_fileNameGeom != null)
            geomShader = Shader(_fileNameGeom, Shader.Type.GEOMETRY_SHADER, this, _folderPath)
        link()
    }
    constructor(
        _fragShader: Shader,
        _vertShader: Shader,
        _geomShader: Shader? = null,
    ) : this() {
        fragShader = _fragShader;
        vertShader = _vertShader;
        if (_geomShader != null)
            geomShader = _geomShader
        link()
    }
    var fragShader: Shader? = null
        private set
    var vertShader: Shader? = null
        private set
    var geomShader: Shader? = null
        private set
    var errorLog: String? = null
        protected set
    open fun link(): Boolean {
        val successfulLinking: Boolean
        //            if (pid > 0)
//                glDeleteProgram(pid);
        val newPid = GL20.glCreateProgram()
        if (newPid <= 0) {
            // TODO Error
            successfulLinking = false
            errorLog = "can't glCreateProgram"
        } else {
            GL20.glAttachShader(newPid, vertShader!!.pid)
            GL20.glAttachShader(newPid, fragShader!!.pid)

            if (geomShader != null)
                GL20.glAttachShader(newPid, geomShader!!.pid)

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
            GL20.glDetachShader(pid, vertShader!!.pid)
            GL20.glDetachShader(pid, fragShader!!.pid)
            if (geomShader != null)
                GL20.glDetachShader(newPid, geomShader!!.pid)
        }
        return successfulLinking
    }

    fun use() {
        Global.engine!!.wgl.currProgram = this
        Global.engine!!.wgl.currTexBindNumber = 0
        Global.engine!!.wgl.setSharedUniforms()
        GL20.glUseProgram(pid)
    }

}