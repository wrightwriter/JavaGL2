package _0_1.wrightglPackage

import _0_1.mainPackage.Global
import _0_1.mathPackage.IVec2
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.lwjgl.stb.STBImage
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
    var res: IVec2 = IVec2(0)
        private set
    var name: String? = null
        private set
    var isWritable = false
        private set

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
        RGBA32F(GL30.GL_RGBA32F), RGBA(GL11.GL_RGBA), RGB(GL11.GL_RGB), RG(GL30.GL_RG), R(GL11.GL_R), DEPTH_COMPONENT(
            GL11.GL_DEPTH_COMPONENT
        );
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
        data: FloatBuffer?, resx: Int = Global.engine!!.resX, resy: Int = Global.engine!!.resY, internalFormat: InternalFormat? = InternalFormat.RGBA32F,
        format: Format? = Format.RGBA, type: Type? = Type.FLOAT) {
        // set res
        res[0] = resx
        res[1] = resy
        pid = GL45.glCreateTextures(GL11.GL_TEXTURE_2D)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, pid)
        if (data != null) GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            internalFormat!!.value,
            resx,
            resy,
            0,
            format!!.value,
            type!!.value,
            data
        ) else {
            val nullPx: FloatArray? = null
            // TODO: check if this works.
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                internalFormat!!.value,
                resx,
                resy,
                0,
                format!!.value,
                type!!.value,
                nullPx
            )
        }
        // TODO
        // THIS SHIT GONNA BREAK RIGHT HERE
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    constructor(_filePath: String, _folderPath: String = Global.engine!!.fileSystem.sketchResourcesFolder) {
        loadFile(_filePath, _folderPath)
    }
    fun loadFile(_filePath: String, _folderPath: String = Global.engine!!.fileSystem.sketchResourcesFolder): Boolean{
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
            if (channels[0] == 3) {
                internalFormat = InternalFormat.RGB
                format = Format.RGB
            } else if (channels[0] == 4) {
                internalFormat = InternalFormat.RGBA
                format = Format.RGBA
            } else {
                errorLog = "Error: (Texture) Unknown number of channesl '" + channels[0] + "'"
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
            for (texture in Texture.fileTexturesFailedLoading) {
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

    // IF CUSTOM PROGRAM, MUST USE IT FIRST!
    fun setUniform(bindNumber: Int = -1, name: String?, program: Program? = Global.engine!!.wgl.currProgram) {
        var bindNumber = bindNumber
        if (bindNumber < 0) {
            Global.engine!!.wgl.currTexBindNumber++
            bindNumber = Global.engine!!.wgl.currTexBindNumber
        }
        if (program != null){
            val loc = GL20.glGetUniformLocation(program.pid, name)
            if (isWritable) GL42.glBindImageTexture(
                bindNumber,
                pid,
                0,
                false,
                0,
                GL15.GL_READ_WRITE,
                format!!.value
            ) else {
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + bindNumber)
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, pid)
            }
            GL20.glUniform1i(loc, bindNumber)
        } else {
            // TODO: Error
            assert(false)
        }
    }

    fun clear() {
        val zero = IntBuffer.allocate(1)
        zero.put(1)
        zero.rewind()
        GL44.glClearTexImage(pid, 0, format!!.value, type!!.value, zero)
    }
}