package _0_1.wrightgl.shader

import _0_1.main.Global
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL32
import org.lwjgl.opengl.GL43
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Stream
import kotlin.io.path.exists

class Shader internal constructor(
    _fileName: String,
    _shaderType: Type? = null,
    _program: AbstractProgram,
    _folderPath: String = Global.engine!!.fileSystem.sketchResourcesFolder,
    val _prependCodeString: String = ""
) {
    var programs: MutableList<AbstractProgram> = ArrayList()
    var pid = 0
        private set
    var shaderType: Type = Type.FRAGMENT_SHADER
        private set
        get
    val fileName: String
        get
    var folderPath: String = _folderPath
        private set
    var errorLog: String? = null

    companion object {
        var shadersList: MutableList<Shader> = ArrayList()
        var shadersFailedCompilationList: MutableList<Shader> = ArrayList()
    }

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

        // try to see if shader is in shaders folder
        var path: Path = Paths.get(folderPath + fileName)
        if (path.exists() == false){
            path = Paths.get(folderPath + "shaders/"+ fileName)
            if (path.exists() ){
                folderPath = path.toString().dropLast(fileName.length)
            }
        }

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
                val path: Path = Paths.get(folderPath + fileName)
                if (!path.exists()){
                    throw IOException()
                }
//                var shaderCode = Files.readString(path, StandardCharsets.UTF_16)
                var shaderCode = Files.readString(path, StandardCharsets.UTF_16)

                try{
                    shaderCode = parseShadercode(shaderCode)

                } catch (e: Exception){
                    System.out.println("Shader file parsing error.")
                }


//                shaderCode.repl

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
            System.err.println(fileName + "\n" + errorLog + "\n" )
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

    private fun parseShadercode(_shaderCode: String): String {
        var shaderCode = _shaderCode
//        shaderCode = versionTagPattern.matcher(shaderCode).replaceFirst("")




        fun concatArrays(arrayA: Array<File>, arrayB: Array<File>): Array<File> {
            return Stream.concat(Arrays.stream(arrayA), Arrays.stream(arrayB))
                .toArray() as Array<File>
        }

        val globalResFolder = Global.engine!!.fileSystem.globalResourcesFolder
        val sketchResFolder = Global.engine!!.fileSystem.sketchResourcesFolder

        val globalIncludes : Array<File> = File(globalResFolder + "shaders/include").listFiles()
        var sketchIncludes :Array<File>? = null
        var sketchIncludesB :Array<File>? = null
        try {
            sketchIncludes = File(sketchResFolder + "include").listFiles()
            sketchIncludesB = File(sketchResFolder + "shaders/include").listFiles()
        } catch(e: Exception){}

        var allIncludeFiles = globalIncludes
        if (sketchIncludes != null)
            allIncludeFiles = concatArrays(allIncludeFiles,sketchIncludes)
        if (sketchIncludesB != null)
            allIncludeFiles = concatArrays(allIncludeFiles,sketchIncludesB)



        for(includeFilePath in allIncludeFiles){
//            val includeFileName: String = includeFilePath.getName().toString()
//            val patternMatcher: Pattern = Pattern.compile("(#include." + includeFileName +")", Pattern.DOTALL )
//            val searchedIncludeString = "#include " + includeFileName;

            val includeFileName: String = includeFilePath.nameWithoutExtension.toString()
//            val anyIncludePattern: Pattern = Pattern.compile("(#include [a-zA-Z0-9.]+)", Pattern.DOTALL )
            val anyIncludePattern: Pattern = Pattern.compile("(#include $includeFileName.include)", Pattern.DOTALL )
            val matcher = anyIncludePattern.matcher(shaderCode)

//            matcher.all
            if ( matcher.find() ){
//                for()
                val includeCodeString = Files.readString(includeFilePath.toPath(), StandardCharsets.UTF_16)
                shaderCode = matcher.replaceFirst(includeCodeString)

//                val foundIncludeString = matcher.group().toString()
//                if (  foundIncludeString.contains(includeFileName) ){
//                }
            }

//            val lines = Files.readAllLines(Paths.get("leaders.txt"))
//            for (line in lines) {
//                if (line.contains(name)) {
//                    println(line)
//                }
//            }


        }

        // parse Shader
        val versionTagPattern = Pattern.compile("(#version .*)\\R")
        shaderCode = versionTagPattern.matcher(shaderCode).replaceFirst("")
//        val allIncludes =
//            concatArrays(
//                concatArrays(globalIncludes,sketchIncludes),
//                sketchIncludesB
//            )
//        for (includeFile in allIncludes){
//        }



        shaderCode = "#version 460\n" + _prependCodeString + shaderCode
        return shaderCode
    }


}