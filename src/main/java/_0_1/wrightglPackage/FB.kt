package _0_1.wrightglPackage

import _0_1.mainPackage.Global
import _0_1.mathPackage.IVec2
import _0_1.mathPackage.Vec4
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL45
import java.util.ArrayList

class FB {
    @JvmField
    var pid = -1
    var textures: MutableList<Texture> = ArrayList()
    var name: String? = null
    var depthTexture: Texture? = null

    enum class Target(val value: Int) {
        FRAMEBUFFER(GL30.GL_FRAMEBUFFER),
        DRAW_FRAMEBUFFER(GL30.GL_DRAW_FRAMEBUFFER),
        READ_FRAMEBUFFER(GL30.GL_READ_FRAMEBUFFER);
    }

    enum class FbBitmask(val value: Int) {
        COLOR_BUFFER_BIT(GL11.GL_COLOR_BUFFER_BIT),
        DEPTH_BUFFER_BIT(GL11.GL_DEPTH_BUFFER_BIT),
        ACCUM_BUFFER_BIT(GL11.GL_ACCUM_BUFFER_BIT),
        STENCIL_BUFFER_BIT(GL11.GL_STENCIL_BUFFER_BIT);
        companion object {
            fun toGLEnum(bitmasks: Array<FbBitmask>): Int {
                var bitmask = 0
                for (m in bitmasks) {
                    bitmask = bitmask or m.value
                }
                return bitmask
            }
        }
    }

    // Cursed.
    // Constructor for deault FB only.
    constructor(_pid: Int) {
        pid = _pid
    }

    // Constructor for FB

    constructor(
        _width: Int = Global.engine!!.resX, _height: Int = Global.engine!!.resY, texturesCnt: Int = 1, hasDepth: Boolean = false,
        internalFormat: Texture.InternalFormat = Texture.InternalFormat.RGBA32F, format: Texture.Format = Texture.Format.RGBA, type: Texture.Type = Texture.Type.FLOAT,
        renderbuffer: Boolean = false) {

        val globalDrawFB = Global.engine!!.wgl.currDrawFB
        val globalReadFB = Global.engine!!.wgl.currReadFB
        pid = GL45.glCreateFramebuffers()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, pid)
        if (hasDepth) {
            depthTexture = Texture(
                null,
                _width,
                _height,
                Texture.InternalFormat.DEPTH_COMPONENT,
                Texture.Format.DEPTH_COMPONENT,
                type
            )
            GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_ATTACHMENT,
                GL11.GL_TEXTURE_2D,
                depthTexture!!.pid,
                0
            )
        }
        for (i in 0 until texturesCnt) {
            textures.add(Texture(null, _width, _height, internalFormat, format, type))
            GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0 + i,
                GL11.GL_TEXTURE_2D,
                textures[i].pid,
                0
            )
        }
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, globalReadFB.pid)
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, globalDrawFB.pid)
    }


    fun clear(colour: Vec4 = Vec4(0.0f, 0.0f, 0.0f, 1.0f), bitmask: Array<FbBitmask> = arrayOf(FbBitmask.COLOR_BUFFER_BIT, FbBitmask.DEPTH_BUFFER_BIT)) {
        val framebuffer = this
        val globalFB = Global.engine!!.wgl.currDrawFB
        val globalPid = globalFB.pid
        val isAlreadyBound = globalFB === framebuffer
        if (!isAlreadyBound) {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebuffer.pid)
        }
        GL11.glClearColor(colour.x, colour.y, colour.z, colour.w)
        var intBitmask = 0
        for (m in bitmask) {
            intBitmask = intBitmask or m.value
        }
        GL11.glClear(intBitmask)
        if (!isAlreadyBound) {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Global.engine!!.wgl.currDrawFB.pid)
        }
    }

    fun setUniformTextures(uniformName: String) {
        for (i in textures.indices) {
            val tex = textures[i]
            tex.setUniform(-1, uniformName + ('A'.toInt() + i).toChar())
        }
    }

    companion object {
        @JvmField
        val defaultFB = FB(0)
        @JvmStatic
        fun bind(target: Target, fb: FB) {
            if (target == Target.READ_FRAMEBUFFER) Global.engine!!.wgl.currReadFB = fb else if (target == Target.DRAW_FRAMEBUFFER) Global.engine!!.wgl.currDrawFB = fb else if (target == Target.FRAMEBUFFER) {
                Global.engine!!.wgl.currReadFB = fb
                Global.engine!!.wgl.currDrawFB = Global.engine!!.wgl.currReadFB
            }
            GL30.glBindFramebuffer(target.value, fb.pid)
            if (target == Target.DRAW_FRAMEBUFFER || target == Target.FRAMEBUFFER) {
                if (fb !== defaultFB) {
                    val texAttachmentsCnt = fb.textures.size
                    val attachments = IntArray(texAttachmentsCnt)
                    for (i in 0 until texAttachmentsCnt) {
                        attachments[i] = GL30.GL_COLOR_ATTACHMENT0 + i
                    }
                    GL20.glDrawBuffers(attachments)
                } else {
                    val attachments = intArrayOf(GL11.GL_BACK)
                    GL20.glDrawBuffers(attachments)
                }
            }
        }

        // TODO: add filtering maybe?
        @JvmStatic

        fun blit(
            read: FB = FB.defaultFB, write: FB = FB.defaultFB,
            bitmasks: Array<FbBitmask> = arrayOf(FbBitmask.COLOR_BUFFER_BIT)
        ) {

            val pidRead = read.pid
            val pidWrite = write.pid
            val screenRes = IVec2(Global.engine!!.resX, Global.engine!!.resY)
            val resRead: IVec2 = if (read === defaultFB) screenRes else read.textures[0].res
            val resWrite = if (write === defaultFB) screenRes else write.textures[0].res
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, pidRead)
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, pidWrite)
            GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0)
            GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0)
            val bitmask = FbBitmask.toGLEnum(bitmasks)
            GL30.glBlitFramebuffer(
                0, 0, resRead[0], resRead[1],
                0, 0, resWrite[0], resWrite[1],
                bitmask, GL11.GL_NEAREST
            )

            // Revert to revious framebuffers.
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, Global.engine!!.wgl.currReadFB.pid)
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, Global.engine!!.wgl.currDrawFB.pid)
        }
    }
}