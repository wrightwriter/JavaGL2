package _0_1.wrightglPackage

import _0_1.mainPackage.Global
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL32
import org.lwjgl.opengl.GL43
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList
import kotlin.io.path.exists

class Shader internal constructor(
    _fileName: String,
    _shaderType: Type? = null,
    _program: Program,
    _folderPath: String = Global.engine!!.fileSystem.sketchResourcesFolder
) {
    @JvmField
    var programs: MutableList<Program> = ArrayList()
    var pid = 0
        private set
    var shaderType: Type = Type.FRAGMENT_SHADER
        private set
        get
    val fileName: String
        get
    val folderPath: String = _folderPath
        get
    var errorLog: String? = null


    init {
        if (_shaderType == null){
            val ext: String = _fileName.split(".")[1]
            if (ext == "fp")
                shaderType = Type.FRAGMENT_SHADER
            else if (ext == "vp")
                shaderType = Type.VERTEX_SHADER
            else if (ext == "gp")
                shaderType = Type.GEOMETRY_SHADER
            else if (ext == "cp")
                shaderType = Type.COMPUTE_SHADER
        } else {
            shaderType = _shaderType
        }
        programs.add(_program)
        shadersList.add(this)
        fileName = _fileName
        compile()
    }
    enum class Type(val value: Int) {
        VERTEX_SHADER(GL20.GL_VERTEX_SHADER), FRAGMENT_SHADER(GL20.GL_FRAGMENT_SHADER),
        COMPUTE_SHADER(GL43.GL_COMPUTE_SHADER), GEOMETRY_SHADER( GL32.GL_GEOMETRY_SHADER );
    }

    fun compile(): Boolean {
        if (pid > 0) GL20.glDeleteShader(pid)
        pid = GL20.glCreateShader(shaderType.value)
        var successfulCompilation: Boolean
        try {
            if (pid <= 0) {
                successfulCompilation = false
            } else {
                var path: Path = Paths.get(folderPath + fileName)
                if (path.exists() == false){
                    path = Paths.get(folderPath + "shaders/"+ fileName)
                    if (path.exists() == false){
                        throw IOException()
                    }
                }
                val shaderCode = Files.readString(path, StandardCharsets.US_ASCII)
                GL20.glShaderSource(pid, shaderCode)
                GL20.glCompileShader(pid)
                if (GL20.glGetShaderi(pid, GL20.GL_COMPILE_STATUS) == 0) {
                    errorLog = GL20.glGetShaderInfoLog(pid, 1024)
                    successfulCompilation = false
                } else {
                    errorLog = null
                    successfulCompilation = true
                }
            }
        } catch (e: IOException) {
            errorLog = "Couldn't find shader file."
            successfulCompilation = false
        }
        if (!successfulCompilation) {
            GL20.glDeleteShader(pid)
            pid = 0
            var foundSelf = false
            for (sh in shadersFailedCompilationList) {
                if (sh === this) {
                    foundSelf = true
                    break
                }
            }
            if (!foundSelf) shadersFailedCompilationList.add(this)
        }
        return successfulCompilation
    }

    companion object {
        @JvmField
        var shadersList: MutableList<Shader> = ArrayList()
        @JvmField
        var shadersFailedCompilationList: MutableList<Shader> = ArrayList()
    }

}