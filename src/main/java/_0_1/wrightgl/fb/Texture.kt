package _0_1.wrightgl.fb

import _0_1.main.Glob
import _0_1.math.vector.IVec2
import _0_1.math.vector.IVec3
import _0_1.wrightgl.shader.AbstractProgram
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.glReadPixels
import org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16
import org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32
import org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F
import org.lwjgl.opengl.GL45.glTextureParameteri
import org.lwjgl.opengl.GL45.glTextureStorage2D
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImageWrite.stbi_write_png
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.ArrayList

class Texture {

    var internalFormat: InternalFormat? = null
        private set
    var format: Format? = null
        private set
    var type: Type? = null
        private set
    var pid = 0
        private set
    var res: IVec3 = IVec3(0)
        private set
    var name: String? = null
        private set
    var channelCnt: Int? = 4
        private set
//    var isWritable = false
//        private set

    var fileName: String? = null
        private set
    var folderPath: String? = null
        private set

    var errorLog: String? = null

    companion object{
        var fileTexturesList: MutableList<Texture> = ArrayList()
            private set
        var fileTexturesFailedLoading: MutableList<Texture> = ArrayList()
            private set
    }

    enum class InternalFormat(val value: Int) {
        RGBA32F(GL30.GL_RGBA32F),
        RGBA(GL11.GL_RGBA),
        RGB(GL11.GL_RGB),
        RG(GL30.GL_RG),
        R(GL11.GL_R),
        DEPTH_COMPONENT( GL11.GL_DEPTH_COMPONENT ),
        DEPTH_COMPONENT16( GL14.GL_DEPTH_COMPONENT16),
        DEPTH_COMPONENT32( GL14.GL_DEPTH_COMPONENT32),
        DEPTH_COMPONENT32F( GL30.GL_DEPTH_COMPONENT32F),
//        DEPTH_COMPONENT16( GL14.GL_DEPTH_COMPONENT16),
        ;
    }

    enum class Format(val value: Int) {
        RGBA(GL11.GL_RGBA), RGB(GL11.GL_RGB), RG(GL30.GL_RG), R(GL11.GL_R), DEPTH_COMPONENT(GL11.GL_DEPTH_COMPONENT);
    }

    //        public enum Filter{
    //            RGBA(GL_RGBA),
    //            DEPTH_COMPONENT(GL_DEPTH_COMPONENT) ;
    //            private int value; private Format(int value){ this.value = value; }
    //        }
    enum class Type(val value: Int) {
        FLOAT(GL11.GL_FLOAT), UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE);
    }

    constructor(
        data: FloatBuffer? = null,
        resx: Int = Glob.engine.res.x,
        resy: Int = Glob.engine.res.y,
        internalFormat: InternalFormat? = InternalFormat.RGBA32F,
        format: Format? = Format.RGBA,
        type: Type? = Type.FLOAT
    ) {
        // set res
        res[0] = resx
        res[1] = resy
        res[2] = 1
        if (format == Format.RGBA){
            channelCnt = 4
        } else if (format == Format.RGB){
            channelCnt = 3
        } else if (format == Format.RG){
            channelCnt = 2
        } else if (format == Format.R){
            channelCnt = 1
        }
        pid = GL45.glCreateTextures(GL11.GL_TEXTURE_2D)

        glTextureParameteri(pid , GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        glTextureParameteri(pid , GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        glTextureParameteri(pid , GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        glTextureParameteri(pid , GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
//        glTextureStorage2D(pid,)

        GL46.glTextureStorage2D(
            pid,
            1,
            internalFormat!!.value,
            resx,
            resy
        )

//        if (data != null) {
//            GL46.glTextureSubImage2D(
//                pid,
//                0,
//                0,
//                0,
//                resx,
//                resy,
//                format!!.value,
//                type!!.value,
//                data
//            )
//        } else {
////            val nullPx: FloatArray? = null
////            val nullPx: FloatArray = FloatArray(resx*resy*channelCnt!!)
//            val nullPx: FloatArray? = null
//            GL46.glTextureSubImage2D(
//                pid,
//                0,
//                0,0,
//                resx,
//                resy,
//                format!!.value,
//                type!!.value,
//                nullPx
//            )
//        }


//        if (data != null) GL11.glTexImage2D(
//            GL11.GL_TEXTURE_2D,
//            0,
//            internalFormat!!.value,
//            resx,
//            resy,
//            0,
//            format!!.value,
//            type!!.value,
//            data
//        ) else {
//            val nullPx: FloatArray? = null
//            // TODO: check if this works.
//            GL11.glTexImage2D(
//                GL11.GL_TEXTURE_2D,
//                0,
//                internalFormat!!.value,
//                resx,
//                resy,
//                0,
//                format!!.value,
//                type!!.value,
//                nullPx
//            )
//        }
        // TODO
        // THIS SHIT GONNA BREAK RIGHT HERE
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }
    fun toArray(): Any{
//        var pixels: Array<Float>
//        val pixels: ByteBuffer = BufferUtils.createByteBuffer(res.x * res.y * channelCnt!!);
        val pixels = FloatArray (res.x * res.y * channelCnt!!)

//        glReadPixels(0,0,res.x, res.y, format!!.value, type!!.value, pixels)
//        glReadPixels(0,0,res.x, res.y, format!!.value, type!!.value, pixels)
        GL46.glGetTextureImage(
            pid,
            0,
            format!!.value,
            type!!.value,
            pixels
//            res.x * res.y * channelCnt!! * Float.SIZE_BYTES,
        )

        return pixels
    }
    fun toFile(_filePath:String){
        val pixels: ByteBuffer = this.toArray() as ByteBuffer;
        stbi_write_png(_filePath, res.x, res.y, channelCnt!!, pixels, 0)
    }

    constructor(_filePath: String, _folderPath: String = Glob.engine.fileSystem.sketchResourcesFolder) {
        loadFile(_filePath, _folderPath)
    }
    fun loadFile(_filePath: String, _folderPath: String = Glob.engine.fileSystem.sketchResourcesFolder): Boolean{
        delete()
        var successfulCompilation: Boolean = true

        fileTexturesList.add(this)
        fileName = _filePath
        folderPath = _folderPath
        //            ByteBuffer image = stbi_load(filePath)
        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val channels = BufferUtils.createIntBuffer(1)
        val pixelsData = STBImage.stbi_load(_folderPath + fileName, width, height, channels, 0)
        if (pixelsData == null) {
            errorLog = "Image not found"
            successfulCompilation = false
            // and now we use an invalid texture... hm
        } else {

            pid = GL45.glCreateTextures(GL11.GL_TEXTURE_2D)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, pid)

            type = Type.UNSIGNED_BYTE
            res[0] = width[0]
            res[1] = height[0]
            channelCnt = channels[0]
            if (channelCnt == 3) {
                internalFormat = InternalFormat.RGB
                format = Format.RGB
            } else if (channelCnt == 4) {
                internalFormat = InternalFormat.RGBA
                format = Format.RGBA
            } else {
                errorLog = "Error: (Texture) Unknown number of channesl '" + channelCnt + "'"
                successfulCompilation = false
            }
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, internalFormat!!.value, res[0], res[1],
                0, format!!.value, type!!.value, pixelsData
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        }
        STBImage.stbi_image_free(pixelsData)

        if (!successfulCompilation) {
            var foundSelf = false
            for (texture in fileTexturesFailedLoading) {
                if (texture === this) {
                    foundSelf = true
                    break
                }
            }
            if (!foundSelf) fileTexturesFailedLoading.add(this)
        }
        return successfulCompilation
    }
    fun delete(){
        if (pid > 0)
            GL11.glDeleteTextures(pid)
        if (fileName != null)
            fileTexturesList.remove(this)
    }


    fun setUniformWritableImage(name: String, program: AbstractProgram = Glob.engine.wgl.currProgram!!) {
        Glob.engine.wgl.currImageBindNumber++
        val bindNumber = Glob.engine.wgl.currImageBindNumber
        setUniformWritableImage(bindNumber, name, program)
    }
    fun setUniformWritableImage(_bindNumber: Int = -1, name: String, program: AbstractProgram? = Glob.engine.wgl.currProgram) {
        val loc = GL20.glGetUniformLocation(program!!.pid, name)
        GL42.glBindImageTexture(
            _bindNumber,
            pid,
            0,
            false,
            0,
            GL15.GL_READ_WRITE,
            format!!.value
        )
        GL20.glUniform1i(loc, _bindNumber)
    }
    fun setUniform(name: String, program: AbstractProgram = Glob.engine.wgl.currProgram!!) {
        Glob.engine.wgl.currTexBindNumber++
        val bindNumber = Glob.engine.wgl.currTexBindNumber
        setUniform(bindNumber, name, program)
    }

    fun setUniform(_bindNumber: Int = -1, name: String, program: AbstractProgram = Glob.engine.wgl.currProgram!!) {
//        val bindNumber = _bindNumber
        val loc = GL20.glGetUniformLocation(program.pid, name)
        GL46.glBindTextureUnit(_bindNumber,pid)
        GL20.glUniform1i(loc, _bindNumber)
    }

    fun clear() {
        val zero = IntBuffer.allocate(1)
        zero.put(1)
        zero.rewind()
        GL44.glClearTexImage(pid, 0, format!!.value, type!!.value, zero)
    }
}